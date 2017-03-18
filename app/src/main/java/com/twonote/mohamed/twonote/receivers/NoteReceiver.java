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

public class NoteReceiver extends BroadcastReceiver {

    private static final int NOTE_NOTIFICATION_ID = 3004;

    public NoteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
        Log.e("Alarm", "Alarm worked.");

        String noteTitle = intent.getStringExtra(NoteContract.NoteEntry.COLUMN_TITLE);
        int noteId = intent.getIntExtra(NoteContract.NoteEntry._ID, 1);
        int noteType = intent.getIntExtra(NoteContract.NoteEntry.COLUMN_TYPE, 1);

        Log.e("note id", String.valueOf(noteId));
        Resources resources = context.getResources();

        Bitmap largeIcon = BitmapFactory.decodeResource(
                resources,
                R.mipmap.ic_launcher);

        String notificationTitle = context.getString(R.string.app_name);

        String notificationText = noteTitle;

//            /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
        int smallArtResourceId = R.mipmap.ic_launcher;
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            /*
             * NotificationCompat Builder is a very convenient way to build backward-compatible
             * notifications. In order to use it, we provide a context and specify a color for the
             * notification, a couple of different icons, the title for the notification, and
             * finally the text of the notification, which in our case in a summary of today's
             * forecast.
             */
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(smallArtResourceId)
                .setLargeIcon(largeIcon)
                .setSound(alarmSound)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

            /*
             * This Intent will be triggered when the user clicks the notification.
             */
            Intent detailIntentForToday = new Intent(context, NoteDetailsActivity.class);
            detailIntentForToday.putExtra(NoteContract.NoteEntry.COLUMN_TYPE, noteType);
            detailIntentForToday.putExtra(NoteContract.NoteEntry._ID, noteId);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

                /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
            notificationManager.notify(NOTE_NOTIFICATION_ID, notificationBuilder.build());



        ContentValues contentValues = new ContentValues();
        String nullValue = null;
        contentValues.put(NoteContract.NoteEntry.ALARM_DATE, nullValue);

        String where = NoteContract.NoteEntry._ID + "=?";
        String [] whereArgs = {String.valueOf(noteId)};
        context.getContentResolver().update(NoteContract.NoteEntry.CONTENT_URI, contentValues, where, whereArgs);

    }
}
