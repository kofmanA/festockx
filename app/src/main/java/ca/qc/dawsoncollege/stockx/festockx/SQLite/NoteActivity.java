package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ca.qc.dawsoncollege.stockx.festockx.R;

public class NoteActivity extends AppCompatActivity implements ItemNoteAdapter.RecyclerViewClickListener{
    private ItemNoteViewModel INVModel;
    private CoordinatorLayout coordinatorLayout;
    private ItemNoteAdapter adapter;

    public static final int NEW_NOTE_ACTIVITY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        getSupportActionBar().setTitle(R.string.noteTitle);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new ItemNoteAdapter(this);
        adapter.setRecyclerClick(this);
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


    /**Summary: On click event for each note. Creates a dialog with the edit text. The Edit text is attached to a
     * positive and negative button. It calls the view model update note method with the new note String
     * if the save button is triggered.
     *
     * @param v
     * @param position: position of the note picked
     */
    @Override
    public void recyclerViewListClicked(View v, int position) {
        final String[] editedNote = new String[1];

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_updatenote, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(INVModel.getNote(position).getNote());
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                editedNote[0] = userInput.getText().toString();
                                final String[] data = {editedNote[0] + ';' + INVModel.getNote(position).getId()};
                                INVModel.updateNote(data[0].split(";"));
                                adapter.setNotes(INVModel.getAllNotes().getValue());
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**Summary: Once New Note Acticity is done, takes String and creates a new note.
     * If note was empty or back button was pressed, pops up toast.
     * @param requestCode
     * @param resultCode
     * @param i
     */
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == NEW_NOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ItemNote note = new ItemNote(i.getStringExtra(NewNoteActivity.EXTRA_REPLY));
            INVModel.insert(note);
        } else {
            Toast.makeText(this, R.string.noteNotSaved, Toast.LENGTH_SHORT).show();
        }
    }


    /**Summary: Launches new Note activity for result.
     * @param v
     * @author Simon Guevara-Ponce
     */
    public void addNote(View v) {
        Intent intent = new Intent(NoteActivity.this, NewNoteActivity.class);
        startActivityForResult(intent, NEW_NOTE_ACTIVITY_REQUEST_CODE);
    }


    /**Summary: Everything that has to do with the Swipe delete,
     *
     */
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }


        /**Summary: Once swipped, deletes note in the list and in the database. Before deleting
         * it saves the ItemNote in case Undo is pressed.
         * @param viewHolder
         * @param i
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final ItemNote noteBackup =  INVModel.getNote(viewHolder.getAdapterPosition());
            INVModel.delete(viewHolder.getAdapterPosition());
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,  noteBackup.getNote() + R.string.noteDeleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.Undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    INVModel.insert(new ItemNote(noteBackup.getNote()));
                    adapter.restoreItem(noteBackup, noteBackup.getId());
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    };


}
