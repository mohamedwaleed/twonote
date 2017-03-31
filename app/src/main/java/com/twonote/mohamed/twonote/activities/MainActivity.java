package com.twonote.mohamed.twonote.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.adapters.NotesAdapter;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.utils.DateUtility;
import com.twonote.mohamed.twonote.utils.NoteType;
import com.twonote.mohamed.twonote.utils.NotificationUtility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int INDEX_NOTE_TYPE = 3;
    public static final int INDEX_NOTE_ID = 0;
    public static final int INDEX_DESCRIPTION_TITLE = 2;
    public static final int INDEX_NOTE_ALARM_DATE = 5;
    public static final int INDEX_NOTE_ALARM_ID = 6;
    private static AlertDialog alertDialog;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int INDEX_NOTE_TITLE = 1;
    public static final int INDEX_NOTE_CREATION_DATE = 4;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton textNoteFab = (FloatingActionButton) findViewById(R.id.textFab);
        textNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, NoteType.TEXT);
                startActivity(intent);
                overridePendingTransition(0, 0);







            }
        });

        FloatingActionButton imageNoteFab = (FloatingActionButton) findViewById(R.id.imageFab);

        imageNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.title));
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String noteTitle = input.getText().toString();

                        if(noteTitle.isEmpty()) {
                            Toast.makeText(MainActivity.this, R.string.empty_title,Toast.LENGTH_SHORT).show();
                            return;
                        }else if(noteTitle.length() > 20){
                            Toast.makeText(MainActivity.this, R.string.long_title,Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                Toast.makeText(MainActivity.this, "Can not create image file",Toast.LENGTH_SHORT).show();
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                        "com.example.android.fileprovider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                                String currentDate = DateUtility.getCurrentDate("dd-MMM-yyyy");

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(NoteContract.NoteEntry.COLUMN_TITLE, noteTitle);
                                contentValues.put(NoteContract.NoteEntry.COLUMN_DESCRIPTION, photoFile.getAbsolutePath());
                                contentValues.put(NoteContract.NoteEntry.COLUMN_TYPE, NoteType.IMAGE);
                                contentValues.put(NoteContract.NoteEntry.CREATION_DATE, currentDate);
                                getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, contentValues);
                            }
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_notes);

        notesAdapter = new NotesAdapter(this);
        recyclerView.setAdapter(notesAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addItemDecoration(dividerItemDecoration);




        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int itemId = (int) viewHolder.itemView.getTag(R.integer.note_id);
                        int numOfDeletedRows  = deleteNote(itemId);
                        if(numOfDeletedRows == 0){
                            Toast.makeText(MainActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }else getNotes();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    @Override
    protected void onResume() {
        getNotes();
        super.onResume();
    }

    public void getNotes() {
        Cursor cursor = getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, null, null, null, null );
        notesAdapter.swapCursor(cursor);
    }

    public int deleteNote(Integer itemId) {
        String where = "_id=?";
        String [] whereArgs = {itemId.toString()};
        Cursor cursor = getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, null , where, whereArgs , null);
        cursor.moveToFirst();
        Integer alarmId = cursor.getInt(INDEX_NOTE_ALARM_ID);
        Log.e("alarm id" , String.valueOf(alarmId));
        NotificationUtility.cancelNoteAlarm(this, alarmId);
        return getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, where, whereArgs );
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
