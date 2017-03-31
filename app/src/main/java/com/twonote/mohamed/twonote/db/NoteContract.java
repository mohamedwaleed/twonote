package com.twonote.mohamed.twonote.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mohamed on 12/03/17.
 */

public class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.twonote.notes";
    public static final String PATH_NOTES = "notes";
    public static final String PATH_NOTES_TYPE = "notes";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class NoteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTES)
                .build();
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TYPE = "type";
        public static final String CREATION_DATE = "creation_date";
        public static final String ALARM_DATE = "alarm_date";
        public static final String ALARM_ID = "alarm_id";
    }

    public static final class NoteTypeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTES_TYPE)
                .build();
        public static final String TABLE_NAME = "note_type";
        public static final String COLUMN_NAME = "name";
    }
}
