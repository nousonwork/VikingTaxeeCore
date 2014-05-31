package com.cabserver.util;

import java.util.HashMap;

public class ConfigDetails {
	
	//public static final String PROXY_HOST = "rtecproxy.ril.com";
	//public static final int PROXY_PORT = 8080;
	//public static final String PROXY_CATEGORY = "SECURE_IMPACT_PROXY";
	//public static final String PROXY_HOST_KEY = "proxy_host_key";
	//public static final String PROXY_PORT_KEY = "proxy_port_key";
	//public static final String CABGURU_SERVER_IP_PORT = "10.60.127.6:9797";
	//public static final String CABGURU_SERVER_IP_PORT = "10.0.2.2:9797";
	//public static final String CABGURU_SERVER_IP_PORT = "115.241.234.156:9797";
	
public static HashMap<String, String> constants;
	
	static{
		constants = new HashMap<>();
	}
	
	
	
	/*public static final String DATABASE_IP="127.0.0.1";	
	public static final String DATABASE_PORT = "3306";
	public static final String DATABASE_NAME = "cabguru";
	public static final String DATABASE_USER = "root";
	public static final String DATABASE_PASSWORD = "root";
	
	
	public static String BOOKING_SCHEDULED_CODE="201";   // Allow Update
	public static String BOOKING_CONFORMED_CODE="202";   // Allow Update
	public static String BOOKING_ON_THE_WAY_CODE="203"; 		 // NOT Allowed Update
	public static String BOOKING_DROPPED_CODE="204";  			 // NOT Allowed update
	public static String BOOKING_CANCELLED_CODE="205"; 			 // NOT Allowed update	
	public static String BOOKING_FAILED_CODE="404";     // Allow Update
	public static String BOOKING_DENIED_CODE="405";     // Allow Update
	public static String INCORRECT_ADDRESS_CODE="406";  // Allow Update
	
	public static String BOOKING_SCHEDULED_MSG="BOOKING SCHEDULED";
	public static String BOOKING_DRIVER_ACCEPT_PENDING_MSG="DRIVER ACCEPTANCE PENDING";
	public static String BOOKING_CONFORMED_MSG="BOOKING CONFIRMED";
	public static String BOOKING_ON_THE_WAY_MSG="ON THE WAY";
	public static String BOOKING_DROPPED_MSG="DROPPED";
	public static String BOOKING_FAILED_MSG="BOOKING FAILED";
	public static String BOOKING_DRIVER_DID_NOT_ACCEPTED_MSG="DRIVER DID NOT ACCEPTED";
	public static String BOOKING_DENIED_MSG="BOOKING DENIED";
	public static String INCORRECT_ADDRESS_MSG="INCORRECT ADDRESS";
	
	
	public static String DRIVER_STATUS_FREE_STR="FREE";
	public static String DRIVER_STATUS_BUSY_STR="BUSY";
	public static String DRIVER_STATUS_WAITING_STR="WAITING";
	public static String DRIVER_STATUS_ON_THE_WAY_FREE_STR="ON-THE-WAY";
	
	public static String GOOGLE_MAPS_API = "maps.googleapis.com";
	//public static String GOOGLE_MAPS_API = "rssg04.ril.com:8080/v27";
	
	
	//////////// IViking scheduler settings
	public static int MIN_BOOKING_TIME = 30;
	public static int MAX_BOOKING_TIME = 45;
	public static int BOOKING_ACTIVATION_TIME = 39;
	public static String MIN_BOOKING_TIME_ERROR_MSG = "Booking Time should be atleast 45 min ahead.";
	public static String ADMIN_MIN_BOOKING_TIME_ERROR_MSG = "Booking Time should be atleast 5 min ahead.";
	public static int ADMIN_ADV_MANUAL_BOOKING_MIN_TIME = 60;
	public static int MANUAL_BOOKING_ACTIVATION_TIME = 58;
	
	
	public static int MANUAL_BOOKING_TYPE = 1;
	public static int SCHEDULED_BOOKING_TYPE = 2;
	
	public static int MANUAL_BOOKING_DEACTIVE_STATUS = 10;
	public static int MANUAL_BOOKING_ACTIVE_STATUS = 11;
	
	public static int SCHEDULED_BOOKING_DEACTIVE_STATUS = 20;
	public static int SCHEDULED_BOOKING_ACTIVE_STATUS = 21;
	
	
	//// US Mobile SMS domains
	
	public static String SMS_SPRINT = "@sprintpaging.com";
	public static String SMS_ATnT = "@txt.att.net";
	public static String SMS_VERIZON = "@vtext.com";
	public static String SMS_TMOBILE = "@tmomail.net";
	public static String SMS_VIRGIN_MOBILE = "@vmobl.com";
	public static String SMS_BOOST_MOBILE = "@myboostmobile.com";
	
	
	public static int DRIVER_CATEGORY_COMPANY = 1;
	public static int DRIVER_CATEGORY_NON_COMPANY = 0;
	
	
	public static int MAIL_TYPE_BOOKING = 1;
	public static int MAIL_TYPE_FORGOT_PASSWORD = 2;
	
	public static String LOCALE_VALUE = "America/Chicago";
	
	//public static long TIME_DIFF = 1000*60*60*11 + 1000*60*30;
	
	public static long TIME_DIFF = -(1000*60*60*2) ;
	
	public static boolean LOCAL_MAIL_SEND = true;
	
	public static long DRIVER_MAP_ICON_DISABLE_TIME = (1000 * 60 * 60 * 12) ;*/
	
}
