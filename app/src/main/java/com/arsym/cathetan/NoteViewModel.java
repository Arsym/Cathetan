package com.arsym.cathetan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.arsym.cathetan.adapter.NoteAdapter;
import com.arsym.cathetan.roomdb.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    private NoteAdapter adapter = new NoteAdapter();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllnotes();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void restoreItem(Note note, int position) {
        repository.insert(note);
        adapter.notifyItemInserted(position);
    }


    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

}
