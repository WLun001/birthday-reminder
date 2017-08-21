package com.example.weilun.birthdayreminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.util.Calendar;

/**
 * Created by Wei Lun on 8/18/2017.
 */

public class NotifyIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static String LOG_TAG = NotifyIntentService.class.getSimpleName();
    private static Calendar calender;

    public NotifyIntentService() {
        super(NotifyIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        calender = Calendar.getInstance();
        calender.add(Calendar.DAY_OF_MONTH, -1);

        PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(this));
        Cursor cursor = dbQuery.queryTodayBirthday(calender);

        //CursorWrapper cursorWrapper = new CursorWrapper(cursor);
        int todayBirthday = cursor.getCount();

        Log.v(LOG_TAG, "today birthday: " + todayBirthday);

        if (todayBirthday > 0) {

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(String.format(getString(R.string.notification_content), todayBirthday))
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.birthday_icon_launcher)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.birthday_icon_launcher))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            Log.v(LOG_TAG, "notification built");

            Intent todayBirthdayIntent = new Intent(this, TodayBirthdayActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, todayBirthdayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(NOTIFICATION_ID, notification);

            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        }
    }
}