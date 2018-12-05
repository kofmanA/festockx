package ca.qc.dawsoncollege.stockx.festockx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Felicia
 */
public class AboutActivity extends MenuActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(new PersonFragmentPagerAdapter(getSupportFragmentManager(),
                AboutActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
    }

    /**
     * stack management: close this activity when another is opened on top to avoid the stack become too big
     */
    @Override
    public void onStop(){
        super.onStop();
        finish();
    }




}
