package ca.qc.dawsoncollege.stockx.festockx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class StockNumberSelectActivity extends AppCompatActivity {

    Spinner numberSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_number_select);

        Integer[] numTickers = new Integer[]{1,2,3,4,5};

        numberSpinner = (Spinner)findViewById(R.id.stock_number);

        ArrayAdapter <Integer> dataAdapter = new ArrayAdapter<Integer>( this,android.R.layout.simple_spinner_item,numTickers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSpinner.setAdapter(dataAdapter);

    }
}
