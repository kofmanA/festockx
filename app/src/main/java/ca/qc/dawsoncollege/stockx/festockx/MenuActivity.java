package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuActivity extends AppCompatActivity {

    /**
     * creates the options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * detects which element in the menu was clicked and performs the appropriate action for what what selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.dawson: //launch dawson web page
                Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dawsoncollege.qc.ca/computer-science-technology/"));
                startActivity(link);
                return true;
            case R.id.about: //start about activity
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;
            case R.id.settings: //start activity settings
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
