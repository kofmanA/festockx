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
import ca.qc.dawsoncollege.stockx.festockx.Request.Request;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.NoteItemActivity;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.ContextCompat.getSystemService;

public class TickerInfoDisplayAdapter extends RecyclerView.Adapter<TickerInfoDisplayAdapter.Holder> {
    private String moneyLeft = "";
    private String JWTToken;
    private String stockName;
    private String numStocksToBuy;
    private JSONObject jsonObj;
    private JSONObject json;
    Context context;
    List<TickerStock> tickers;
    private final String TAG = "issue";


    public TickerInfoDisplayAdapter(Context context, List<TickerStock> tickers) {
        this.context = context;
        this.tickers = tickers;
    }

    /**
     * Creates dialog to buy the stock being long pressed
     * Also calls the method which will query our API to buy a stock
     * @param parent
     * @param viewType
     * @return
     * @author Alex and Zhijie
     */
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
                                SharedPreferences prefs = context.getSharedPreferences(
                                        "Settings", MODE_PRIVATE);
                                JSONObject authenticationJSON = new JSONObject();
                                try {
                                    authenticationJSON.put("email", prefs.getString("eAddress",null));
                                    authenticationJSON.put("password", prefs.getString("password",null));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final HashMap<String, String> authData = new HashMap<>();
                                authData.put("url", "http://stockxportfolio.herokuapp.com/api/auth/login");
                                authData.put("method", "POST");
                                authData.put("data", authenticationJSON.toString());
                                authData.put("token", JWTToken);
                                ConnectivityManager connMgr = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

                                //LOGIN, retreive JWT Token
                                if (netInfo != null && netInfo.isConnected()) {
                                    new Request(){
                                        @Override
                                        protected void onPostExecute(String result){
                                            try {
                                                json = new JSONObject(result);
                                                if(json.has("access_token")) {
                                                    JWTToken = json.getString("access_token");
                                                    //Get number of stocks to buy and the name of the stock they want to purchase
                                                    numStocksToBuy = userInput.getText().toString();
                                                    stockName = holder.symbolName.getText().toString();
                                                } else if(json.has("error")) {
                                                    String errorMsg = json.getString("error");
                                                    CharSequence text = "ERROR: " + errorMsg;

                                                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                                //Send Api request to our own API with numStocks and stockName
                                                getCashLeft(parent,holder);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            };
                                        }
                                    }.execute(authData);
                                }


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

    /**
     * Binds view with all of the ticker's information
     * @param holder
     * @param position
     * @author Alex
     */
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

    /**
     * Holder contains information to put inside of an individual view holder
     * @author Alex
     */
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

    /**
     * Performs the API call to retreive the amount of cash the user has remaining in their balance
     * @param parent
     * @param holder
     * @author Alex and Zhijie
     */
    public void getCashLeft(ViewGroup parent, Holder holder) {
        final JSONObject tickerQuantity = new JSONObject();
        try {
            tickerQuantity.put("quantity", numStocksToBuy);
            tickerQuantity.put("ticker",stockName);
        }catch(JSONException e){
            e.printStackTrace();
        }

        ConnectivityManager connMgr = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            final HashMap<String, String> buyData = new HashMap<>();
            buyData.put("url", "http://stockxportfolio.herokuapp.com/api/api/buy");
            buyData.put("method", "POST");
            buyData.put("data", tickerQuantity.toString());
            buyData.put("token", JWTToken);
            new Request(){
                @Override
                protected void onPostExecute(String result) {
                    try {

                        Log.d(TAG, "onPostExecute: " + result);
                        json = new JSONObject(result);
                        if (json.has("cashleft")) {
                            moneyLeft = json.getString("cashleft");
                            popUpMoneyDialog(moneyLeft,context,parent.getContext());
                        } else if(json.has("error")) {
                            String errorMsg = json.getString("error");
                            CharSequence text = "ERROR: " + errorMsg;

                            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute(buyData);
        }
    }

    /**Summary: Displays snackbar after purchase is done
     *
     * @param moneyLeft
     * @param context
     * @author Simon Guevara-Ponce
     */
    private void popUpMoneyDialog(String moneyLeft,Context context, Context parentContext) {
        CoordinatorLayout coordinatorLayout =  ((Activity) parentContext).findViewById(R.id.coordinatorLayoutTicker);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout,  context.getString(R.string.balanceRemaining)+" " +moneyLeft, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.showPortfolio, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PortfolioActivity.class);
                context.startActivity(i);
            }
        });
        snackbar.setActionTextColor(context.getResources().getColor(R.color.accent6));
        snackbar.show();
    }
}
