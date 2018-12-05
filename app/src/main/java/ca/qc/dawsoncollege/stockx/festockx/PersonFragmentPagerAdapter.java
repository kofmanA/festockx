package ca.qc.dawsoncollege.stockx.festockx;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PersonFragmentPagerAdapter extends FragmentPagerAdapter{

    final int NUMBER_PAGES = 4;
    private int tabNames[] = new int[] { R.string.zhijie,  R.string.felicia, R.string.alex, R.string.simon };
    private Context context;

    public PersonFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return NUMBER_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        return PersonFragment.newInstance(position, tabNames[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return context.getString(tabNames[position]);
    }



}
