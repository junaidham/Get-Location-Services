package com.websmithing.gpstracker.MyDataApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;



import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Utils {
    public static final String TAG ="Utils";
    public final static int ALL_PERMISSIONS_RESULT = 107;


    public static void showToastMessage(Context context, String Msg) {
        Toast.makeText(context, Msg, Toast.LENGTH_LONG).show();
    }

    public static void showLog(String key, String value) {
        Log.e(TAG, key + "          " + value);
    }

    public static boolean isNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            } else {
                Log.e(TAG,"NetworkInfo[]   " + info);
            }
        } else {
            Log.e(TAG,"ConnectivityManager   " + manager);
        }
        return false;
    }


    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }


    public static void showGpsSettingAlert(final Context context) {
        Log.e(TAG,"--Checking GPS showGpsSettingAlert ");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, Do you want to go to settings menu? ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    // http://www.digitstory.com/enable-gps-automatically-android/
    // Checking GPS Status on Android requires
    public static boolean isGPSEnabled(Context mContext) {
        Log.e(TAG,"--Checking GPS Status on Android requires ");
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return manager .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /// https://stackoverflow.com/questions/45385209/how-to-ask-permission-runtime-again-if-the-user-deny-for-the-first-time
    // https://stackoverflow.com/questions/33666071/android-marshmallow-request-permission
    // https://www.journaldev.com/10409/android-runtime-permissions-example
    public static boolean checkPermission(Context context) {
        int result1 = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(context, CAMERA);
        int result3 = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                    && result2 == PackageManager.PERMISSION_GRANTED
                    && result3 == PackageManager.PERMISSION_GRANTED
                    && result4 == PackageManager.PERMISSION_GRANTED
                ;
    }

    public static boolean checkPermission2(Context context) {
        int result1 = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED
                ;
    }


    public static void requestPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{ACCESS_FINE_LOCATION,CAMERA,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE},
                ALL_PERMISSIONS_RESULT);

    }

    public static void requestPermission2(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{ACCESS_FINE_LOCATION,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE},
                ALL_PERMISSIONS_RESULT);

    }
    // getMonthName
    public static String getMonthName(int month) {
        if (month == 1) {
            return "Jan";
        } else if (month == 2) {
            return "Feb";
        } else if (month == 3) {
            return "Mar";
        } else if (month == 4) {
            return "Apr";
        } else if (month == 5) {
            return "May";
        } else if (month == 6) {
            return "Jun";
        } else if (month == 7) {
            return "Jul";
        } else if (month == 8) {
            return "Aug";
        } else if (month == 9) {
            return "Sep";
        } else if (month == 10) {
            return "Oct";
        } else if (month == 11) {
            return "Nov";
        } else if (month == 12) {
            return "Dec";
        }
        return "";
    }

}