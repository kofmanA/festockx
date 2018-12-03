package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class StockNumberSelectActivity extends Activity {

    private JSONObject jsonObj;
    private static final String TAG = "HttpURLConn";
    Spinner numberSpinner;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_number_select);

        Integer[] numTickers = new Integer[]{1, 2, 3, 4, 5};

        numberSpinner = (Spinner) findViewById(R.id.stock_number);

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numTickers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSpinner.setAdapter(dataAdapter);

        numberSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                list = (ListView) findViewById(R.id.tickerBoxes);
                int number = (int) numberSpinner.getSelectedItem();
                list.setAdapter(new TickerNumberAdapter(StockNumberSelectActivity.this, number));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Passes intent of a string arraylist containing all of the user's submitted tickers
     * This is to allow the ShowTickerInfo class to perform the API call
     * @param v
     */
    public void submit(View v){
        //Submit strings of all text fields
        List<String> tickers = getListOfTickers();
        Intent i = new Intent(this, ShowTickerInfoActivity.class);
        i.putStringArrayListExtra("tickers", (ArrayList<String>) tickers);
        startActivity(i);
    }

    /**
     * Returns a list of all the strings retreived from the inputted text
     * Takes the linearLayout of each input field, retreives the input firld and puts it to a list of tickers
     * @return
     */
    public List<String> getListOfTickers(){
        List<String> tickers = new ArrayList<String>();

        for (int i = 0; i < list.getChildCount(); i++) {
            if (list.getChildAt(i) instanceof LinearLayout) {
                LinearLayout child = (LinearLayout) list.getChildAt(i);
                EditText content = (EditText) child.getChildAt(0);
                tickers.add(content.getText().toString());
            }

        }
        Log.d("tickers",tickers.toString());
        return tickers;
    }



}
