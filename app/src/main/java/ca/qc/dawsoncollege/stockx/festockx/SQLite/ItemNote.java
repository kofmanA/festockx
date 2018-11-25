package ca.qc.dawsoncollege.stockx.festockx.SQLite;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "itemNote_table")
public class ItemNote {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "note")
    private String note;


    public ItemNote(@NonNull String note){
        this.note=note;
    }
    public String getNote(){
        return this.note;
    }

}
