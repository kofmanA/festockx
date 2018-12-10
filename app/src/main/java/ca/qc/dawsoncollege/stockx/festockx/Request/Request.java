package ca.qc.dawsoncollege.stockx.festockx.Request;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

/**
 * This is a request class for the Portfolio get and post requests, it takes a list of hashmap as a
 * parameter and implements doInBackground
 *
 * TO use it , you need to create an Anonymous Request method that overrides onPostExecute.
 */
public class Request extends AsyncTask<HashMap<String, String>, Void, String> {
    final String TAG = "REQUEST";
    @Override
    protected String doInBackground(HashMap<String, String>... data) {
        HttpURLConnection connection = null;
        InputStream instream = null;
        try {
            if (data.length <= 0 || data[0] == null || !data[0].containsKey("url") || !data[0].containsKey("method") || (!data[0].containsKey("data") && !data[0].containsKey("token"))) {
                this.cancel(true);
                Log.d(TAG, "doInBackground: Something went wrong" );
                return "";
            }
            URL url = new URL(data[0].get("url")); //HTTP/1.1
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(data[0].get("method"));
            connection.setRequestProperty("Content-Type", "application/json");

            if (data[0].containsKey("token")) {
                connection.setRequestProperty("Authorization", "Bearer " + data[0].get("token"));
                if (data[0].get("data") != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(data[0].get("data"));
                    writer.flush();
                }
            } else {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data[0].get("data"));
                writer.flush();
            }


            connection.connect();

            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground response: " + response);
            if (response == HttpURLConnection.HTTP_OK) {
                instream = connection.getInputStream();

                return readIt(instream);
            } else {

                return readIt(connection.getErrorStream());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (instream != null && connection != null) {
                    instream.close();
                    connection.disconnect();
                }
            } catch (Exception e) {
                Log.e(TAG, "problem closing input stream or html connection");
            }
        }
        Log.d(TAG, "doInBackground: REACHED NULL");
        return "";
    }

    /**
     * @param is
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @author Patricia Campbell
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