package ca.qc.dawsoncollege.stockx.festockx.Portfolio;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ca.qc.dawsoncollege.stockx.festockx.Request.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ca.qc.dawsoncollege.stockx.festockx.Menu.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.ItemNoteAdapter;

public class PortfolioActivity extends MenuActivity implements ItemNoteAdapter.RecyclerViewClickListener {
    private String JWTToken = null;
    private OwnedStockAdapter adapt;
    private Context context;
    private TextView balance;
    private RecyclerView recyclerView;
    private HashMap<String, Integer> ownedStocks;


    /**
     * Starts by quering the API for a token then populates the recycler view with the data.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        this.context = this;
        balance = ((Activity) context).findViewById(R.id.balanceid);
        this.recyclerView = findViewById(R.id.ownedStockRecyclerView);

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

        //Initial request to retrieve the token
        new Request(){
            @Override
            protected void onPostExecute(String result){
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.has("access_token")) {
                        JWTToken = json.getString("access_token");
                        allStocksData.put("token", JWTToken);
                        balanceData.put("token", JWTToken);
                        //This request gets all the owned stocks
                        new Request() {
                            @Override
                            protected void onPostExecute(String result) {
                                JSONArray json = null;
                                if (result != null && result.isEmpty() == false) {
                                    try {
                                        ownedStocks = new HashMap<>();
                                        json = new JSONArray(result);
                                        for (int i = 0; i < json.length(); i++) {
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
                                }
                            }

                            ;
                        }.execute(allStocksData);

                        //this request gets the balance
                        new Request() {
                            @Override
                            protected void onPostExecute(String result) {
                                try {

                                    JSONObject json = new JSONObject(result);
                                    json = new JSONObject(result);
                                    if (json.has("balance")) {
                                        String moneyLeft = json.getString("balance");
                                        balance.setText(balance.getText() + ": " + moneyLeft + "$");
                                    } else if (json.has("error")) {
                                        String errorMsg = json.getString("error");
                                        CharSequence text = "ERROR: " + errorMsg;

                                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            ;
                        }.execute(balanceData);
                    } else if(json.has("error")) {
                        String errorMsg = json.getString("error");
                        CharSequence text = "ERROR: " + errorMsg;

                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(PortfolioActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(authData);


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
        allStocksData.put("token", JWTToken);
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
                    } else if(json.has("error")) {
                        String errorMsg = json.getString("error");
                        CharSequence text = "ERROR: " + errorMsg;

                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
        }.execute(allStocksData);
    }
}
