package ca.qc.dawsoncollege.stockx.festockx.Menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ca.qc.dawsoncollege.stockx.festockx.Menu.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;


public class SettingsActivity extends MenuActivity {
    EditText fNameET;
    EditText lNameET;
    EditText eAddressET;
    EditText passwordET;
    Spinner pStockExSpin;
    Spinner currencySpin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fNameET = (EditText) findViewById(R.id.fnameTV);
        lNameET = (EditText) findViewById(R.id.lnameTV);
        eAddressET = (EditText) findViewById(R.id.eAddressTV);
        passwordET = (EditText) findViewById(R.id.PasswordTV);
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
            editor.putString("password", passwordET.getText().toString());
            editor.putString("pStockEx", pStockExSpin.getSelectedItem().toString());
            editor.putString("currency", currencySpin.getSelectedItem().toString());
            editor.putString("lastUpdated", new Date().toString());
            editor.commit();

            Toast.makeText(this,R.string.saved,Toast.LENGTH_SHORT).show();
            finish();
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
        if(prefs.contains("lastUpdated")){
            ((TextView) findViewById(R.id.lastUpdated)).setText(getString(R.string.lastUpdatedAt) + " " +  prefs.getString("lastUpdated", "ERROR"));
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

    /**Creates a dialog to confirm whether the user wants to go back.
     * The dialog is Yes/No. If yes, calls the private method that calls super
     * @author: Simon Guevara-Ponce
     */
    @Override
    public void onBackPressed()
    {
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }


     /**Calls super.onBackPressed. Checks if settings were saved at least once, if so, let them go back
      * @author: Simon Guevara-Ponce
     */
    private void goBack(){
        SharedPreferences prefs = this.getSharedPreferences(
                "Settings", MODE_PRIVATE);
        if(prefs.contains("fName")) {
            super.onBackPressed();
        }
        else{
            Toast.makeText(this,R.string.cantGoBack,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * stack management: close this activity when another is opened on top to avoid the stack become too big
     */
    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }

    public void clickRegister(View v){
        Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stockxportfolio.herokuapp.com/register"));
        startActivity(link);
    }

}
