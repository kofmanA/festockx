package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.ClipData;

import java.util.List;

public class ItemNoteViewModel extends AndroidViewModel {
    private noteRepository nRepository;
    private LiveData<List<ItemNote>> listNotes;

    public ItemNoteViewModel(Application application){
        super(application);
        nRepository = new noteRepository(application);
        listNotes = nRepository.getAllNotes();
    }
    public void insert(ItemNote itemNote){
        nRepository.insert(itemNote);
    }

    public void delete(int pos){
        nRepository.delete(listNotes.getValue().get(pos));
    }

    public ItemNote getNote(int pos){ return listNotes.getValue().get(pos);}


    public LiveData<List<ItemNote>> getAllNotes(){
        return listNotes;
    }
}
