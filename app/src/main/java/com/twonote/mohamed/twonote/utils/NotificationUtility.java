package com.twonote.mohamed.twonote.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.twonote.mohamed.twonote.R;
import com.twonote.mohamed.twonote.activities.NoteDetailsActivity;
import com.twonote.mohamed.twonote.db.NoteContract;
import com.twonote.mohamed.twonote.receivers.NoteReceiver;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mohamed on 18/03/17.
 */

public class NotificationUtility {


    public static void sendNoteNotification(Context context, String noteTitle, int noteId, int noteType) {
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

        notificationManager.notify(noteId, notificationBuilder.build());
    }


    public static void scheduleNoteAlarm(Context context, String noteTitle, String noteDescription, String alarmDateTime, String currentDate, int noteId, Integer alarmId) throws ParseException {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
        selectedDate.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
        selectedDate.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH));
        selectedDate.set(Calendar.HOUR_OF_DAY, selectedDate.get(Calendar.HOUR_OF_DAY));
        selectedDate.set(Calendar.MINUTE, selectedDate.get(Calendar.MINUTE));

        Date alarmDate = DateUtility.getDataObject(alarmDateTime,"dd-MMM-yyyy hh:mm a");

        Log.e("date" , alarmDate.toString());
        Intent intent = new Intent(context, NoteReceiver.class);
        intent.putExtra(NoteContract.NoteEntry._ID, noteId);
        intent.putExtra(NoteContract.NoteEntry.COLUMN_TITLE, noteTitle);
        intent.putExtra(NoteContract.NoteEntry.COLUMN_DESCRIPTION, noteDescription);
        intent.putExtra(NoteContract.NoteEntry.COLUMN_TYPE, NoteType.TEXT);
        intent.putExtra(NoteContract.NoteEntry.CREATION_DATE, currentDate);
        intent.putExtra(NoteContract.NoteEntry.ALARM_DATE, alarmDateTime);
        intent.putExtra(NoteContract.NoteEntry.ALARM_ID, alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDate.getTime(), pendingIntent);
    }


    public static void cancelNoteAlarm(Context context, Integer alarmId)  {

        Intent intent = new Intent(context, NoteReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }

}
