package com.twonote.mohamed.twonote.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.receivers.NoteReceiver;
import com.twonote.mohamed.twonote.utils.DateUtility;
import com.twonote.mohamed.twonote.utils.NoteType;
import com.twonote.mohamed.twonote.utils.NotificationUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    private Calendar dateCalender = Calendar.getInstance();
    private Calendar timeCalendar = Calendar.getInstance();
    private String selectedDate = null;
    private String selectedTime = null;
    private AlertDialog alarmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
    }

    public void addNote(){

        Intent intent = getIntent();
        int noteType = intent.getIntExtra(Intent.EXTRA_TEXT, 1);

        switch (noteType){
            case NoteType.TEXT:
                EditText noteTitleEditText = (EditText) findViewById(R.id.noteTitle);
                EditText noteDescriptionEditText = (EditText) findViewById(R.id.noteDescription);
                String noteTitle = noteTitleEditText.getText().toString();
                String noteDescription = noteDescriptionEditText.getText().toString();


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

                ContentValues contentValues = new ContentValues();
                contentValues.put(NoteContract.NoteEntry.COLUMN_TITLE, noteTitle);
                contentValues.put(NoteContract.NoteEntry.COLUMN_DESCRIPTION, noteDescription);
                contentValues.put(NoteContract.NoteEntry.COLUMN_TYPE, NoteType.TEXT);
                contentValues.put(NoteContract.NoteEntry.CREATION_DATE, currentDate);
                contentValues.put(NoteContract.NoteEntry.ALARM_DATE, alarmDateTime);
                contentValues.put(NoteContract.NoteEntry.ALARM_ID, alarmId);
                Uri uri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, contentValues);
                int noteId = (int) ContentUris.parseId(uri);

                if(selectedDate != null && selectedTime != null) {
                    Log.e("Eee", alarmDateTime);
                    try {
                        NotificationUtility.scheduleNoteAlarm(this, noteTitle, noteDescription, alarmDateTime, currentDate, noteId , alarmId);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }

        finish();

    }



    public void setAlarm() {

        this.selectedDate = null;
        this.selectedTime = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this);

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
                new DatePickerDialog(AddNoteActivity.this, dateListener, dateCalender
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
                    new DatePickerDialog(AddNoteActivity.this, dateListener, dateCalender
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
                    new TimePickerDialog(AddNoteActivity.this, timeListener, dateCalender.get(Calendar.HOUR_OF_DAY), dateCalender.get(Calendar.MINUTE), false).show();

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
                new TimePickerDialog(AddNoteActivity.this, timeListener, dateCalender.get(Calendar.HOUR_OF_DAY), dateCalender.get(Calendar.MINUTE), false).show();

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
            addNote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
