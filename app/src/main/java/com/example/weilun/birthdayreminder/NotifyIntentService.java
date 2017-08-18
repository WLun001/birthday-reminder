package com.example.weilun.birthdayreminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Wei Lun on 8/18/2017.
 */

public class NotifyIntentService extends IntentService{

    public static final int NOTIFICATION_ID = 1;
    public static final String CURSOR = "com.example.weilun.birthdayreminder.CURSOR";
    private static Calendar calender;

    public NotifyIntentService(){
        super(NotifyIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        calender = Calendar.getInstance();
        calender.add(Calendar.DAY_OF_MONTH, -1);

        PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(this));
        String[] columns = PersonContract.columns;
        String[] selectionArgs = {calender.getTimeInMillis() + "", "" + calender.getTimeInMillis()};
        //to convert millisecond to Unix timestamp, divide by 1000
        Cursor cursor = dbQuery.query(columns, "strftime('%m-%d'," + PersonContract.PersonEntry.COLUMN_NAME_DOB + "/1000, 'unixepoch')"
                        + " BETWEEN strftime('%m-%d',?/1000, 'unixepoch') AND strftime('%m-%d',?/1000, 'unixepoch')"
                + "AND " + PersonContract.PersonEntry.COLUMN_NAME_NOFITY + " = '1'",
                selectionArgs, null, null, null);

        //CursorWrapper cursorWrapper = new CursorWrapper(cursor);
        int todayBirthday = cursor.getCount();

        Log.v("NotifyIntentSerivice", "today birthday: " + todayBirthday + "");

        if(todayBirthday > 0) {

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(String.format(getString(R.string.notification_content), todayBirthday))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_account_circle_black_24dp);

            Log.v("NotifyIntentService", "notification built");

            Intent todayBirthdayIntent = new Intent(this, TodayBirthdayActivity.class);
//            Intent cursorIntent = new Intent(this, TodayBirthdayActivity.class);
//            cursorIntent.putExtra(CURSOR,cursorWrapper);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, todayBirthdayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(NOTIFICATION_ID, notification);

            builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        }
    }

//    public class CursorWrapper implements Serializable{
//        private Cursor cursor;
//        public CursorWrapper(Cursor cursor) {
//            this.cursor = cursor;
//        }
//
//        public Cursor getCursor(){
//            return cursor;
//        }
//    }
}