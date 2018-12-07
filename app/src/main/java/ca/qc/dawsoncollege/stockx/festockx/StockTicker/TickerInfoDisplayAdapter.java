package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

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
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.qc.dawsoncollege.stockx.festockx.R;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.NoteItemActivity;

import static android.support.v4.content.ContextCompat.getSystemService;

public class TickerInfoDisplayAdapter extends RecyclerView.Adapter<TickerInfoDisplayAdapter.Holder> {
    private String stockName;
    private String numStocksToBuy;
    private JSONObject jsonObj;
    Context context;
    List<TickerStock> tickers;
    private final String TAG = "call";
    private int BUFFER = 1024;


    public TickerInfoDisplayAdapter(Context context, List<TickerStock> tickers){
        this.context = context;
        this.tickers = tickers;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ticker_info,parent,false);
        Holder holder = new Holder(view);

        view.setOnLongClickListener(v -> {

            LayoutInflater li = LayoutInflater.from(this.context);
            View promptsBuyStock = li.inflate(R.layout.prompt_buystock, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
            alertDialogBuilder.setView(promptsBuyStock);

            final EditText userInput = (EditText) promptsBuyStock
                    .findViewById(R.id.editTextDialogUserInput);
            final TextView buyStockPrompt = (TextView) promptsBuyStock.findViewById(R.id.buyStockPrompt);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(R.string.buyStock,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    //Get number of stocks to buy and the name of the stock they want to purchase
                                    numStocksToBuy = userInput.getText().toString();
                                    stockName = holder.symbolName.getText().toString();
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
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
        Log.d(TAG,tickerStats.getTickerName());
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





    public class Holder extends RecyclerView.ViewHolder {
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

        public Context getContext(){
            return this.context;
        }
    }

    public void getCashLeft(Holder holder){
        /**
         * TODO:
         * Create second Async Class to log user in and receive bearer token
         * Send email and password in a json object
         * Get back the value from the access_token json object
         * If there is not a valid login, the json object is called error, so check for its existence
         */
        //Get token of logged in user from settings
        String loginUrl = "";


        String bearerToken = "bk3gi7ZJHWLyeG4QhI2ruwRqLsZCLhNCIpn0qYivknYwL5I3p8emaJD5ABwo";

        //change to heroku URL for later
        String url = "https://ass3.test/api/api/buy?api_token=" + bearerToken + "&quantity=" + numStocksToBuy + "&name=" + stockName ;

        ConnectivityManager connMgr = (ConnectivityManager) holder.getContext().getSystemService(holder.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
           new BuyStock().execute(url);
        }
    }


    private class BuyStock extends AsyncTask<String,Void,String> {

        protected void onPostExecute(String result) {
            try {
                Log.d("result: ", result + "");
                jsonObj = new JSONObject(result);
               // Intent i = new Intent(this, PortolioActivity.class);
                if(jsonObj.has("cashleft")){
                    String moneyLeft = jsonObj.getString("cashleft");
                //   i.putExtra("cashleft",moneyLeft);
                     popUpMoneyDialog(moneyLeft);
                }
                else if(jsonObj.has("error")){
                    String errorMsg = jsonObj.getString("error");
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
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "exception thrown by download" );
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private void  popUpMoneyDialog(String moneyLeft){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //launch activity
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.moneyLeftDialogTitle).setMessage(R.string.moneyLeftDialogMessage+moneyLeft+"$."+R.string.moneyLeftDialogGoTo).setPositiveButton(R.string.Yes, dialogClickListener)
                    .setNegativeButton(R.string.No, dialogClickListener).show();

        }

    }

    private String downloadUrl(String myUrl) throws IOException {
        InputStream is = null;
        HttpURLConnection conn = null;
        URL url = new URL(myUrl);
        try {
            conn = (HttpURLConnection) url.openConnection() ;
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            //Allow input
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization:","Bearer ");
            conn.connect();

            int response = conn.getResponseCode();
            if(response != HttpsURLConnection.HTTP_OK){
                return "Server returned: " + response + " aborting read";
            }
            //The request is fine
            is = conn.getInputStream();
            return readIt(is);
        } catch(IOException e) {
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
                    } catch (IllegalStateException ignore ) {
                    }
            }
        }
    }

    private String getBearerToken(String email, String pword) throws IOException {
        InputStream is = null;
        HttpURLConnection conn = null;
        URL url = new URL(email+pword);
        try {
            conn = (HttpURLConnection) url.openConnection() ;
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            //Allow input
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization:","Bearer ");
            conn.connect();

            int response = conn.getResponseCode();
            if(response != HttpsURLConnection.HTTP_OK){
                return "Server returned: " + response + " aborting read";
            }
            //The request is fine
            is = conn.getInputStream();
            return readIt(is);
        } catch(IOException e) {
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
                    } catch (IllegalStateException ignore ) {
                    }
            }
        }
    }

    public String readIt(InputStream is) throws IOException,UnsupportedEncodingException {
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
}
