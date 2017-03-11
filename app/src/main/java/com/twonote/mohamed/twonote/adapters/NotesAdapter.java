package com.twonote.mohamed.twonote.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twonote.mohamed.twonote.R;

/**
 * Created by mohamed on 11/03/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {

    private final Context mContext;
    private String []data = {"note1" , "note2" , "note3" , "note4", "note5"};
    public NotesAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId = R.layout.list_item_note;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        holder.bind(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        private TextView noteTitleTextView;

        public NoteHolder(View itemView) {
            super(itemView);
            noteTitleTextView = (TextView) itemView.findViewById(R.id.noteTitleTextView);
        }

        public void bind(String title) {
            noteTitleTextView.setText(title);
        }
    }

}
