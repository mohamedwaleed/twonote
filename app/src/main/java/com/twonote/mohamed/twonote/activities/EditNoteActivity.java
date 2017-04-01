package com.twonote.mohamed.twonote.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.utils.DateUtility;
import com.twonote.mohamed.twonote.utils.NoteType;
import com.twonote.mohamed.twonote.utils.NotificationUtility;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {


    private Cursor mCursor;
    private Calendar dateCalender = Calendar.getInstance();
    private Calendar timeCalendar = Calendar.getInstance();
    private String selectedDate = null;
    private String selectedTime = null;
    private AlertDialog alarmDialog;
    private String imageUrl = null;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView editNoteImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int noteId = intent.getIntExtra(NoteContract.NoteEntry._ID, 1);
        int noteType = intent.getIntExtra(NoteContract.NoteEntry.COLUMN_TYPE, 1);

        EditText editNoteDescEditText = null;


        if(noteType == NoteType.TEXT) {
            setContentView(R.layout.activity_edit_note);
            editNoteDescEditText = (EditText) findViewById(R.id.editNoteDescription);
        }else if(noteType == NoteType.IMAGE) {
            setContentView(R.layout.activity_edit_image_note);
            editNoteImageView = (ImageView) findViewById(R.id.editNoteImageView);
        }
        EditText editNoteTitleEditText = (EditText) findViewById(R.id.editNoteTitle);

        mCursor = getNote(noteId);
        mCursor.moveToPosition(0);
        String noteTitle = mCursor.getString(MainActivity.INDEX_NOTE_TITLE);
        String noteDescription = mCursor.getString(MainActivity.INDEX_DESCRIPTION_TITLE);




        editNoteTitleEditText.setText(noteTitle);

        Log.e("note type" , String.valueOf(noteType));
        Log.e("sss" , String.valueOf(NoteType.IMAGE));
        if(noteType == NoteType.TEXT){
            editNoteDescEditText.setText(noteDescription);
        }else if(noteType == NoteType.IMAGE) {

            File imgFile = new File(noteDescription);
            Log.e("sss" , "out");
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                editNoteImageView.setImageBitmap(myBitmap);
                editNoteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                Toast.makeText(EditNoteActivity.this, "Can not create image file",Toast.LENGTH_SHORT).show();
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(EditNoteActivity.this,
                                        "com.example.android.fileprovider",
                                        photoFile);
                                imageUrl = photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imageUrl);
            editNoteImageView.setImageBitmap(myBitmap);
        }else {
            imageUrl = null;
        }

    }

    public Cursor getNote(int noteId) {
        String selection = NoteContract.NoteEntry._ID + "=?";
        String [] selectionArgs = {String.valueOf(noteId)};
        return getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, null, selection, selectionArgs, null);
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

    public void editNote(Integer noteId, Integer noteType){
        EditText editNoteTitleEditText = (EditText) findViewById(R.id.editNoteTitle);
        EditText editNoteDescEditText = null;
        String noteDescription = null;

        if(noteType == NoteType.TEXT) {
            editNoteDescEditText = (EditText) findViewById(R.id.editNoteDescription);
            noteDescription = editNoteDescEditText.getText().toString();
        }else {
            noteDescription = mCursor.getString(MainActivity.INDEX_DESCRIPTION_TITLE);
            if(imageUrl != null) {
                noteDescription = imageUrl;
            }
        }

        String noteTitle = editNoteTitleEditText.getText().toString();


        if (noteTitle.length() > 20) {
            Toast.makeText(this, R.string.long_title, Toast.LENGTH_SHORT).show();
            return ;
        }
        if (noteTitle.isEmpty()){
            Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show();
            return ;
        }

        String alarmDateTime = null;
        Integer alarmId = null;
        if(selectedDate != null && selectedTime != null){
            alarmDateTime = selectedDate + " " + selectedTime;
            alarmId = 1 + (int)(Math.random() * 10000);
        }
        Log.e("alarm id" , String.valueOf(alarmId));
        String currentDate = DateUtility.getCurrentDate("dd-MMM-yyyy");

        mCursor = getNote(noteId);
        mCursor.moveToPosition(0);
        Integer previousAlarmId = mCursor.getInt(MainActivity.INDEX_NOTE_ALARM_ID);

        String where = NoteContract.NoteEntry._ID + "=?";
        String [] whereArgs = {noteId.toString()};

        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteContract.NoteEntry.COLUMN_TITLE, noteTitle);
        contentValues.put(NoteContract.NoteEntry.COLUMN_DESCRIPTION, noteDescription);
        contentValues.put(NoteContract.NoteEntry.ALARM_DATE, alarmDateTime);
        contentValues.put(NoteContract.NoteEntry.ALARM_ID, alarmId);
        int affectedRows = getContentResolver().update(NoteContract.NoteEntry.CONTENT_URI, contentValues, where, whereArgs);
        Log.e("affeced Rows" , String.valueOf(affectedRows));
        if(selectedDate != null && selectedTime != null) {
            try {
                NotificationUtility.cancelNoteAlarm(this, previousAlarmId);
                NotificationUtility.scheduleNoteAlarm(this, noteTitle, noteDescription, alarmDateTime, currentDate, noteId , alarmId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        Toast.makeText(this, R.string.updated, Toast.LENGTH_LONG).show();

    }


    public void setAlarm() {

        this.selectedDate = null;
        this.selectedTime = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);

        View mView = getLayoutInflater().inflate(R.layout.date_time_picker, null);

        builder.setTitle("Choose alarm date and time");
        builder.setView(mView);
        alarmDialog = builder.create();
        alarmDialog.show();


        final EditText datePickerEditText = (EditText) mView.findViewById(R.id.datePickerEditText);
        final EditText timePickerEditText = (EditText) mView.findViewById(R.id.timePickerEditText);

        if (Build.VERSION.SDK_INT >= 11) {
            datePickerEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            datePickerEditText.setTextIsSelectable(true);
        } else {
            datePickerEditText.setRawInputType(InputType.TYPE_NULL);
            datePickerEditText.setFocusable(true);
        }

        if (Build.VERSION.SDK_INT >= 11) {
            timePickerEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            timePickerEditText.setTextIsSelectable(true);
        } else {
            timePickerEditText.setRawInputType(InputType.TYPE_NULL);
            timePickerEditText.setFocusable(true);
        }


        datePickerEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        setSelectedDate(year, monthOfYear, dayOfMonth, datePickerEditText);
                    }

                };
                new DatePickerDialog(EditNoteActivity.this, dateListener, dateCalender
                        .get(Calendar.YEAR), dateCalender.get(Calendar.MONTH),
                        dateCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        datePickerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {
                    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            setSelectedDate(year, monthOfYear, dayOfMonth, datePickerEditText);
                        }

                    };
                    new DatePickerDialog(EditNoteActivity.this, dateListener, dateCalender
                            .get(Calendar.YEAR), dateCalender.get(Calendar.MONTH),
                            dateCalender.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        timePickerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {
                    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            setSelectedTime(hourOfDay, minute, timePickerEditText);
                        }
                    };
                    new TimePickerDialog(EditNoteActivity.this, timeListener, dateCalender.get(Calendar.HOUR_OF_DAY), dateCalender.get(Calendar.MINUTE), false).show();

                }
            }
        });


        timePickerEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setSelectedTime(hourOfDay, minute, timePickerEditText);
                    }
                };
                new TimePickerDialog(EditNoteActivity.this, timeListener, dateCalender.get(Calendar.HOUR_OF_DAY), dateCalender.get(Calendar.MINUTE), false).show();

            }
        });
    }


    private void setSelectedTime(int hourOfDay, int minute, EditText timePickerEditText) {
        timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        timeCalendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String selectedTime = format.format(timeCalendar.getTime());
        this.selectedTime = selectedTime;
        timePickerEditText.setText(selectedTime);
    }

    private void setSelectedDate(int year, int monthOfYear, int dayOfMonth, EditText datePickerEditText) {
        dateCalender.set(Calendar.YEAR, year);
        dateCalender.set(Calendar.MONTH, monthOfYear);
        dateCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd-MMMM-yyyy");
        String selectedDate = format.format(dateCalender.getTime());
        this.selectedDate = selectedDate;
        datePickerEditText.setText(selectedDate);
    }
    public void hideAlarmDialog(View view) {


        if(selectedTime == null || selectedDate == null){
            Toast.makeText(this, "You have to select date and time", Toast.LENGTH_SHORT).show();
            return ;
        }

        alarmDialog.dismiss();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.set_alarm) {
            setAlarm();
            return true;
        }else if(id == R.id.save_note) {

            Intent intent = getIntent();
            int noteId = intent.getIntExtra(NoteContract.NoteEntry._ID, 1);
            int noteType = intent.getIntExtra(NoteContract.NoteEntry.COLUMN_TYPE, 1);
            editNote(noteId, noteType);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
