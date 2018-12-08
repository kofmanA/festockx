package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ca.qc.dawsoncollege.stockx.festockx.MenuActivity;
import ca.qc.dawsoncollege.stockx.festockx.R;

public class NoteItemActivity extends MenuActivity {

    /**Summary: Activit that displays the full note     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noteitem);
        Intent intent = getIntent();
        String message = intent.getStringExtra("note");
        TextView TV = findViewById(R.id.itemNoteTV);
        TV.setText(message);
    }

}
