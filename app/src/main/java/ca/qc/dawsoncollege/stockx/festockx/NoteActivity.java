package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.SQLite.*;

public class NoteActivity extends Activity {
    private ItemNoteViewModel INVModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ItemNoteAdapter adapter = new ItemNoteAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        INVModel = ViewModelProviders.of(this).get(ItemNoteViewModel.class);
        INVModel.getAllNotes().observe(this, new Observer<List<ItemNote>>() {
            @Override
            public void onChanged(@Nullable List<ItemNote> itemNotes) {
                @Override
                        public void onChange(@Nullable final List<ItemNote> notes){
                    adapter.
                }
            }
        });
    }

}
