package com.example.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG ="AlarmReceiver";

	public AlarmReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(TAG,"onCreate");

		// Toast.makeText(context, "Alarm Reciever", Toast.LENGTH_SHORT).show();
		context.startService(new Intent(context, AndroidLocationServices.class));
		context.startService(new Intent(context, LocationService.class));

	}

}
