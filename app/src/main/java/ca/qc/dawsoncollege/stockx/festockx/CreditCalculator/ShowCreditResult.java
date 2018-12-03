package ca.qc.dawsoncollege.stockx.festockx.CreditCalculator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class ShowCreditResult extends AppCompatActivity {
    TextView result;
    TextView balanceLeft;
    TextView monthsLeft;
    Intent i;
    String isPayable;
    boolean canPay;

    /**Summary: get Intent and displays information based upon the fact that the debt is payable
     *
     * @param savedInstanceState
     * @author Simon Guevara-Ponce
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credit_result);
        result = (TextView)findViewById(R.id.creditResult);
         i = getIntent();

        //Check whether they can pay it off or not
        canPay = i.getBooleanExtra("canPay",false);
        if (canPay){
            TextView  monthsTV = (TextView)findViewById(R.id.months);
            TextView  yearsTV = (TextView)findViewById(R.id.years);
            monthsTV.setText(String.valueOf(i.getIntExtra("monthsToPay",0)));
            yearsTV.setText(String.valueOf(i.getDoubleExtra("yearsToPay",0)));
        }
        else{
            result.setText(R.string.Unpayable);
        }
    }

    /**Summary: Pops up dialog to look up a contact. Inflates a dialog from a layout. Sets
     * a positive and negative button to that dialog. If the send button is pressed, creates
     * a cursor for all the contacts. If the contact name is the same as the one inputed, creates
     * another cursor that looks for the email, if they have an email, calls sendEmail method
     * @param v
     * @author Simon Guevara-Ponce
     */
    public void sendEmailDialog(View v){

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
                            public void onClick(DialogInterface dialog, int id) {
                                String input = userInput.getText().toString();
                                Cursor cursor = getContentResolver()
                                        .query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);
                                boolean contactFound=false;
                                while (cursor.moveToNext()) {

                                    String contactname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    if (contactname.equalsIgnoreCase(input)) {
                                        contactFound=true;
                                        String contactId = cursor
                                                .getString(cursor
                                                .getColumnIndex(ContactsContract.Contacts._ID));

                                        Cursor emails = getContentResolver().query(
                                                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                                 + " = " + contactId, null, null);
                                        boolean hasEmail=false;
                                        while (emails.moveToNext()) {
                                            hasEmail=true;
                                            String emailAddress = emails.getString(emails
                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                            sendEmail(emailAddress);
                                        }
                                        if(!hasEmail){
                                            Toast.makeText(userInput.getContext(),R.string.noEmail,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                                if(!contactFound){
                                    Toast.makeText(userInput.getContext(),R.string.noContactFound,Toast.LENGTH_SHORT).show();
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

    /**Summary: Method that creates the email with all the details and launches activity.     *
     * @param destination
     * @author Simon Guevara-Ponce
     */
    public void sendEmail(String destination){
        Intent ie = new Intent(Intent.ACTION_SEND);

        double balance = i.getDoubleExtra("balance",0);
        double interest = i.getDoubleExtra("interest",0);
        int yearsVal = i.getIntExtra("years",1);
        int monthsToPay = i.getIntExtra("monthsToPay",0);
        double yearsToPay = i.getDoubleExtra("yearsToPay",0);
        double payment = i.getDoubleExtra("payment",0);
        String message = "For a debt of " + balance + "$, with " + interest*100 + "% interest, if you wanted to pay it under " +
                yearsVal+" years while paying "+payment+"$ every month";
        if (canPay) {
           message= message+", it would take you " + monthsToPay + " months to do it, or " + yearsToPay + " years.";
        }
        else{
            message= message + ". "+R.string.Unpayable;
        }
        ie.setType("message/rfc822");
        ie.putExtra(Intent.EXTRA_EMAIL  , new String[]{destination});
        ie.putExtra(Intent.EXTRA_SUBJECT, "Credit Calculator Results");
        ie.putExtra(Intent.EXTRA_TEXT   , message);
        try {
            startActivity(Intent.createChooser(ie, "Send mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.noEmailLauncher, Toast.LENGTH_SHORT).show();
        }
    }

}
