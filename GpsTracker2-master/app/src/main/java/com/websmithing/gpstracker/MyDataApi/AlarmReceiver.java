package com.websmithing.gpstracker.MyDataApi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.websmithing.gpstracker.logger.Logger;
//import com.infojaalprime.hrms.attendance_model.mark_attendance_model.MarkAttendanceModel;
//import com.infojaalprime.hrms.interfaces.ApiFetcher;
//import com.infojaalprime.hrms.logger.Logger;
//import com.infojaalprime.hrms.manager.ApiManager;
//import com.infojaalprime.hrms.manager.SessionManager;
//import com.infojaalprime.hrms.models.app_version_model.AppVersionModel;
//import com.infojaalprime.hrms.urls.ServiceUrls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver implements ApiFetcher,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "AlarmReceiver";
    SessionManager sessionManager;
    ApiManager apiManager;
    Context mcontext;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000;
    TrackingDatabase trackingDatabase;
    private LocationServices services;

    double latitude = 0.0;
    double longitude = 0.0;

    String location_address = "", gps_status = "", internet_status = "", date = "", time = "", unique_id = "";

    Context context;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.e(TAG, "onReceive");
        sessionManager = new SessionManager(context);
        apiManager = new ApiManager(this);

        try {
            trackingDatabase = new TrackingDatabase(context);
            trackingDatabase.getTrackingCount();
            Log.e(TAG, "Count the getTrackingCount");
        } catch (Exception e) {
            methodForException(e.toString());
        }

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Logger.e(TAG,"checkSelfPermission");

                gps_status = "Location Permission Not Granted";

                date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
                time = new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();

                if (trackingDatabase.getTrackingCount() >= 12) {
                    if (Utils.isNetwork(context)) {
                        Logger.e(TAG,"Permission not permitted if count >12 and Network -On");
                        internet_status = "Internet Connection Available";
                        trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                        Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());

                        String xml = makeInsertSuspectRequestXML();
                        Logger.e("xml      " + xml);
                        String[] key2 = {"Doc"};
                        String[] value2 = {xml};
                        apiManager.set_interface_context_post(key2, value2, "URL_INSERT_ATTENDANCE_TRACKING", ServiceUrls.URL_INSERT_ATTENDANCE_TRACKING);
                        Log.e(TAG, "Post INSERT_ATTENDANCE_TRACKING");
                        Log.e(TAG, "called apiManager.set_interface_context_post(key2, value2, \"URL_INSERT_ATTENDANCE_TRACKING");

                    } else {
                        Logger.e(TAG,"Permission not permitted if count >12 and Network -Off");
                        internet_status = "No Internet Connection";
                        trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                        Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());
                    }
                } else {
                    Logger.e("Records are less than 12");
                    Logger.e(TAG,"Permission not permitted if count < 12 ");

                    if (Utils.isNetwork(context)) {
                        internet_status = "Internet Connection Available";
                    } else {
                        internet_status = "No Internet Connection";
                    }
                    trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                    Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());
                }

            } else {
                gps_status = "Location Permission Granted";
                Log.i(TAG,"Permission permitted-ok if count < 12 ");

                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    gps_status = "GPS is enabled";
                    date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
                    time = new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();
                    buildGoogleApiClient();
                    createLocationRequest();
                    mGoogleApiClient.connect();
                    Log.i(TAG,"Permission permitted-ok & GPS-ok");
                } else {
                    gps_status = "GPS is not enabled";

                    date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
                    time = new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();

                    if (trackingDatabase.getTrackingCount() >= 12) {

                        if (Utils.isNetwork(context)) {
                            Logger.e(TAG,"Permission permitted-ok if count >12 & GPS-off && Network-Ok ");

                            internet_status = "Internet Connection Available";
                            trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                            Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());

                            String xml = makeInsertSuspectRequestXML();
                            Logger.e("xml      " + xml);
                            String[] key2 = {"Doc"};
                            String[] value2 = {xml};
                            apiManager.set_interface_context_post(key2, value2, "URL_INSERT_ATTENDANCE_TRACKING", ServiceUrls.URL_INSERT_ATTENDANCE_TRACKING);
                            Log.e(TAG, "Post INSERT_ATTENDANCE_TRACKING");
                        } else {
                            Logger.e(TAG,"Permission permitted-ok if count >12 & GPS-off && Network-Off ");
                            internet_status = "No Internet Connection";
                            trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                            Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());
                        }
                    } else {
                        Logger.e("Records are less than 12");

                        if (Utils.isNetwork(context)) {
                            internet_status = "Internet Connection Available";
                        } else {
                            internet_status = "No Internet Connection";
                        }
                        Log.e(TAG,"Permission permitted-ok if count < 12");
                        trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                        Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());
                    }
                }
            }
        } catch (Exception e) {
            Logger.e("Exception     " + e.toString());
            methodForException(e.toString());
        }
    }

    @Override
    public void onAPIRunningState(int a, String apiName) {

    }

    @Override
    public void onFetchProgress(int progress) {

    }

    @Override
    public void onFetchComplete(String response, String apiName) {
        Log.d(TAG,"onFetchComplete");

        try {
            response = response.substring(76, response.length() - 9);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            if (apiName.equals("URL_INSERT_ATTENDANCE_TRACKING")) {
                MarkAttendanceModel markAttendanceModel = gson.fromJson(response, MarkAttendanceModel.class);
                if (markAttendanceModel.getStatus().toString().equals("Successed")) {
                    Logger.e("success       " + markAttendanceModel.getMessage());
                    trackingDatabase.clearTracking();
                } else {
                    Logger.e("fail       " + markAttendanceModel.getMessage());
                }
            } else if (apiName.equals("URL_MOBILE_ERROR")) {
                AppVersionModel appVersionModel = gson.fromJson(response, AppVersionModel.class);
                if (appVersionModel.getStatus().equals("Successed")) {
                    Logger.e("msg  " + appVersionModel.getMessage());
                } else {
                    Logger.e("msg  " + appVersionModel.getMessage());
                }
            }
        } catch (Exception e) {
            Utils.showLog("Exception", e + "");
            methodForException(e.toString());
        }
    }

    @Override
    public void onFetchFailed(ANError error, String apiName) {
        methodForException(error.getMessage());
    }

    public String makeInsertSuspectRequestXML() {
        String xml = "<NewDataSet>";
        for (int i = 0; i < trackingDatabase.getTrackingCount(); i++) {

            xml = xml + "<SAL_AttendanceTracking_Mst>" +
                    "<pk_AttenId>" + i + "</pk_AttenId>" +
//                    "<fk_empId>" + sessionManager.getEmpId() + "</fk_empId>" + //GU-65
                    "<fk_empId>" + "GU-65" + "</fk_empId>" +
                    "<latitude>" + trackingDatabase.viewTracking().get(i).getLatitude() + "</latitude>" +
                    "<longitude>" + trackingDatabase.viewTracking().get(i).getLongitude() + "</longitude>" +
                    "<locationaddress>" + trackingDatabase.viewTracking().get(i).getLocation() + "</locationaddress>" +
                    "<internetStatus>" + trackingDatabase.viewTracking().get(i).getInternet_status() + "</internetStatus>" +
                    "<gpsStatus>" + trackingDatabase.viewTracking().get(i).getGps_status() + "</gpsStatus>" +
                    "<dated>" + trackingDatabase.viewTracking().get(i).getDate() + "T" + trackingDatabase.viewTracking().get(i).getTime() + "+" + "05:30" + "</dated>" +
                    "</SAL_AttendanceTracking_Mst>";
        }
        xml = xml + "</NewDataSet>";
        return xml;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        Log.d(TAG, "onConnectionConnected");
//        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.showLog("Connection failed", connectionResult.getErrorCode() + "");
    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.e("onLocationChanged chal gya ");

        try {
            if (location == null) {
                Toast.makeText(mcontext, "no location", Toast.LENGTH_LONG).show();
            }
            if (latitude == 0.0 && longitude == 0.0) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Utils.showLog("lat long onLocationChanged", latitude + ", " + longitude + "");

                new GetLocationAsyncFromMap().execute();
            } else {
                Logger.e("lat long have value");
                stopLocationUpdates();
//                displayLocation();
            }
        } catch (Exception e) {
            Logger.e("Exception         " + e.toString());
            methodForException(e.toString());
        }
    }

