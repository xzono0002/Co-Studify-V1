package com.june.costudify.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.june.costudify.entities.Notes;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC ")
    List<Notes> getAllNotes();

//    @Query("SELECT * FROM notes WHERE note_due_date ")

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Notes notes);

    @Delete
    void deleteNote(Notes notes);
}
