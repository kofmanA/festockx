package ca.qc.dawsoncollege.stockx.festockx;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.SQLite.*;

public class NoteActivity extends AppCompatActivity  {
    private ItemNoteViewModel INVModel;
    private CoordinatorLayout coordinatorLayout;
    private ItemNoteAdapter adapter;

    public static final int NEW_NOTE_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
         adapter = new ItemNoteAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        INVModel = ViewModelProviders.of(this).get(ItemNoteViewModel.class);
        INVModel.getAllNotes().observe(this, new Observer<List<ItemNote>>() {
            @Override
            public void onChanged(@Nullable List<ItemNote> itemNotes) {
                adapter.setNotes(itemNotes);
            }
        });
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == NEW_NOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ItemNote note = new ItemNote(i.getStringExtra(NewNoteActivity.EXTRA_REPLY));
            INVModel.insert(note);
        } else {
            Toast.makeText(this, R.string.noteNotSaved, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Creates a shared preferences editor and saves all the changes.
     * First, checks if email entered is valid, if it's not, pops up a toast.
     *
     * @param v
     * @author Simon Guevara-Ponce
     */
    public void addNote(View v) {
        Intent intent = new Intent(NoteActivity.this, NewNoteActivity.class);
        startActivityForResult(intent, NEW_NOTE_ACTIVITY_REQUEST_CODE);
    }



    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final int noteid = viewHolder.getAdapterPosition();
            final ItemNote noteBackup =  INVModel.getNote(viewHolder.getAdapterPosition());
            INVModel.delete(viewHolder.getAdapterPosition());

            final boolean[] undo = new boolean[1];
            undo[0] =false;
                    // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,  noteBackup.getNote() + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    undo[0] = true;
                    INVModel.insert(new ItemNote(noteBackup.getNote()));
                    adapter.restoreItem(noteBackup, noteid);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);

            snackbar.addCallback(new Snackbar.Callback() {

                                     @Override
                                     public void onDismissed(Snackbar snackbar, int event) {
                                         Log.i("HELLO","HIT ");
                                         if(!undo[0]) {
                                             Log.i("HELLO"," " + noteid);

                                         }
                                     }
                                 });

            snackbar.show();

        }
    };
}
