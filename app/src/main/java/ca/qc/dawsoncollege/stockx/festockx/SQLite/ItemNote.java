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

    @NonNull
    @ColumnInfo(name = "note")
    private String note;

    @PrimaryKey(autoGenerate = true)
    private int id;
    public ItemNote(@NonNull String note){
        this.note=note;
    }
    public void setId(@NonNull int id){ this.id = id;}
    public String getNote(){
        return this.note;
    }
    public int getId(){
        return this.id;
    }

}
