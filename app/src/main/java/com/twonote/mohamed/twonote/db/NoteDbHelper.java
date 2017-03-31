package com.twonote.mohamed.twonote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.twonote.mohamed.twonote.db.NoteContract.*;
/**
 * Created by mohamed on 12/03/17.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "note.db";

    private static final int DATABASE_VERSION = 6;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_NOTE_TABLE =

                "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +

                        NoteEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        NoteEntry.COLUMN_TITLE       + " VARCHAR(20) NOT NULL, "                 +

                        NoteEntry.COLUMN_DESCRIPTION + " TEXT,"                  +

                        NoteEntry.COLUMN_TYPE   + " INTEGER NOT NULL, " +

                        NoteEntry.CREATION_DATE   + " VARCHAR(20) NOT NULL, " +

                        NoteEntry.ALARM_DATE   + " VARCHAR(20), " +
                        NoteEntry.ALARM_ID   + " INTEGER )";


        sqLiteDatabase.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
