package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

import ca.qc.dawsoncollege.stockx.festockx.Portfolio.PortfolioActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

                                Log.d("Click works","click");
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
        /**
         * TODO:
         * Create second Async Class to log user in and receive bearer token
         * Send email and password in a json object
         * Get back the value from the access_token json object
         * If there is not a valid login, the json object is called error, so check for its existence
         */
        //Get token of logged in user from settings
        String loginUrl = "";

        //change to heroku URL for later
        String url = "http://stockxportfolio.herokuapp.com/api/api/buy?quantity=" + numStocksToBuy + "&name=" + stockName;

        ConnectivityManager connMgr = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            new BuyStock().execute(url);
        }
    }


    class BuyStock extends AsyncTask<String, Void, String> {

        protected void onPostExecute(String result) {
            try {
                Log.d("result: ", result + "");
                jsonObj = new JSONObject(result);
                // Intent i = new Intent(this, PortolioActivity.class);s
                if (jsonObj.has("cashleft")) {
                    moneyLeft = jsonObj.getString("cashleft");
                    //   i.putExtra("cashleft",moneyLeft);
                    popUpMoneyDialog(moneyLeft,context);
                } else if (jsonObj.has("error")) {
                    errorMsg = jsonObj.getString("error");
                    // i.putExtra("error",errorMsg);
                    // startActivity(i);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.e(TAG, "DOwnloading URL "+ urls[0]);
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "exception thrown by download");
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private void popUpMoneyDialog(String moneyLeft,Context context) {
//            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        //When positive button is clicked, pop the portfolio up with the passed intent data
//                        case DialogInterface.BUTTON_POSITIVE:
//                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//                            alertDialogBuilder
//                                    .setCancelable(false)
//                                    .setPositiveButton(R.string.showPortfolio,
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int id) {
//                                                    Intent i = new Intent(context, PortfolioActivity.class);
//                                                    //If there's an error, set an error intent, if not, set the intent of the cashleft of the user
//                                                    if(!moneyLeft.equals(""))
//                                                        i.putExtra("cashleft",moneyLeft);
//                                                    if(!errorMsg.equals(""))
//                                                        i.putExtra("error",errorMsg);
//                                                    //Pass the data from here to the portfolio activity
//                                                    ///WHY CANT I DO THIS
//                                                    context.startActivity(i);
//                                                }
//                                            })
//                                    .setNegativeButton(R.string.cancel,
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int id) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//                            AlertDialog alertDialog = alertDialogBuilder.create();
//                            alertDialog.show();
//                            //launch activity
//                            break;
//
//                        case DialogInterface.BUTTON_NEGATIVE:
//                            dialog.dismiss();
//                            break;
//                    }
//                }
//            };
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.moneyLeftDialogTitle).setMessage(R.string.moneyLeftDialogMessage + moneyLeft + "$." + R.string.moneyLeftDialogGoTo).setPositiveButton(R.string.Yes, this)
                            .setNegativeButton(R.string.No, this).show();
                }
            };
        }

    }

    private String downloadUrl(String myUrl) throws IOException {
        InputStream is = null;
        HttpURLConnection conn = null;
        URL url = new URL(myUrl);
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            //Allow input
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization:", "Bearer ");
            conn.connect();

            int response = conn.getResponseCode();
            if (response != HttpsURLConnection.HTTP_OK) {
                return "Server returned: " + response + " aborting read";
            }
            //The request is fine
            is = conn.getInputStream();
            return readIt(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception in bg");
            Log.getStackTraceString(e);
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
                if (conn != null)
                    try {
                        conn.disconnect();
                    } catch (IllegalStateException ignore) {
                    }
            }
        }
    }

    private String getBearerToken(String email, String pword) throws IOException {
        InputStream is = null;
        HttpURLConnection conn = null;
        URL url = new URL(email + pword);
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            //Allow input
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization:", "Bearer ");
            conn.connect();

            int response = conn.getResponseCode();
            if (response != HttpsURLConnection.HTTP_OK) {
                return "Server returned: " + response + " aborting read";
            }
            //The request is fine
            is = conn.getInputStream();
            return readIt(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception in bg");
            Log.getStackTraceString(e);
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
                if (conn != null)
                    try {
                        conn.disconnect();
                    } catch (IllegalStateException ignore) {
                    }
            }
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
        return new String(byteArrayOutputStream.toString());
    }

    //This class should take the email and password and return the token to class's JWTToken field
    class Request extends AsyncTask<HashMap<String, String>, Void, String> {

        //Cache the JWT Token received from the login
        @Override
        protected void onPostExecute(String result){
            try {
                Log.d("TEst", "onPostExecute: " + result);
                JSONObject json = new JSONObject(result);
                JWTToken = json.getString("access_token");
                Log.d("TOKEN", "onPostExecute: " + JWTToken);
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
                Log.d("METHOD", "doInBackground: " + data[0].get("method"));
                connection.setRequestMethod(data[0].get("method"));
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setRequestProperty("Authorization", "Bearer " + JWTToken);
                Log.d("DATA", "doInBackground: " + data[0].get("data"));
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data[0].get("data"));
                writer.flush();

                connection.connect();

                int response = connection.getResponseCode();
                Log.d("RESPONSE", "doInBackground: " + response);
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
            Log.d("NULL", "doInBackground: REACHED NULL");
            return "";
        }
    }
}
