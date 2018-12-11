package ca.qc.dawsoncollege.stockx.festockx.About;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class PersonFragmentPagerAdapter extends FragmentPagerAdapter{

    final int NUMBER_PAGES = 4;
    private int tabNames[] = new int[] { R.string.zhijie,  R.string.felicia, R.string.alex, R.string.simon };
    private Context context;

    /**
     * Constructor
     * @param fm
     * @param context
     */
    public PersonFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * Gets the count of the number of pages
     * @return
     * @author Felicia Gorgacheva
     */
    @Override
    public int getCount() {
        return NUMBER_PAGES;
    }

    /**
     * Gets the item at a position
     * @param position
     * @return
     * @author Felicia Gorgacheva
     */
    @Override
    public Fragment getItem(int position) {
        return PersonFragment.newInstance(position, tabNames[position]);
    }

    /**
     * Gets the title of tha page by its position
     * @param position
     * @return
     * @author Felicia Gorgacheva
     */
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return context.getString(tabNames[position]);
    }



}
