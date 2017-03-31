package com.twonote.mohamed.twonote.receivers;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.activities.NoteDetailsActivity;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.utils.NotificationUtility;

public class NoteReceiver extends BroadcastReceiver {


    public NoteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String noteTitle = intent.getStringExtra(NoteContract.NoteEntry.COLUMN_TITLE);
        int noteId = intent.getIntExtra(NoteContract.NoteEntry._ID, 1);
        int noteType = intent.getIntExtra(NoteContract.NoteEntry.COLUMN_TYPE, 1);

        NotificationUtility.sendNoteNotification(context, noteTitle, noteId, noteType);


        ContentValues contentValues = new ContentValues();
        String nullValue = null;
        contentValues.put(NoteContract.NoteEntry.ALARM_DATE, nullValue);
        contentValues.put(NoteContract.NoteEntry.ALARM_ID, nullValue);

        String where = NoteContract.NoteEntry._ID + "=?";
        String [] whereArgs = {String.valueOf(noteId)};
        context.getContentResolver().update(NoteContract.NoteEntry.CONTENT_URI, contentValues, where, whereArgs);

    }


}
