package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences prefs = this.getSharedPreferences(
                "lastQuestion", Context.MODE_PRIVATE);


    }
}
