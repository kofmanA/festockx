package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

import ca.qc.dawsoncollege.stockx.festockx.Portfolio.PortfolioActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.*;
import android.support.v4.*;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.qc.dawsoncollege.stockx.festockx.R;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.NoteItemActivity;

import static android.support.v4.content.ContextCompat.getSystemService;

public class TickerInfoDisplayAdapter extends RecyclerView.Adapter<TickerInfoDisplayAdapter.Holder> {

    private String errorMsg = "";
    private String moneyLeft = "";
    private String JWTToken;
    private String stockName;
    private String numStocksToBuy;
    private JSONObject jsonObj;
    private JSONObject json;
    Context context;
    List<TickerStock> tickers;
    private final String TAG = "issue";
    private int BUFFER = 1024;


    public TickerInfoDisplayAdapter(Context context, List<TickerStock> tickers) {
        this.context = context;
        this.tickers = tickers;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ticker_info, parent, false);
        Holder holder = new Holder(view);

        view.setOnLongClickListener(v -> {

            LayoutInflater li = LayoutInflater.from(this.context);
            View promptsBuyStock = li.inflate(R.layout.prompt_buystock, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
            alertDialogBuilder.setView(promptsBuyStock);

            EditText userInput = (EditText) promptsBuyStock
                    .findViewById(R.id.editStockBuyAmount);
            final TextView buyStockPrompt = (TextView) promptsBuyStock.findViewById(R.id.buyStockPrompt);
            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(R.string.buyStock,
                            (dialog, id) -> {

                                //GET USER INFO
                                JSONObject authenticationJSON = new JSONObject();
                                try {
                                    authenticationJSON.put("email", "asd@asd.asd");
                                    authenticationJSON.put("password", "asdasdasd");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final HashMap<String, String> authData = new HashMap<>();
                                authData.put("url", "http://stockxportfolio.herokuapp.com/api/auth/login");
                                authData.put("method", "POST");
                                authData.put("data", authenticationJSON.toString());

                                ConnectivityManager connMgr = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

                                //LOGIN, retreive JWT Token
                                if (netInfo != null && netInfo.isConnected()) {
                                    new Request().execute(authData);
                                }

                                //Get number of stocks to buy and the name of the stock they want to purchase
                                numStocksToBuy = userInput.getText().toString();
                                stockName = holder.symbolName.getText().toString();

                                //Send Api request to our own API with numStocks and stockName
                                getCashLeft(parent,holder);
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        TickerStock tickerStats = tickers.get(position);
        Log.d(TAG, tickerStats.getTickerName());
        holder.symbolName.setText(tickerStats.getTickerName());
        holder.companyName.setText(tickerStats.getCompanyName());
        holder.currentPrice.setText(tickerStats.getPrice());
        holder.currency.setText(tickerStats.getCurrencyType());
        holder.stockExchange.setText(tickerStats.getStockExchange());
        holder.dayChange.setText(tickerStats.getDayChange());
        holder.changePct.setText(tickerStats.getChangePct());
    }

    @Override
    public int getItemCount() {
        return tickers.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        public Context context;
        TextView symbolName;
        TextView companyName;
        TextView currentPrice;
        TextView currency;
        TextView stockExchange;
        TextView dayChange;
        TextView changePct;

        public Holder(View itemView) {
            super(itemView);
            context = this.context;
            symbolName = (TextView) itemView.findViewById(R.id.symbolDisplay);
            companyName = (TextView) itemView.findViewById(R.id.nameDisplay);
            currentPrice = (TextView) itemView.findViewById(R.id.priceDisplay);
            currency = (TextView) itemView.findViewById(R.id.currencyDisplay);
            stockExchange = (TextView) itemView.findViewById(R.id.stockExchangeDisplay);
            dayChange = (TextView) itemView.findViewById(R.id.dayChangeDisplay);
            changePct = (TextView) itemView.findViewById(R.id.changePercentDisplay);
        }

        public Context getContext() {
            return this.context;
        }
    }

    public void getCashLeft(ViewGroup parent, Holder holder) {
        String loginUrl = "";

        String url = "http://stockxportfolio.herokuapp.com/api/api/buy?quantity=" + numStocksToBuy + "&name=" + stockName + "&bearer=" + JWTToken;
        JSONObject tickerQuantity = new JSONObject();
        try {
            tickerQuantity.put("quantity", numStocksToBuy);
            tickerQuantity.put("ticker",stockName);
        }catch(JSONException e){
            e.printStackTrace();
        }

        ConnectivityManager connMgr = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            new BuyStock().execute(tickerQuantity.toString());
        }
    }


    class BuyStock extends AsyncTask<String, Void, String> {

        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                jsonObj = new JSONObject(result);

                if (jsonObj.has("cashleft")) {
                    moneyLeft = jsonObj.getString("cashleft");
                    popUpMoneyDialog(moneyLeft,context);
                } else {
                    String error = jsonObj.getString("error");
                    CharSequence text = "ERROR: " + errorMsg;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    // i.putExtra("error",errorMsg);
                    // startActivity(i);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... jsonObjects){
            HttpURLConnection connection = null;
            InputStream instream = null;
            try {
                URL url = new URL("http://stockxportfolio.herokuapp.com/api/api/buy"); //HTTP/1.1
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setRequestProperty("Authorization", "Bearer " + JWTToken);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(jsonObjects[0].toString());
                    writer.flush();

                connection.connect();

                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    instream = connection.getInputStream();
                    return readIt(instream);
                } else {
                    this.cancel(true);
                    return "";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (instream != null && connection != null) {
                        instream.close();
                        connection.disconnect();
                    }
                }
                catch(Exception e){
                    Log.e("Error", "problem closing input stream or html connection");
                }
            }
            Log.d("NULL", "doInBackground: REACHED NULL");
            return "";
        }

        private void popUpMoneyDialog(String moneyLeft,Context context) {


            CoordinatorLayout coordinatorLayout =  ((Activity) context).findViewById(R.id.coordinatorLayoutTicker);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,  context.getString(R.string.balanceRemaining)+moneyLeft+context.getString(R.string.showPortfolio), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.showPortfolio, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     Intent i = new Intent(context, PortfolioActivity.class);
                     context.startActivity(i);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    public String readIt(InputStream is) throws IOException, UnsupportedEncodingException {
        int bytesRead;
        int totalRead = 0;
        byte[] buffer = new byte[BUFFER];
        //For data from the server
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

        //Collect data in our output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataOutputStream writer = new DataOutputStream(byteArrayOutputStream);

        while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
            writer.write(buffer);
            totalRead += bytesRead;
        }
        writer.flush();
        Log.d("Parsed info",new String(byteArrayOutputStream.toString()));
        return new String(byteArrayOutputStream.toString());
    }

    class Request extends AsyncTask<HashMap<String, String>, Void, String> {

        //Cache the JWT Token received from the login
        @Override
        protected void onPostExecute(String result){
            try {
                json = new JSONObject(result);
                JWTToken = json.getString("access_token");

            } catch (JSONException e) {
                e.printStackTrace();
            };
        }


        //Send the request to the login API to get the JWT token
        @Override
        protected String doInBackground(HashMap<String, String>... data) {
            HttpURLConnection connection = null;
            InputStream instream = null;
            try {
                URL url = new URL(data[0].get("url")); //HTTP/1.1
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod(data[0].get("method"));
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setRequestProperty("Authorization", "Bearer " + JWTToken);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data[0].get("data"));
                writer.flush();

                connection.connect();

                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    instream = connection.getInputStream();

                    return readIt(instream);
                } else {
                    this.cancel(true);
                    return "";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (instream != null && connection != null) {
                        instream.close();
                        connection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("Error", "problem closing input stream or html connection");
                }
            }
            return "";
        }
    }
}
