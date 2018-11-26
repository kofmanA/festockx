package ca.qc.dawsoncollege.stockx.festockx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class TickerInfoActivity extends AppCompatActivity {

    ListView allTickers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_info);
        allTickers = (ListView) findViewById(R.id.allTickers);
    }
}
