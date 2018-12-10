package ca.qc.dawsoncollege.stockx.festockx.Portfolio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.ItemNoteAdapter;

public class PortfolioActivity extends MenuActivity implements ItemNoteAdapter.RecyclerViewClickListener {
    private String JWTToken = null;
    private OwnedStockAdapter adapt;
    private Context context;
    private TextView balance;
    private RecyclerView recyclerView;
    private HashMap<String, Integer> ownedStocks;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        this.context = this;
        balance = ((Activity) context).findViewById(R.id.balanceid);
        this.recyclerView = findViewById(R.id.ownedStockRecyclerView);

        SharedPreferences sharedPreferences = PortfolioActivity.this.getSharedPreferences("Settings", MODE_PRIVATE);
        String email = sharedPreferences.getString("eAddress", "none");
        String pwd = sharedPreferences.getString("password", "asdasdasd");

        JSONObject authenticationJSON = new JSONObject();

        SharedPreferences prefs = this.getSharedPreferences(
                "Settings", MODE_PRIVATE);
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

        final HashMap<String, String> allStocksData = new HashMap<>();
        allStocksData.put("url", "http://stockxportfolio.herokuapp.com/api/api/allstocks");
        allStocksData.put("method", "GET");

        final HashMap<String, String> balanceData = new HashMap<>();
        balanceData.put("url", "http://stockxportfolio.herokuapp.com/api/api/getCash");
        balanceData.put("method", "GET");

        new Request(){
            @Override
            protected void onPostExecute(String result){
                try {
                    JSONObject json = new JSONObject(result);
                    JWTToken = json.getString("access_token");
                    new Request(){
                        @Override
                        protected void onPostExecute(String result){
                            JSONArray json = null;
                            try {
                                ownedStocks = new HashMap<>();
                                json = new JSONArray(result);
                                for(int i = 0; i < json.length(); i++){
                                    JSONObject object = (JSONObject) json.get(i);
                                    ownedStocks.put(object.getString("ticker"), object.getInt("quantity"));
                                }


                                adapt = new OwnedStockAdapter(PortfolioActivity.this, ownedStocks);
                                recyclerView.setAdapter(adapt);
                                adapt.setRecyclerClick(PortfolioActivity.this);
                                recyclerView.setLayoutManager(new LinearLayoutManager(PortfolioActivity.this));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                    }.execute(allStocksData);

                    new Request(){
                        @Override
                        protected void onPostExecute(String result){
                            try {
                                Log.d("HELLO",result);
                                JSONObject json = new JSONObject(result);
                                Log.d("HELLO",json.keys().next().toString());
                                json = new JSONObject(result);
                                if (json.has("balance")) {
                                    String moneyLeft = json.getString("balance");
                                    balance.setText(balance.getText()+": "+moneyLeft+"$");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                    }.execute(balanceData);

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
                connection.setRequestMethod(data[0].get("method"));
                connection.setRequestProperty("Content-Type", "application/json");

                if(JWTToken != null){
                    connection.setRequestProperty("Authorization", "Bearer " + JWTToken);
                    if(data[0].get("data")!=null){
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

    /**Summary: Item click event.
     * When clicked, displays dialog with spinner asking the amount of stock they want to sell,
     * once the Sell button is pressed, calls the sellStock method     *
     * @param v View clicked
     * @param position: position of the stock picked
     * @author Simon Guevara-Ponce
     */
    @Override
    public void recyclerViewListClicked(View v, int position) {
        String[] data = adapt.getStock(position);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_sellstock, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

       Spinner amount = (Spinner) promptsView.findViewById(R.id.amountSell);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PortfolioActivity.this,android.R.layout.simple_spinner_dropdown_item, getRange(data[1]));
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        amount.setAdapter(adapter);
        amount.setPrompt(data[1]);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.sell,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String input = amount.getSelectedItem().toString();
                                sellStock(new String[] {data[0],input});
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(R.string.sellStocksTitle);
        alertDialog.show();
    }

    /**Summary: Private helper method.
     * Takes string, returns String[] of range from 1 to range     *
     * @param range
     * @return  String[]
     * @author Simon Guevara-Ponce
     */
    private String[] getRange(String range){
        int intrange = Integer.parseInt(range);
        ArrayList<String> rangeArr = new ArrayList<String>();
        for(int i=1;i<=intrange;i++){
            rangeArr.add(String.valueOf(i));
        }
        return rangeArr.toArray(new String[rangeArr.size()]);
    }

    /**Summary: Calls the Request class to make the async task with the proper data.
     * Then pops up a snackbar displaying balance.     *
     * @param data
     * @author Simon Guevara-Ponce
     */
    private void sellStock(String[] data){
        final HashMap<String, String> allStocksData = new HashMap<>();
        allStocksData.put("url", "http://stockxportfolio.herokuapp.com/api/api/sell");
        allStocksData.put("method", "POST");
        JSONObject tickerQuantity = new JSONObject();
        try {
            tickerQuantity.put("quantity", data[1]);
            tickerQuantity.put("ticker",data[0]);
        }catch(JSONException e){
            e.printStackTrace();
        }
        allStocksData.put("data",tickerQuantity.toString());

        new Request(){
            @Override
            protected void onPostExecute(String result){

                try {
                    JSONObject json = new JSONObject(result);
                    json = new JSONObject(result);
                    if (json.has("cashleft")) {
                       String moneyLeft = json.getString("cashleft");
                        CoordinatorLayout coordinatorLayout =  ((Activity) context).findViewById(R.id.coordinatorLayoutPortfolio);

                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout,  context.getString(R.string.balanceRemaining)+" "+moneyLeft, Snackbar.LENGTH_LONG);

                        snackbar.show();
                        //update balance
                        balance.setText(context.getString(R.string.balance) + ": " + moneyLeft);
                        //update map
                        //set adapter
                        int count = ownedStocks.remove(data[0]);
                        if(count - Integer.parseInt(data[1]) > 0) {
                            ownedStocks.put(data[0], count - Integer.parseInt(data[1]));
                        }
                        adapt = new OwnedStockAdapter(PortfolioActivity.this, ownedStocks);
                        recyclerView.setAdapter(adapt);
                        adapt.setRecyclerClick(PortfolioActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PortfolioActivity.this));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }.execute(allStocksData);
    }
}
