package ca.qc.dawsoncollege.stockx.festockx;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.SQLite.*;

public class NoteActivity extends AppCompatActivity {
    private ItemNoteViewModel INVModel;
    public static final int NEW_NOTE_ACTIVITY_REQUEST_CODE = 1;
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
                    adapter.setNotes(itemNotes);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent i){
        super.onActivityResult(requestCode,resultCode,i);
        if (requestCode == NEW_NOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            ItemNote note = new ItemNote(i.getStringExtra(NewNoteActivity.EXTRA_REPLY));
            INVModel.insert(note);
        }
        else
        {
            Toast.makeText(this, R.string.noteNotSaved,Toast.LENGTH_SHORT).show();
        }
    }


    /**Creates a shared preferences editor and saves all the changes.
     * First, checks if email entered is valid, if it's not, pops up a toast.
     * @param v
     * @author Simon Guevara-Ponce
     */
    public void addNote(View v){
        Intent intent = new Intent(NoteActivity.this, NewNoteActivity.class);
        startActivityForResult(intent, NEW_NOTE_ACTIVITY_REQUEST_CODE);
    }

}
