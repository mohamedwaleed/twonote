package com.twonote.mohamed.twonote.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.utils.NoteType;

import java.io.File;

public class NoteDetailsActivity extends AppCompatActivity {

    private Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        Intent intent = getIntent();
        int noteId = intent.getIntExtra(NoteContract.NoteEntry._ID, 1);
        int noteType = intent.getIntExtra(NoteContract.NoteEntry.COLUMN_TYPE, 1);

        mCursor = getNote(noteId);
        mCursor.moveToPosition(0);
        String noteTitle = mCursor.getString(MainActivity.INDEX_NOTE_TITLE);
        String noteDescription = mCursor.getString(MainActivity.INDEX_DESCRIPTION_TITLE);

        TextView noteTitleTextView = (TextView) findViewById(R.id.noteTitleDisplay);
        ImageView noteImageView = (ImageView) findViewById(R.id.noteImageView);
        TextView noteDescriptionTextView = (TextView) findViewById(R.id.noteDescriptionDisplay);

        noteTitleTextView.setText(noteTitle);

        switch (noteType) {
            case NoteType.TEXT:
                noteImageView.setVisibility(View.INVISIBLE);
                noteDescriptionTextView.setText(noteDescription);
                break;
            case NoteType.IMAGE:
                File imgFile = new File(noteDescription);

                if(imgFile.exists()){
                    noteDescriptionTextView.setVisibility(View.INVISIBLE);
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    noteImageView.setImageBitmap(myBitmap);
                }
                break;
        }

    }


    public Cursor getNote(int noteId) {
        String [] projection = {NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_TITLE,
                NoteContract.NoteEntry.COLUMN_DESCRIPTION,
                NoteContract.NoteEntry.COLUMN_TYPE,
                NoteContract.NoteEntry.CREATION_DATE};
        String selection = NoteContract.NoteEntry._ID + "=?";
        String [] selectionArgs = {String.valueOf(noteId)};
        return getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit) {
            Intent intent = getIntent();
            int noteId = intent.getIntExtra(NoteContract.NoteEntry._ID, 1);
            int noteType = intent.getIntExtra(NoteContract.NoteEntry.COLUMN_TYPE, 1);

            Intent editNoteIntent = new Intent(this, EditNoteActivity.class);
            editNoteIntent.putExtra(NoteContract.NoteEntry._ID, noteId);
            editNoteIntent.putExtra(NoteContract.NoteEntry.COLUMN_TYPE, noteType);
            startActivity(editNoteIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
