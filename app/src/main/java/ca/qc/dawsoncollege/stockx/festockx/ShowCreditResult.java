package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.os.Bundle;

public class ShowCreditResult extends Activity {

    String isPayable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credit_result);

        //it is either a yes or a no
        String isPayable = getIntent().getStringExtra("payable");

    }
}
