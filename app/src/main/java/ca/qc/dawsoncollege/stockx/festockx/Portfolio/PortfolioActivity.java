package ca.qc.dawsoncollege.stockx.festockx.Portfolio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import ca.qc.dawsoncollege.stockx.festockx.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

public class PortfolioActivity extends MenuActivity {
    final String JWTToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        RecyclerView recyclerView = findViewById(R.id.ownedStockRecyclerView);
        //login
        Request request = new Request(){
            @Override
            protected void onPostExecute(String result){
                try {
                    JSONObject json = new JSONObject(result);

                } catch (JSONException e) {
                    Toast.makeText(PortfolioActivity.this, "Error parsin JSON", Toast.LENGTH_SHORT).show();
                }
            }

        };
        recyclerView.setAdapter(new OwnedStockAdapter(this, ownedStocks));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class Request extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            InputStream instream = null;
            try {
                URL url = new URL(strings[0]); //HTTP/1.1
                connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                if(JWTToken != null){
                    connection.setRequestProperty("Header", "Bearer " + JWTToken);
                }
                connection.setDoInput(true);
                connection.connect();

                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    instream = connection.getInputStream();
                    return readIt(instream);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PortfolioActivity.this);

                    builder.setTitle(R.string.error)
                            .setMessage(R.string.errmsg)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }
            catch (Exception e){
                Log.e("Error", e.toString());
            }
            finally {
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
            return null;
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
