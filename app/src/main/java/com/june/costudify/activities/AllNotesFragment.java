package com.june.costudify.activities;

import static android.app.appsearch.AppSearchResult.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.june.costudify.R;
import com.june.costudify.adapters.NotesAdapter;
import com.june.costudify.database.NotesDatabase;
import com.june.costudify.entities.Notes;
import com.june.costudify.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class AllNotesFragment extends Fragment implements NotesListener {

    FloatingActionButton addNote;
    private RecyclerView noteAreaRecycler;
    private List<Notes> notesList;
    private NotesAdapter notesAdapter;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;
    private int noteClickedPosition = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_notes, container, false);
        addNote = rootView.findViewById(R.id.add_notes);
        noteAreaRecycler = rootView.findViewById(R.id.notesRecycle);
        noteAreaRecycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        notesList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesList, this);
        noteAreaRecycler.setAdapter(notesAdapter);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), AddNotes.class),
                        REQUEST_CODE_ADD_NOTE);
            }
        });

        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        return rootView;
    }

    @Override
    public void onNoteClicked(Notes notes, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getContext(), AddNotes.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", notes);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Notes>> {

            @Override
            protected List<Notes> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getActivity())
                        .noteDao().getAllNotes();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Notes> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    notesList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    notesList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    noteAreaRecycler.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    notesList.remove(noteClickedPosition);
                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        notesList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }
}