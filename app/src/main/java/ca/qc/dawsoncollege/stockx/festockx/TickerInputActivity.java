package ca.qc.dawsoncollege.stockx.festockx;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class TickerInputActivity extends Activity {

    ListView tickerBoxes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_input);
        tickerBoxes = (ListView)findViewById(R.id.tickerBoxes);
    }

}
