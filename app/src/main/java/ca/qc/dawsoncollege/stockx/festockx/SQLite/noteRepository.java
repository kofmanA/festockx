package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import static java.lang.Integer.parseInt;

public class noteRepository {
    private ItemNoteDAO itemNoteDAO;
    private LiveData<List<ItemNote>> allNotes;

    noteRepository(Application application){
        noteRoomDatabase rdb= noteRoomDatabase.getDatabase(application);
        itemNoteDAO = rdb.itemNodeDAO();
        allNotes = itemNoteDAO.getAllNotes();
    }

    LiveData<List<ItemNote>> getAllNotes(){
        return allNotes;
    }

    public void insert(ItemNote itemNote){
        new insertAsyncTask(itemNoteDAO).execute(itemNote);
    }

    public void updateNote(String[] data) { new updateAsyncTask(itemNoteDAO).execute(data);}

    public void delete(ItemNote itemNote){new deleteAsyncTask(itemNoteDAO).execute(itemNote);
    }


    private static class insertAsyncTask extends AsyncTask<ItemNote, Void, Void> {

        private ItemNoteDAO mAsyncTaskDao;

        insertAsyncTask(ItemNoteDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ItemNote... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class updateAsyncTask extends AsyncTask<String, Void, Void> {

        private ItemNoteDAO mAsyncTaskDao;

       updateAsyncTask(ItemNoteDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.updateNote(parseInt(params[1]),params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<ItemNote, Void, Void> {

        private ItemNoteDAO mAsyncTaskDao;

         deleteAsyncTask(ItemNoteDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ItemNote... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

}
