package com.twonote.mohamed.twonote.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class NoteContentProvider extends ContentProvider {

    public static final int CODE_NOTES = 100;

    private NoteDbHelper noteDbHelper;

    private UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = NoteContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, NoteContract.PATH_NOTES, CODE_NOTES);

        return uriMatcher;
    }

    public NoteContentProvider() {
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int rowAffected =  noteDbHelper.getWritableDatabase().delete(NoteContract.NoteEntry.TABLE_NAME, where, whereArgs);
        if (rowAffected > 0){
            getContext().getContentResolver().notifyChange(NoteContract.NoteEntry.CONTENT_URI, null);
        }
        return rowAffected;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowInserted = 0;
        switch (uriMatcher.match(uri)) {
            case CODE_NOTES:
                rowInserted = noteDbHelper.getWritableDatabase().insert(NoteContract.NoteEntry.TABLE_NAME,null,values);
                if(rowInserted == -1){
                    throw new IllegalArgumentException("Insert error");
                }else {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            default:
                throw new UnsupportedOperationException("");
        }
        return uri.withAppendedPath(uri, String.valueOf(rowInserted));
    }

    @Override
    public boolean onCreate() {
        noteDbHelper = new NoteDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case CODE_NOTES:
               cursor = noteDbHelper.getReadableDatabase().query(NoteContract.NoteEntry.TABLE_NAME,
                        projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        int rowAffected = 0;
        switch (uriMatcher.match(uri)) {
            case CODE_NOTES:
                rowAffected = noteDbHelper.getWritableDatabase().update(NoteContract.NoteEntry.TABLE_NAME, values, where, whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException("");
        }
        return rowAffected;
    }
}
