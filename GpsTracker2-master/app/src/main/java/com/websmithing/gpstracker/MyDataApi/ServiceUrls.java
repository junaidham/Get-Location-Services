package com.websmithing.gpstracker.MyDataApi;

public class ServiceUrls {


//    https://github.com/atishagrawal/androidbackgroundgps
//    https://stackoverflow.com/questions/21762293/run-gps-as-background-service-and-send-coordinates-to-web-server-php/21763366#21763366https://stackoverflow.com/questions/21762293/run-gps-as-background-service-and-send-coordinates-to-web-server-php/21763366#21763366


    ///////////////////////////////////////////////

    private static String BASE_URL = "http://infojaal.primecomfort.com/WebService/";
    private static String MARK_ATTENDANCE = "MarkAttendance.asmx/";
    private static String LEAVE = "WEB_LeaveMgt.asmx/";

    ///////////////////////////////////////////////

    public static final String URL_LOGIN = BASE_URL + MARK_ATTENDANCE + "NewEmployeeLogin";
    public static final String URL_LOGOUT = BASE_URL + MARK_ATTENDANCE + "NewEmployeeLogout";
    public static final String URL_APP_VERSION = BASE_URL + MARK_ATTENDANCE + "CheckAppVersion";
    public static final String URL_MOBILE_ERROR = BASE_URL + MARK_ATTENDANCE + "InsertMobileError";

    public static String URL_ATTENDANCE_REQUIRED = BASE_URL + MARK_ATTENDANCE + "Attendance_Required";
    //    public static String URL_INSERT_APPLY_ATTENDANCE = BASE_URL + MARK_ATTENDANCE + "InsertApplyAttendance";
    public static String URL_INSERT_APPLY_ATTENDANCE = BASE_URL + MARK_ATTENDANCE + "InsertNewApplyAttendance";
    //    public static String URL_INSERT_ATTENDANCE_TRACKING = BASE_URL + MARK_ATTENDANCE + "InsertAttendanceTracking";
    public static String URL_INSERT_ATTENDANCE_TRACKING = BASE_URL + MARK_ATTENDANCE + "InsertNewAttendanceTracking";
    public static String URL_POPULATE_MONTH = BASE_URL + MARK_ATTENDANCE + "PopulateMonth";
    public static String URL_POPULATE_YEAR = BASE_URL + MARK_ATTENDANCE + "PopulateYear";
    public static String URL_VIEW_ATTENDANCE = BASE_URL + MARK_ATTENDANCE + "PopulateAttendance";
    public static String URL_STAFF_EMPLOYEE = BASE_URL + MARK_ATTENDANCE + "PopulateStaffEmployee";
    public static String URL_GET_LAT_LONG_DATEWISE = BASE_URL + MARK_ATTENDANCE + "GetLatLongDatewise";



    public static String URL_VIEW_LEAVE_REQUEST = BASE_URL + LEAVE + "ViewLeaveRequest";

    public static String URL_GET_HALF_DAY_CLUB = BASE_URL + LEAVE + "GetHalfDayClub";
    public static String URL_GET_HALF_DAY_STATUS = BASE_URL + LEAVE + "GetHalfDayStatus";
    public static String URL_POPULATE_LEAVE_TYPE = BASE_URL + LEAVE + "PopulateLeaveType";
    public static String URL_CALCULATE_DAYS = BASE_URL + LEAVE + "CalculateDays";
    public static String URL_INSERT_APPLY_LEAVE = BASE_URL + LEAVE + "InsertApplyLeave";

    public static String URL_GET_PENDING_LEAVE_APPROVAL = BASE_URL + LEAVE + "GetPendingLeaveApprovalList";
    public static String URL_LEAVE_APPROVAL_STATUS = BASE_URL + LEAVE + "LeaveApprovalStatus";
    public static String URL_APPROVE_LEAVE = BASE_URL + LEAVE + "ApproveApplyLeave";

    ///////////////////////////////////////////////

    public static final String Status = "status";
    public static final String success = "Success";
    public static final String Message = "Message";
    public static final String Data = "Data";

    public static String googlePlaceDirection = "https://maps.googleapis.com/maps/api/directions/json?";
}