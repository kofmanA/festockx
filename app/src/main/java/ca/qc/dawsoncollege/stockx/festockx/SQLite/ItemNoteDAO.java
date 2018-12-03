package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemNoteDAO {
    @Insert
     void insert(ItemNote itemNode);

    @Delete
    void delete(ItemNote itemNote);

    @Query("DELETE FROM itemNote_table")
    void deleteAll();

    @Query("SELECT * FROM itemNote_table")
    LiveData<List<ItemNote>> getAllNotes();

    @Query("UPDATE itemNote_table SET note = :message WHERE id = :id")
    public void updateNote(int id, String message);

}
