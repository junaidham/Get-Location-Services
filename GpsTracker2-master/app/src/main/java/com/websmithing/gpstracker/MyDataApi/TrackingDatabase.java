package com.websmithing.gpstracker.MyDataApi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.androidnetworking.error.ANError;
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

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class TrackingDatabase implements ApiFetcher {
    public static final String TAG = "TrackingDatabase";

    Context context;
    Realm myRealm;
     ApiManager apiManager;
    SessionManager sessionManager;

    String unique_id = "", emp_id = "";

    public TrackingDatabase(Context context) {
        this.context = context;
        apiManager = new ApiManager(this);
        sessionManager = new SessionManager(context);
        emp_id = sessionManager.getEmpId();
        try {
            myRealm = Realm.getInstance(context);
            Log.d(TAG,"-Realm.getInstance");
        }
//        catch (RealmMigrationNeededException r) {
//            Logger.e("RealmMigrationNeededException         " + r.toString());
//            Realm.deleteRealmFile(context);
//            myRealm = Realm.getInstance(context);
//        }
        catch (Exception e) {
            methodForException(e.toString());
            Logger.e(TAG,"Exception-" + e.toString());
        }
    }

    public void addTracking(String latitude, String longitude, String location, String gps_status, String internet_status, String date, String time) {
        Log.e(TAG,"addTracking");
        try {
            myRealm.beginTransaction();
            Log.d(TAG,"-myRealm.beginTransaction");
            TrackingModel trackingModel = myRealm.createObject(TrackingModel.class);
            Log.d(TAG,"-myRealm.createObject(TrackingModel.class)");
            trackingModel.setLatitude(latitude);
            trackingModel.setLongitude(longitude);
            trackingModel.setLocation(location);
            trackingModel.setGps_status(gps_status);
            trackingModel.setInternet_status(internet_status);
            trackingModel.setDate(date);
            trackingModel.setTime(time);
        } catch (Exception e) {
            Logger.e(TAG,"Exception-" + e);
            methodForException(e.toString());
        } finally {
            myRealm.commitTransaction();
            Log.d(TAG,"-myRealm.commitTransaction");
        }
    }

    public RealmResults<TrackingModel> viewTracking() {
        Log.d(TAG,"-RealmResults -> myRealm.where(TrackingModel.class).findAll()");
        return myRealm.where(TrackingModel.class).findAll();
    }

    public int getTrackingCount() {
        return myRealm.where(TrackingModel.class).findAll().size();
    }

//    public void deleteBusinessTarget(int productId) {
//        if (myRealm.where(BusinessTargetModel.class).equalTo("id", productId).count() == 0) {
//            Toast.makeText(context, "no such element", Toast.LENGTH_SHORT).show();
//        } else {
//            BusinessTargetModel suspectManagementModel = myRealm.where(BusinessTargetModel.class).equalTo("id", productId).findFirst();
//            myRealm.beginTransaction();
//            suspectManagementModel.removeFromRealm();
//            myRealm.commitTransaction();
//            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void clearTracking() {
        Log.e(TAG,"clearTracking");
        try {
            myRealm.beginTransaction();
            myRealm.clear(TrackingModel.class);
        } catch (Exception e) {
            Logger.e("Exception          " + e);
            methodForException(e.toString());
        } finally {
            myRealm.commitTransaction();
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
        try {
            response = response.substring(76, response.length() - 9);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            if (apiName.equals("URL_MOBILE_ERROR")) {
                AppVersionModel appVersionModel = gson.fromJson(response, AppVersionModel.class);
                if (appVersionModel.getStatus().equals("Successed")) {
                    Logger.e(TAG,"-msg " + appVersionModel.getMessage());
                } else {
                    Logger.e(TAG,"-msg " + appVersionModel.getMessage());
                }
            }
        } catch (Exception e) {
            Utils.showLog(TAG,"Exception"+e + "");
            methodForException(e.toString());
        }
    }

    @Override
    public void onFetchFailed(ANError error, String apiName) {

    }

    void methodForException(String exception) {

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            unique_id = telephonyManager.getDeviceId();
            Logger.e(TAG,"unique_id     " + unique_id);
        } catch (Exception e) {
            Logger.e(TAG,"Exception     " + e.toString());
        }

        String xml = makeXmlForException(exception);
        Utils.showLog(TAG,"xml-"+ xml);
        String[] key2 = {"Doc", "CompanyCode"};
        String[] value2 = {xml, "prime"};
        apiManager.set_interface_context_post(key2, value2, "URL_MOBILE_ERROR", ServiceUrls.URL_MOBILE_ERROR);
    }

    public String makeXmlForException(String exception) {
        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        String time1 = new SimpleDateFormat("HH:mm:ss").format(new Date()).toString();
        String xml =
                "<NewDataSet>" +
                        "<SAL_Mobile_Error_Mst>" +
                        "<pk_mobileerrorId>" + 0 + "</pk_mobileerrorId>" +
                        "<fk_empId>" + emp_id + "</fk_empId>" +
                        "<dated>" + date1 + "T" + time1 + "+" + "05:30" + "</dated>" +
                        "<devicename>" + Build.MODEL + "</devicename>" +
                        "<deviceID>" + unique_id + "</deviceID>" +
                        "<OSversion>" + Build.VERSION.RELEASE + "</OSversion>" +
                        "<exception>" + exception + "</exception>" +
                        "<Tag>" + "TrackingDatabase" + "</Tag>" +
                        "</SAL_Mobile_Error_Mst>" +
                        "</NewDataSet>";
        return xml;
    }
}
