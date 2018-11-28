package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NoteItemActivity extends Activity {
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
