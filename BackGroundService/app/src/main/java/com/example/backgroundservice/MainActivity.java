package com.example.backgroundservice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * 
 * @author Atish Agrawal
 * 
 */

public class MainActivity extends Activity {
	private static final String TAG ="MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG,"onCreate");
		final Button btnStart = findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				btnStart.setBackgroundColor(getResources().getColor(R.color.colorAccent));
				startService(new Intent(getApplicationContext(), AndroidLocationServices.class));
			}
		});


//		try {
//			startActivity(new Intent(Intent.ACTION_VIEW,
//					Uri.parse("market://details?id=" + "TempleRun")));
//		} catch (android.content.ActivityNotFoundException anfe) {
//			startActivity(new Intent(Intent.ACTION_VIEW,
//					Uri.parse("http://play.google.com/store/apps/details?id="
//							+ "TempleRun")));
//		}

	}

}
