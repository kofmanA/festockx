package ca.qc.dawsoncollege.stockx.festockx.About;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class PersonFragment extends Fragment {

    public String name;
    private int fragPage;
    private Object[] info;
    private int id;

    /**
     * Method to instantiate a personFragment
     * @param page
     * @param id
     * @return
     */
    public static PersonFragment newInstance(int page, int id) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putInt(""+page, id);
        PersonFragment fragment = new PersonFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when the fragment is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragPage = getArguments().getInt("page");
        id = getArguments().getInt("" + fragPage);
    }

    /**
     * Called when the view is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        return view;
    }

    /**
     * Called when the view is created
     * @param view
     * @param bundle
     */
    @Override
    public void onViewCreated(View view, Bundle bundle){
        TextView tv = (TextView) view.findViewById(R.id.persontv);
        tv.setText(getString(id));

        ImageView iv = (ImageView) view.findViewById(R.id.personimg);
        iv.setImageDrawable(getImg(id));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescription(v);
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescription(v);
            }
        });

    }

    /**
     * gives the id of the element that invoked this method to receive the name of the person "clicked"
     * and shows an appropriate alert containing the person's description
     * @param view
     * @author Felicia Gorgacheva
     */
    public void showDescription (View view){

        String description;


        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle(getString(id))
                .setMessage(getDescription(id))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

    }

    /**
     * receives an id to know which element was clicked and returns the corresponding description string
     * @param id
     * @return
     * @author Felicia Gorgacheva
     */
    private String getDescription (int id) {
        String description = getString(R.string.probdes);
        switch(id){
            case R.string.zhijie:
                return getString(R.string.zhides);
            case R.string.felicia:
                return getString(R.string.feldes);
            case R.string.simon:
                return getString(R.string.simondes);
            case R.string.alex:
                return getString(R.string.alexdes);
        }
        return description;
    }

    /**
     * receives an id and returns the corresponding name of the person clicked
     * @param id
     * @return
     * @author Felicia Gorgacheva
     */
    private String getName(int id){
        String name = getString(R.string.probname);
        switch(id){
            case R.string.zhijie:
                return getString(R.string.zhijie);
            case R.string.felicia:
                return getString(R.string.felicia);
            case R.string.simon:
                return getString(R.string.simon);
            case R.string.alex:
                return getString(R.string.alex);
        }
        return name;
    }

    /**
     * Gets the image resource based on its id
     * @param id
     * @return
     * @author Felicia Gorgacheva
     */
    private Drawable getImg(int id){
        switch(id){
            case R.string.zhijie:
                return getResources().getDrawable(R.drawable.zhijie,null);
            case R.string.felicia:
                return getResources().getDrawable(R.drawable.felicia,null);
            case R.string.simon:
                return getResources().getDrawable(R.drawable.simon,null);
            case R.string.alex:
                return getResources().getDrawable(R.drawable.alex,null);
        }
        return null;
    }



}
