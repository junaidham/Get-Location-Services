package com.websmithing.gpstracker.MyDataApi;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static final String IS_LOGIN = "IsLoggedIn";

    //    fk_emp_id
    public static String empId = "empId";
    //    emp_id
    public static String empId1 = "empId1";
    public static String empCode = "empCode";
    public static String empName = "empName";
    public static String compName = "compName";
    public static String address = "address";
    public static String locName = "locName";
    public static String grade = "grade";
    public static String department = "department";
    public static String designation = "desig";
    public static String compId = "compId";
    public static String isLocReq = "LocReq";
    public static String cLat = "CLat";
    public static String cLong = "CLong";
    public static String mobileDevice = "MobileDeviceID";
    public static String attendanceSource = "AttendanceSource";
    public static String menuText = "MenuText";
    public static String finId = "finId";

    public SessionManager(Context context) {
        pref = context.getSharedPreferences("InfojaalPrime", Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public void createLogin() {
        editor.putBoolean(IS_LOGIN, true);
    }

    public void saveLogin(String Key, String Value) {
        editor.putString(Key, Value);
        editor.commit();
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(IS_LOGIN, false);
    }

    public void createLogout() {
        editor.clear();
        editor.commit();
    }

    public String getCompId() {
        return pref.getString(compId, "");
    }

    public String getEmpName() {
        return pref.getString(empName, "");
    }

    public String getEmpCode() {
        return pref.getString(empCode, "");
    }

    public String getEmpId() {
        return pref.getString(empId, "");
    }

    public String getFinId() {
        return pref.getString(finId, "");
    }

    public String getEmpId1() {
        return pref.getString(empId1, "");
    }

    public String getIsLocReq() {
        return pref.getString(isLocReq, "");
    }

    public String getCLat() {
        return pref.getString(cLat, "");
    }

    public String getCLong() {
        return pref.getString(cLong, "");
    }

    public String getMobileDevice() {
        return pref.getString(mobileDevice, "");
    }

    public String getAttendanceSource() {
        return pref.getString(attendanceSource, "");
    }

    public String getDesignation() {
        return pref.getString(designation, "");
    }

    public String getMenuText() {
        return pref.getString(menuText, "");
    }



}


