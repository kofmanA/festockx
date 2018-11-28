package ca.qc.dawsoncollege.stockx.festockx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class ForexActivity extends MenuActivity {

    private JSONObject jobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forex);
    }

    //get amount entered -- show error message if button pressed but nothing selected or stuff missing
    //get from choice
    //get to choice
    //make appropriate call to api to get conversion information

    //calculate
    public double calculate (double from, double to){



        return from;
    }

    //display


    private class RetrieveExchangeRates extends AsyncTask<String,Void,String> {

        protected void onPostExecute(String result) {
            try {
                Log.d("result: ", result + "");
                jobj = new JSONObject(result);

                if(jobj.has("rates")){
                    JSONObject currenciesObject = jobj.getJSONObject("rates");
                    ArrayList<String> currenciesIterator = currenciesObject.keys();
                    String[] currencies = new String[currenciesObject.length()];

                    for(String currency : currenciesIterator){

                    }

                    Spinner from = (Spinner) findViewById(R.id.from);
                    Spinner to = (Spinner) findViewById(R.id.to);

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    spinner.setAdapter(spinnerArrayAdapter);


                }

                //Pass retreived ticker info objects into the adapter
                //tickerInfoList = (ListView) findViewById(R.id.allTickers);
                //tickerInfoList.setAdapter(new TickerInfoDisplayAdapter(ShowTickerInfoActivity.this, tickersInfo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... urls) {
            try {
                return callUrl(urls[0]);
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


    private void callUrl() throws IOException {
        URL url = new URL("https://api.exchangeratesapi.io/latest"); //HTTP/1.1
        HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");

        connection.connect();

        int response = connection.getResponseCode();
        if(response == HttpURLConnection.HTTP_OK){
            InputStream instream = connection.getInputStream();
            //read


            instream.close();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.error)
                    .setMessage(R.string.errmsg)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }

        connection.disconnect();

    }

}
