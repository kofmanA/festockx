package ca.qc.dawsoncollege.stockx.festockx.StockTicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import ca.qc.dawsoncollege.stockx.festockx.R;

/**
 * Contains list view being all of the input boxes that are used for inputting a ticker
 * Note: Discussed with tricia saying that I was allowed to use ListView in this instance
 * @author Alex
 */
public class TickerInfoActivity extends AppCompatActivity {

    ListView allTickers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_info);
        allTickers = (ListView) findViewById(R.id.allTickers);
    }
}
