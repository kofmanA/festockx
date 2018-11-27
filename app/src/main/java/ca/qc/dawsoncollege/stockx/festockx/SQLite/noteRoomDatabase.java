package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {ItemNote.class},version = 1,exportSchema = false)
public abstract class noteRoomDatabase extends RoomDatabase {
    public abstract ItemNoteDAO itemNodeDAO();
    private static volatile noteRoomDatabase INSTANCE;
    private  static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDB(INSTANCE).execute();
        }
    };

    static noteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (noteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            noteRoomDatabase.class, "note_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static class PopulateDB extends AsyncTask<Void,Void,Void> {
        private final ItemNoteDAO inDAO;

        PopulateDB(noteRoomDatabase db){
            inDAO = db.itemNodeDAO();
        }



        @Override
        protected Void doInBackground(Void... params) {
            inDAO.deleteAll();
            ItemNote in = new ItemNote("Reminder to call John");
            inDAO.insert(in);
            return null;
        }
    }

}
