package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends Activity {
    EditText fNameET;
    EditText lNameET;
    EditText eAddressET;
    Spinner pStockExSpin;
    Spinner currencySpin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fNameET = (EditText) findViewById(R.id.fnameTV);
        lNameET = (EditText) findViewById(R.id.lnameTV);
        eAddressET = (EditText) findViewById(R.id.eAddressTV);
        pStockExSpin = (Spinner) findViewById(R.id.pStockEx);
        currencySpin = (Spinner) findViewById(R.id.curr);
        showSettings();
    }

    /**Creates a shared preferences editor and saves all the changes.
     * First, checks if email entered is valid, if it's not, pops up a toast.
     * @param v
     * @author Simon Guevara-Ponce
     */
    public void saveSettings(View v){
        SharedPreferences preferences = this.getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(isEmailValid(eAddressET.getText().toString())){
            editor.putString("fName", fNameET.getText().toString());
            editor.putString("lName", lNameET.getText().toString());
            editor.putString("eAddress", eAddressET.getText().toString());
            editor.putString("pStockEx", pStockExSpin.getSelectedItem().toString());
            editor.putString("currency", currencySpin.getSelectedItem().toString());
            editor.putString("lastUpdated",new Date().toString());
            editor.commit();

            Toast.makeText(this,R.string.saved,Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this,R.string.invalidEmail,Toast.LENGTH_LONG).show();
        }
        //Redirect them to MainApp
    }

    /**Checks if settings already exist. If they do, shows them in the accord EditText
     * @author: Simon Guevara-Ponce
     */
    public void showSettings(){
        SharedPreferences prefs = this.getSharedPreferences(
                "Settings", MODE_PRIVATE);
        if(prefs.contains("fName")){
            Log.i("HELLO","saved");
            fNameET.setText(prefs.getString("fName","ERROR"));
        }
        if(prefs.contains("lName")){
            lNameET.setText(prefs.getString("lName","ERROR"));
        }
        if(prefs.contains("eAddress")){
            eAddressET.setText(prefs.getString("eAddress","ERROR"));
        }
        if(prefs.contains("pStockEx")){
            pStockExSpin.setPrompt(prefs.getString("pStockEx","ERROR"));
        }
        if(prefs.contains("currency")){
            currencySpin.setPrompt(prefs.getString("currecy","ERROR"));
        }
    }

    /**Uses Patterns and Matchers to check if the email is valid     *
     * @param email
     * @return Boolean of its validity
     * @author Simon Guevara-Ponce
     */
    private boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed()
    {
        final Boolean cancelled;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.backSettingContent)
                .setTitle(R.string.backSettingTitle);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goBack();
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // code here to show dialog
        // optional depending on your needs
        // User clicked Yes button

    }

    public void goBack(){
        super.onBackPressed();
    }

}
