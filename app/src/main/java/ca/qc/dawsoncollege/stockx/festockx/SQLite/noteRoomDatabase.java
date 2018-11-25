package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ItemNote.class},version = 1,exportSchema = false)
public abstract class noteRoomDatabase extends RoomDatabase {
    public abstract ItemNoteDAO itemNodeDAO();
    private static volatile noteRoomDatabase INSTANCE;


    static noteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (noteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            noteRoomDatabase.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
