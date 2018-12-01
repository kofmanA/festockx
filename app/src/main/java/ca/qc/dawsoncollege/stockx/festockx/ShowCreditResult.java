package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowCreditResult extends Activity {
    TextView result;
    TextView balanceLeft;
    TextView monthsLeft;
    String isPayable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credit_result);
        result = (TextView)findViewById(R.id.creditResult);
        //Set the result to determine whether it is payable or not
        Intent i = getIntent();
        //Check whether they can pay it off or not
        boolean canPay = i.getBooleanExtra("canPay",false);
    }
}
