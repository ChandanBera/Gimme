package com.xentric.gimme.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by abhideepmallick on 14/07/17.
 */

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("boot_broadcast_poc", "starting service...");
        context.startService(new Intent(context, SensorService.class));
    }


}