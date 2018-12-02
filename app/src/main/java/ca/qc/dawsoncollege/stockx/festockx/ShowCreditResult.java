package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCreditResult extends Activity {
    TextView result;
    TextView balanceLeft;
    TextView monthsLeft;
    Intent i;
    String isPayable;
    boolean canPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credit_result);
        result = (TextView)findViewById(R.id.creditResult);
        //Set the result to determine whether it is payable or not
         i = getIntent();
        //Check whether they can pay it off or not
        canPay = i.getBooleanExtra("canPay",false);
        if (canPay){
            TextView  monthsTV = (TextView)findViewById(R.id.months);
            TextView  yearsTV = (TextView)findViewById(R.id.years);
            monthsTV.setText(String.valueOf(i.getIntExtra("monthsToPay",0)));
            yearsTV.setText(String.valueOf(i.getDoubleExtra("yearsToPay",0)).substring(0,5));
        }
        else{
            result.setText(R.string.Unpayable);
        }
    }

    public void sendEmailDialog(View v){
        final String[] editedNote = new String[1];

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_sendemail, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String input = userInput.getText().toString();
                                Cursor cursor = getContentResolver()
                                        .query(ContactsContract.Contacts.CONTENT_URI,null,null,null);
                                while(cursor.moveToNext()){
                                    String contactname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    if (contactname.equals(input)){
                                        String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                        sendEmail(email);
                                    }
                                }
                            }

                        })
                .setNegativeButton(R.string.No,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void sendEmail(String destination){
        Intent ie = new Intent(Intent.ACTION_SEND);

        int balance = i.getIntExtra("balance",0);
        int interest = i.getIntExtra("interest",0);
        int yearsVal = i.getIntExtra("years",1);
        int monthsToPay = i.getIntExtra("monthsToPay",0);
        int yearsToPay = i.getIntExtra("yearsToPay",0);
        String message = "For a debt of " + balance + "$, with " + interest + " interest, if you wanted to pay it under " +
                yearsVal;
        if (canPay) {
           message= message+", it would take you " + monthsToPay + "months to do it, or " + yearsToPay + " years.";
        }
        else{
            message= message + ", it would be impossible to pay it off in the proposed window of time.";
        }
        ie.setType("message/rfc822");
        ie.putExtra(Intent.EXTRA_EMAIL  , new String[]{destination});
        ie.putExtra(Intent.EXTRA_SUBJECT, "Credit Calculator Results");
        ie.putExtra(Intent.EXTRA_TEXT   , message);
        try {
            startActivity(Intent.createChooser(ie, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
