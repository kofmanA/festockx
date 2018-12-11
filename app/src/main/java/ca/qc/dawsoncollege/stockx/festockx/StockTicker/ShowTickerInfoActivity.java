package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ca.qc.dawsoncollege.stockx.festockx.Menu.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

public class ShowTickerInfoActivity extends MenuActivity {

    private JSONObject jsonObj;
    RecyclerView tickerInfoList;
    List<String> tickers;
    private int BUFFER = 1024;
    private String TAG = "Exception";
    List<TickerStock> tickersInfo = new ArrayList<TickerStock>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ticker_info);
        if(getIntent().hasExtra("tickers")){
            //Retreive tickers sent from the user input
            tickers = getIntent().getExtras().getStringArrayList("tickers");
            getAllStockInfo();
        }
    }

    public void getAllStockInfo(){
        String beginningUrl = "https://www.worldtradingdata.com/api/v1/stock?symbol=";
        /*Comma separated string of tickers for the URL.
         *Example: If the user entered AAPL and MSFT in their ticker boxes, the tickerCommaSeparated String would be AAPL,MSFT
         */
        String tickersCommaSeparated = "";
        for(int i = 0; i < tickers.size();i++){
            if(i == tickers.size() - 1){
                tickersCommaSeparated += tickers.get(i).toUpperCase();
            } else {
                tickersCommaSeparated += tickers.get(i).toUpperCase() + ",";
            }
        }
        Log.d("api tickers:",tickersCommaSeparated);
        //Concatenating the query token
        String endToken = "&api_token=bk3gi7ZJHWLyeG4QhI2ruwRqLsZCLhNCIpn0qYivknYwL5I3p8emaJD5ABwo";

        String fullUrl = beginningUrl + tickersCommaSeparated + endToken;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnected()){
            new DownloadStock().execute(fullUrl);
        }
    }


    private class DownloadStock extends AsyncTask<String,Void,String> {

        protected void onPostExecute(String result) {
            try {
                Log.d("result: ", result + "");
                jsonObj = new JSONObject(result);
                //data object indicates that querying the API with the ticker
                if(!jsonObj.has("data")){
                    //Create a ticker stock with all info indicating that it isn't a valid ticker
                    //If time, will find a way to costruct a better looking error message that the ticker is not found
                    TickerStock ts = new TickerStock();
                    ts.setTickerName("This ticker is not in the exchange");
                    ts.setCompanyName("This ticker is not in the exchange");
                    ts.setCurrencyType("This ticker is not in the exchange");
                    ts.setDayChange("This ticker is not in the exchange");
                    ts.setPrice("This ticker is not in the exchange");
                    ts.setStockExchange("This ticker is not in the exchange");
                    ts.setChangePct("This ticker is not in the exchange");
                    tickersInfo.add(ts);
                }
                else {
                    //All of the data of the stock ticker is contained in the data object in the API
                    JSONArray stockObject = jsonObj.getJSONArray("data");

                    String tickerName, companyName, currentPrice, currencyType, stockExchange, dayChange, changePct;
                    //Go through each ticker symbol
                    for (int i = 0; i < stockObject.length(); i++) {
                        JSONObject obj = stockObject.getJSONObject(i);

                        tickerName = obj.getString("symbol");
                        companyName = obj.getString("name");
                        currentPrice = obj.getString("price");
                        currencyType = obj.getString("currency");
                        stockExchange = obj.getString("stock_exchange_short");
                        dayChange = obj.getString("day_change");
                        changePct = obj.getString("change_pct");
                        //Create new TickerStock object
                        TickerStock ts = new TickerStock(tickerName, companyName, currentPrice, currencyType, stockExchange, dayChange, changePct);
                        tickersInfo.add(ts);
                    }
                }
                //Pass retreived ticker info objects into the adapter
                tickerInfoList = (RecyclerView) findViewById(R.id.allTickers);
                tickerInfoList.setAdapter(new TickerInfoDisplayAdapter(ShowTickerInfoActivity.this, tickersInfo));
                tickerInfoList.setLayoutManager(new LinearLayoutManager(ShowTickerInfoActivity.this));
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
