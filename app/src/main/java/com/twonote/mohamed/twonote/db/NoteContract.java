package com.twonote.mohamed.twonote.db;

import android.provider.BaseColumns;

/**
 * Created by mohamed on 12/03/17.
 */

public class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.twonote.notes";
    public static final String PATH_NOTES = "notes";

    class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TYPE = "type";
    }

    class NoteTypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "note_type";
        public static final String COLUMN_NAME = "name";
    }
}
