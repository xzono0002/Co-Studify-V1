package com.june.costudify.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.june.costudify.R;
import com.june.costudify.entities.Notes;
import com.june.costudify.listeners.NotesListener;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final List<Notes> notes;
    private NotesListener notesListener;

    public NotesAdapter(List<Notes> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.note_item, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setNote(notes.get(position));
        holder.noteItemLayout.setOnClickListener(v -> notesListener.onNoteClicked(notes.get(position), position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView noteArea;
        TextView dateTime;
        View colorIndicator;
        LinearLayout noteItemLayout;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteArea = itemView.findViewById(R.id.noteArea);
            dateTime = itemView.findViewById(R.id.dueDate);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            noteItemLayout = itemView.findViewById(R.id.noteItemLayout);
        }

        void setNote(Notes notes){
            noteArea.setText(notes.getTitle());
            dateTime.setText(notes.getDateCreated());
            GradientDrawable gradientDrawable = (GradientDrawable) colorIndicator.getBackground();
            if(notes.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(notes.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#212529" ));
            }
        }
    }
}
