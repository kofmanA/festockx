package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

public class CreditCostActivity extends Activity {

    private JSONObject jsonObj;
    private static final String TAG = "HttpURLConn";
    Spinner numberSpinner;
    EditText balance;
    EditText rate;
    EditText payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_cost);

        Integer[] numYears = new Integer[]{1, 5, 10};

        numberSpinner = (Spinner) findViewById(R.id.year_number);
        balance = (EditText)findViewById(R.id.balanceInput);
        rate = (EditText)findViewById(R.id.interestRateInput);
        payment = (EditText)findViewById(R.id.monthlyPaymentInput);

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numYears);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberSpinner.setAdapter(dataAdapter);

        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int number = (int) numberSpinner.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Passes intent of a string arraylist containing all of the user's submitted tickers
     * This is to allow the ShowTickerInfo class to perform the API call
     *
     * @param v
     */
    public void calculate(View v) {
        Intent intent = new Intent(this, ShowCreditResult.class);
        double balanceVal = Double.parseDouble(balance.getText().toString());
        double paymentVal = Double.parseDouble(payment.getText().toString());
        double interestVal = Double.parseDouble(rate.getText().toString());
        int yearsVal = Integer.parseInt(numberSpinner.getSelectedItem().toString());
        int monthsVal = yearsVal * 12;
        int lastMonth = 0;
        for(int i = 0; i < monthsVal; i++){
            balanceVal *= 1 + interestVal / 100;
            balanceVal -= paymentVal;
            //Caches the month where it subtracted the payment
            lastMonth = i + 1;
            //It has been successfully payed off
            if(balanceVal <= 0)
                break;
        }
        boolean isPayable = false;
        if(balanceVal <= 0){
            int monthsToPay = lastMonth;
            //Calculate years it took to pay off
            double yearsToPay = ((lastMonth / 12.0) * 100.00) / 100.0;
            intent.putExtra("monthsToPay",monthsToPay);
            intent.putExtra("yearsToPay",yearsToPay);
            intent.putExtra("canPay",true);

            isPayable = true;
        }
        else if(balanceVal > 0){
            //Calculate years it took to pay off
            intent.putExtra("balanceLeft",balanceVal);
            intent.putExtra("canPay",false);
            isPayable = true;
        }
        intent.putExtra("balance",balanceVal);
        intent.putExtra("interest",interestVal);
        intent.putExtra("years",yearsVal);
        startActivity(intent);
    }
}
