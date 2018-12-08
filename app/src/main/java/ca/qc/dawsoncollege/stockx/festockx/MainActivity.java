package ca.qc.dawsoncollege.stockx.festockx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import ca.qc.dawsoncollege.stockx.festockx.CreditCalculator.CreditCostActivity;
import ca.qc.dawsoncollege.stockx.festockx.Portfolio.PortfolioActivity;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.NoteActivity;
import ca.qc.dawsoncollege.stockx.festockx.StockTicker.StockNumberSelectActivity;

public class MainActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = this.getSharedPreferences(
                "Settings", MODE_PRIVATE);
        if(!prefs.contains("fName")) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    public void launchHints(View v){
        Intent intent = new Intent(this, HintsActivity.class);
        startActivity(intent);
    }

    public void launchNotes(View v){
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    public void launchCreditCalculator(View v){
        Intent intent = new Intent(this, CreditCostActivity.class);
        startActivity(intent);
    }

    public void launchStockQuotes(View v){
        Intent intent = new Intent(this, StockNumberSelectActivity.class);
        startActivity(intent);
    }

    public void launchForEx(View v){
        Intent intent = new Intent(this, ForexActivity.class);
        startActivity(intent);
    }

    public void launchPortfolio(View v){
        Intent intent = new Intent(this, PortfolioActivity.class);
        startActivity(intent);
    }

}
