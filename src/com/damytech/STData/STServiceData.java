package com.damytech.STData;

public class STServiceData {
	// Service Address
    //public static String serviceAddr = "http://sypic.oicp.net:10200/Service.svc/";
    //public static String serviceAddr = "http://192.168.1.32:10200/Service.svc/";
    public static String serviceAddr = "http://218.25.54.28:10221/Service.svc/";
    //public static String serviceAddr = "http://172.27.35.42:9001/Service.svc/";

    // Error Code
    public static final int ERR_SUCCESS = 0;
    public static final int ERR_SERVER_ERROR = 500;

    // Command List
    public static String cmdRegisterPatient = "RegisterPatient";
    public static String cmdLoginPatient = "LoginPatient";
    public static String cmdAddDiagnInfo = "AddDiagnInfo";
    public static String cmdGetDiagnList = "GetDiagnList";
    public static String cmdGetDiagnDetail = "GetDiagnDetail";

	// Connection Info
	public static int connectTimeout = 4 * 1000; // 5 Seconds
    public static int connectImageTimeout = 19 * 1000; // 5 Seconds
}
