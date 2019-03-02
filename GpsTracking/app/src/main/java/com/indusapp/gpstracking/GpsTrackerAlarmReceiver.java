package com.indusapp.gpstracking;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;





// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class GpsTrackerAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";
//    AlarmReceiver alarmReceiver;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");
        context.startService(new Intent(context, LocationService.class));
        Log.d(TAG,"LocationService");
//        alarmReceiver = new AlarmReceiver();

//        context.sendBroadcast(new Intent(context, AlarmReceiver.class));
//        Log.d(TAG,"Called sendBroadcast and AlarmReceiver ");

        //sendBroadcast
//        try {
//            context.sendBroadcast(new Intent(context, AlarmReceiver.class));
//            Log.d(TAG,"Called sendBroadcast and AlarmReceiver ");
//        } catch (Exception e) {
//            alarmReceiver.methodForException(e.toString());
//        }

        // Using PendingIntent with an IntentService
//        try {
////            Intent intent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (1* 60 * 1000), pendingIntent);
//            Log.d(TAG,"Called pendingIntent and (AlarmManager) getSystemService(ALARM_SERVICE) ");
//        } catch (Exception e) {
//            Logger.e("Exception     " + e.toString());
//            alarmReceiver.methodForException(e.toString());
//        }





//        context.sendBroadcast(new Intent(context, AlarmReceiver.class));
//        Log.d(TAG,"Called sendBroadcast and AlarmReceiver ");
    }
}
