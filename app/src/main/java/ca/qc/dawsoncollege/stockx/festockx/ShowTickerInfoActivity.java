package ca.qc.dawsoncollege.stockx.festockx;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

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

public class ShowTickerInfoActivity extends Activity {

    private JSONObject jsonObj;
    ListView tickerInfoList;
    List<String> tickers;
    private int BUFFER = 1024;
    private String TAG = "Exception";
    List<TickerStock> tickersInfo = new ArrayList<TickerStock>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ticker_info);
        if(getIntent().hasExtra("tickers")){
            tickers = getIntent().getExtras().getStringArrayList("tickers");
            Log.d("TICKERS:",tickers.toString());
            getAllStockInfo();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllStockInfo(){
        String beginningUrl = "https://www.worldtradingdata.com/api/v1/stock?symbol=";
        //Comma separated string of tickers for the URL
        String tickersCommaSeparated = String.join(",", tickers).toUpperCase();
        String endUrl = "&api_token=bk3gi7ZJHWLyeG4QhI2ruwRqLsZCLhNCIpn0qYivknYwL5I3p8emaJD5ABwo";

        String fullUrl = beginningUrl + tickersCommaSeparated + endUrl;
        Log.d("fullUrl: ",fullUrl + "");
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
                JSONArray stockObject = jsonObj.getJSONArray("data");

                String tickerName, companyName, currentPrice, currencyType, stockExchange, dayChange, changePct;
                for(int i = 0 ; i < stockObject.length(); i++){
                    JSONObject obj = stockObject.getJSONObject(i);
                    tickerName = obj.getString("symbol");
                    companyName = obj.getString("name");
                    currentPrice = obj.getString("price");
                    currencyType = obj.getString("currency");
                    stockExchange = obj.getString("stock_exchange_short");
                    dayChange = obj.getString("day_change");
                    changePct = obj.getString("change_pct");
                    TickerStock ts = new TickerStock(tickerName,companyName,currentPrice,currencyType,stockExchange,dayChange,changePct);
                    tickersInfo.add(ts);
                }
                //Adapter
                tickerInfoList = (ListView) findViewById(R.id.allTickers);
                tickerInfoList.setAdapter(new TickerInfoDisplayAdapter(ShowTickerInfoActivity.this, tickersInfo));
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
