package com.twonote.mohamed.twonote.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.activities.MainActivity;
import com.twonote.mohamed.twonote.activities.NoteDetailsActivity;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.utils.NoteType;

/**
 * Created by mohamed on 11/03/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {

    private final Context mContext;
    private Cursor mCursor;

    public NotesAdapter(Context context) {
        this.mContext = context;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) mCursor.close();
        mCursor = cursor;
        if(cursor != null) {
            notifyDataSetChanged();
        }
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId = R.layout.list_item_note;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        mCursor.moveToPosition(position);
        Integer noteType = mCursor.getInt(MainActivity.INDEX_NOTE_TYPE);
        String noteTitle = mCursor.getString(MainActivity.INDEX_NOTE_TITLE);
        String noteCreationDate = mCursor.getString(MainActivity.INDEX_NOTE_CREATION_DATE);
        String alarm_date = mCursor.getString(MainActivity.INDEX_NOTE_ALARM_DATE);

        boolean hasAlarm = true;
        if(alarm_date == null || alarm_date.isEmpty()) {
            hasAlarm = false;
        }

        holder.bind(noteTitle, noteType, noteCreationDate, hasAlarm);

        holder.itemView.setTag(R.integer.note_type, noteType);
        int id = mCursor.getInt(mCursor.getColumnIndex(NoteContract.NoteEntry._ID));
        holder.itemView.setTag(R.integer.note_id, id);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        Log.e("xzxx", String.valueOf(mCursor.getInt(MainActivity.INDEX_NOTE_ID)));
        return mCursor.getInt(MainActivity.INDEX_NOTE_ID);
    }


    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView noteTitleTextView;
        private TextView noteCreationDateTextView;
        private ImageView noteIconImageView;
        private ImageView alarmIconImageView;
        private Integer noteType;
        private Integer noteId;

        public NoteHolder(View itemView) {
            super(itemView);
            noteTitleTextView = (TextView) itemView.findViewById(R.id.noteTitleTextView);
            noteIconImageView = (ImageView) itemView.findViewById(R.id.noteIconImageView);
            alarmIconImageView = (ImageView) itemView.findViewById(R.id.alarmIconImageView);
            noteCreationDateTextView = (TextView) itemView.findViewById(R.id.creationDateTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(String title, int noteType, String noteCreationDate, boolean hasAlarm) {
            noteTitleTextView.setText(title);
            noteCreationDateTextView.setText(noteCreationDate);
            switch (noteType) {
                case NoteType.TEXT:
                    noteIconImageView.setImageResource(R.drawable.ic_text_fields_black_24dp);
                    break;
                case NoteType.IMAGE:
                    noteIconImageView.setImageResource(R.drawable.ic_insert_photo_black_24dp);
            }

            if(hasAlarm) {
                alarmIconImageView.setVisibility(View.VISIBLE);
            }else {
                alarmIconImageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Integer noteType = (Integer) itemView.getTag(R.integer.note_type);
            Integer noteId = (Integer) itemView.getTag(R.integer.note_id);
            Intent intent = new Intent(mContext , NoteDetailsActivity.class);
            intent.putExtra(NoteContract.NoteEntry.COLUMN_TYPE, noteType);
            intent.putExtra(NoteContract.NoteEntry._ID, noteId);
            mContext.startActivity(intent);
        }
    }

}
