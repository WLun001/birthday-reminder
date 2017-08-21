package com.example.weilun.birthdayreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Wei Lun on 8/18/2017.
 */

public class NotiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, NotifyIntentService.class);
        context.startService(intentService);
    }
}