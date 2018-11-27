package ca.qc.dawsoncollege.stockx.festockx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends MenuActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /**
     * gives the id of the element that invoked this method to receive the name of the person "clicked"
     * and shows an appropriate alert containing the person's description
     * @param view
     */
    public void showDescription (View view){
        int id = 0;
        String name;
        String description;

        if(view instanceof ImageView || view instanceof TextView){
            id = view.getId();

        }

        description = getDescription(id);
        name = getName(id);

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setTitle(name)
                .setMessage(description)
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
     */
    private String getDescription (int id) {
        String description = "Problem getting description";
        switch(id){
            case R.id.zhiimg:
            case R.id.zhitv :
                return getString(R.string.zhides);
            case R.id.felimg:
            case R.id.feltv :
                return getString(R.string.feldes);
            case R.id.simonimg:
            case R.id.simontv :
                return getString(R.string.simondes);
            case R.id.aleximg:
            case R.id.alextv :
                return getString(R.string.alexdes);
        }
        return description;
    }

    /**
     * receives an id and returns the corresponding name of the person clicked
     * @param id
     * @return
     */
    private String getName(int id){
        String name = "Problem getting name";
        switch(id){
            case R.id.zhiimg:
            case R.id.zhitv :
                return "Zhijie Cao";
            case R.id.felimg:
            case R.id.feltv :
                return "Felicia Gorgacheva";
            case R.id.simonimg:
            case R.id.simontv :
                return "Simon Guevara";
            case R.id.aleximg:
            case R.id.alextv :
                return "Alex Kofman";
        }
        return name;
    }
}
