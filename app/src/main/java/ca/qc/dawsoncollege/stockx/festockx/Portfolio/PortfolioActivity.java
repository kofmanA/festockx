package ca.qc.dawsoncollege.stockx.festockx.Portfolio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import ca.qc.dawsoncollege.stockx.festockx.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

public class PortfolioActivity extends MenuActivity {
    private String JWTToken = null;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        final RecyclerView recyclerView = findViewById(R.id.ownedStockRecyclerView);

        SharedPreferences sharedPreferences = PortfolioActivity.this.getSharedPreferences("Settings", MODE_PRIVATE);
        String email = sharedPreferences.getString("eAddress", "none");
        String pwd = sharedPreferences.getString("password", "asdasdasd");

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


        final HashMap<String, String> allStocksData = new HashMap<>();
        allStocksData.put("url", "http://stockxportfolio.herokuapp.com/api/api/allstocks");
        allStocksData.put("method", "GET");

        new Request(){
            @Override
            protected void onPostExecute(String result){
                try {
                    Log.d("TEst", "onPostExecute: " + result);
                    JSONObject json = new JSONObject(result);
                    JWTToken = json.getString("access_token");
                    Log.d("TOKEN", "onPostExecute: " + JWTToken);
                    new Request(){
                        @Override
                        protected void onPostExecute(String result){
                            JSONArray json = null;
                            try {
                                HashMap<String, Integer> ownedStocks = new HashMap<>();
                                json = new JSONArray(result);
                                for(int i = 0; i < json.length(); i++){
                                    JSONObject object = (JSONObject) json.get(i);
                                    ownedStocks.put(object.getString("ticker"), object.getInt("quantity"));
                                }

                                recyclerView.setAdapter(new OwnedStockAdapter(PortfolioActivity.this, ownedStocks));
                                recyclerView.setLayoutManager(new LinearLayoutManager(PortfolioActivity.this));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                    }.execute(allStocksData);
                } catch (JSONException e) {
                    Toast.makeText(PortfolioActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(authData);
    }

    class Request extends AsyncTask<HashMap<String, String>, Void, String> {

        @Override
        protected String doInBackground(HashMap<String, String>... data) {
            HttpURLConnection connection = null;
            InputStream instream = null;
            try {
                if(data.length <= 0 || data[0] == null || !data[0].containsKey("url") || !data[0].containsKey("method") || (!data[0].containsKey("data") && JWTToken == null)){

                    this.cancel(true);
                    return "";
                }
                URL url = new URL(data[0].get("url")); //HTTP/1.1
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                Log.d("METHOD", "doInBackground: " + data[0].get("method"));
                connection.setRequestMethod(data[0].get("method"));
                connection.setRequestProperty("Content-Type", "application/json");

                if(JWTToken != null){
                    connection.setRequestProperty("Authorization", "Bearer " + JWTToken);
                } else {
                    Log.d("DATA", "doInBackground: " + data[0].get("data"));
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(data[0].get("data"));
                    writer.flush();
                }


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
                }
                catch(Exception e){
                    Log.e("Error", "problem closing input stream or html connection");
                }
            }
            Log.d("NULL", "doInBackground: REACHED NULL");
            return "";
        }

        /**
         *
         * @author Patricia Campbell
         * @param is
         * @return
         * @throws IOException
         * @throws UnsupportedEncodingException
         */
        public String readIt(InputStream is) throws IOException, UnsupportedEncodingException {
            final int BUFFER = 1024;
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

}
