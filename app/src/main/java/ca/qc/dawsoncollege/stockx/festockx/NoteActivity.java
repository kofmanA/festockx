package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import ca.qc.dawsoncollege.stockx.festockx.SQLite.*;

public class NoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ItemNoteAdapter adapter = new ItemNoteAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
