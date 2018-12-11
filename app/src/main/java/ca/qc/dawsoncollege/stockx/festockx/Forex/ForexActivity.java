package ca.qc.dawsoncollege.stockx.festockx.Forex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.Menu.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

import static android.content.ContentValues.TAG;

/**
 * @author Felicia
 */
public class ForexActivity extends MenuActivity {

    private JSONObject json;
    private String fromItem;
    private String toItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forex);

        //this call takes care of filling up the spinners, that's why it is in onCreate
        new RetrieveExchangeRates().execute("https://api.exchangeratesapi.io/latest");
    }

    /**
     * called when convert button is pressed. It collect the selected items as strings from the spinners and makes another call to the api but using the select FROM as a base, retrieves
     * TO value from the jason object with the new base and multiplies it by the amount to get the foreign currency amount.
     * Once calculated, the converted amount is  displayed on the ui
     * @param view
     */
    public void convert(View view) {
        fromItem = ((Spinner) findViewById(R.id.from)).getSelectedItem().toString();
        toItem = ((Spinner) findViewById(R.id.to)).getSelectedItem().toString();

        //anonymous AsyncTask takes care of retrieving the rate required, retreives the remaining needed views, converts the amount and displays it
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... url) {
                try {
                    return callUrl(url[0]);
                } catch (IOException e) {
                    Log.e(TAG, "exception thrown by download");
                    return "Unable to retrieve web page. URL may be invalid.";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    if (result == null) {
                        return;
                    }
                    Log.d("result: ", result + "");
                    json = new JSONObject(result);

                    if (json.has("rates")) {
                        double rate;
                        JSONObject currenciesObject = json.getJSONObject("rates");
                        rate = currenciesObject.getDouble(toItem);

                        double amountToConvert = Double.parseDouble(((EditText)findViewById(R.id.amount)).getText().toString());

                        double convertedAmount = Math.round(rate * amountToConvert * 100)/100;

                        TextView tv = (TextView)findViewById(R.id.converted);
                        tv.setText(""+convertedAmount + " " + toItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }.execute("https://api.exchangeratesapi.io/latest?base=" + fromItem);
    }

    /**
     * nested AsyncTaks takes care of retrieving the currencies available in the api and fills up two spinners on the ui to allow the user to choose from and to currencies
     */
    private class RetrieveExchangeRates extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            try {
                return callUrl(url[0]);
            } catch (IOException e) {
                Log.e(TAG, "exception thrown by download");
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        protected void onPostExecute(String result) {
            try {
                if (result == null) {
                    return;
                }
                Log.d("result: ", result + "");
                json = new JSONObject(result);

                if (json.has("rates")) {
                    JSONObject currenciesObject = json.getJSONObject("rates");
                    Iterator<String> currenciesIterator = currenciesObject.keys();
                    List<String> currencies = new ArrayList<>();

                    while (currenciesIterator.hasNext()) {
                        currencies.add(currenciesIterator.next());
                    }

                    Spinner from = (Spinner) findViewById(R.id.from);
                    Spinner to = (Spinner) findViewById(R.id.to);

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ForexActivity.this, android.R.layout.simple_spinner_item, currencies);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    from.setAdapter(spinnerArrayAdapter);
                    to.setAdapter(spinnerArrayAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * opens connections, gets an inputstream from the response and send it to readIt() to get the information needed form the resposne
     * finally closes connections when they've been opened
     *
     * @param urlParam
     * @return
     * @throws IOException
     */
    private String callUrl(String urlParam) throws IOException {
        HttpURLConnection connection = null;
        InputStream instream = null;

        try {
            URL url = new URL(urlParam); //HTTP/1.1
            connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                instream = connection.getInputStream();
                return readIt(instream);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

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


