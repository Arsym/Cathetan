package com.arsym.cathetan.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.arsym.cathetan.utils.TimestampConverter;

import java.util.Date;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private int color;

    @ColumnInfo(name = "timeCreate")
    @TypeConverters({TimestampConverter.class})
    private Date timeCreate;

    public Note(String title, String description, int color, Date timeCreate) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.timeCreate = timeCreate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

}