//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(context, "This device is not supported.", Toast.LENGTH_LONG).show();
////                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    protected synchronized void buildGoogleApiClient() {

       // if(mGoogleApiClient==null)
      //  {
               mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
               mGoogleApiClient.connect();
        // }
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      //  mLocationRequest.setSmallestDisplacement(0);
    }

    private void displayLocation() {
        try {

            mLastLocation = services.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Utils.showLog("mLastLocation", mLastLocation + "");
            if (mLastLocation != null)
            {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                Utils.showLog("lat long displayLocation", latitude + ", " + longitude + "");

                new GetLocationAsyncFromMap().execute();
            }
            else
                {
                Logger.e("Could not get the location. Make sure location is enabled on the device");
                startLocationUpdates();
            }
        } catch (Exception e) {
            Logger.e("Exception         " + e.toString());
            methodForException(e.toString());
        }
    }


    protected void startLocationUpdates() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        services.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    // class AsyncTask
    private class GetLocationAsyncFromMap extends AsyncTask<String, Void, String> {

        StringBuilder str;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            str = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (geocoder.isPresent()) {
                    Address returnAddress = addresses.get(0);
                    String addressLine = returnAddress.getAddressLine(0);
                    String addressLine1 = returnAddress.getAddressLine(1);

                    if (addressLine1 == null) {
                        str.append(addressLine);
                    } else {
                        str.append(addressLine + ", " + addressLine1);
                    }
                } else {
                }
            } catch (Exception e) {
                Logger.e("Exception     " + e.toString());
                methodForException(e.toString());
            }
            return str.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                location_address = result;
                Logger.e("location_address          " + location_address);

                if (trackingDatabase.getTrackingCount() >= 12) {
                    if (Utils.isNetwork(context)) {

                        internet_status = "Internet Connection Available";
                        trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                        Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());

                        String xml = makeInsertSuspectRequestXML();
                        Logger.e("xml      " + xml);
                        String[] key2 = {"Doc"};
                        String[] value2 = {xml};
                        apiManager.set_interface_context_post(key2, value2, "URL_INSERT_ATTENDANCE_TRACKING", ServiceUrls.URL_INSERT_ATTENDANCE_TRACKING);
                        Log.e(TAG, "Post INSERT_ATTENDANCE_TRACKING");
                    } else {

                        internet_status = "No Internet Connection";
                        trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                        Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());
                    }
                } else {
                    Logger.i("Records are less than 12");
                    if (Utils.isNetwork(context)) {
                        internet_status = "Internet Connection Available";
                    } else {
                        internet_status = "No Internet Connection";
                    }
                    trackingDatabase.addTracking(latitude + "", longitude + "", location_address, gps_status, internet_status, date, time);
                    Logger.e("data          " + trackingDatabase.getTrackingCount() + " | " + trackingDatabase.viewTracking());
                }
            } catch (Exception e) {
                Logger.e("Exception     " + e.toString());
                methodForException(e.toString());
            }
        }
    }

    public void methodForException(String exception) {

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            unique_id = telephonyManager.getDeviceId();
            Logger.e("unique_id     " + unique_id);
        } catch (Exception e) {
            Logger.e("Exception     " + e.toString());
        }

        String xml = makeXmlForException(exception);
        Utils.showLog("xml", xml);
        String[] key2 = {"Doc","CompanyCode"};
        String[] value2 = {xml,"prime"};
        apiManager.set_interface_context_post(key2, value2, "URL_MOBILE_ERROR", ServiceUrls.URL_MOBILE_ERROR);
    }

    public String makeXmlForException(String exception) {

        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        String time1 = new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();
        String xml =
                "<NewDataSet>" +
                        "<SAL_Mobile_Error_Mst>" +
                        "<pk_mobileerrorId>" + 0 + "</pk_mobileerrorId>" +
//                        "<fk_empId>" + sessionManager.getEmpId() + "</fk_empId>" +
                        "<fk_empId>" + "GU-65" + "</fk_empId>" +
                        "<dated>" + date1 + "T" + time1 + "+" + "05:30" + "</dated>" +
                        "<devicename>" + Build.MODEL + "</devicename>" +
                        "<deviceID>" + unique_id + "</deviceID>" +
                        "<OSversion>" + Build.VERSION.RELEASE + "</OSversion>" +
                        "<exception>" + exception + "</exception>" +
                        "<Tag>" + "AlarmReceiver" + "</Tag>" +
                        "</SAL_Mobile_Error_Mst>" +
                        "</NewDataSet>";
        return xml;
    }




}