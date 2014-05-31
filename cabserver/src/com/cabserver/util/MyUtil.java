package com.cabserver.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.cabserver.db.DatabaseManager;
import com.cabserver.gson.pojo.CustomerNotificationPojo;
import com.cabserver.parser.GsonJsonParser;
import com.cabserver.pojo.TravelMaster;
import com.google.gson.Gson;

public class MyUtil {

	static final Logger log = Logger.getLogger(com.cabserver.util.MyUtil.class
			.getName());

	public static void main_5(String args[]) {

		try {
			final String username = "nousonwork@gmail.com";
			final String password = "uJkExT2PBC4aq35xyswP6A";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.mandrillapp.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			String receiverId = "nousonwork@gmail.com";

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("VikingTaxee@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(receiverId));
			message.setSubject("Booking Details");
			message.setText("Pick-Up:"
					+ "7300 Gallagher Drive, Minneapolis, MN 55435, USA"
					+ "\nDrop-Off:"
					+ "2701-2799 West 76th Street, Richfield, MN 55423, USA"
					+ "\nCustomer Name:" + "Shankar" + "\nPickUp Time:"
					+ "2014-02-24 12:00:36.0" + "\nApproximate Distance:"
					+ "12.5" + " Miles" + "\nExpected Fare:" + "$ " + 35);
			// + "\n15% Discounted Fare:" + "$"
			// + f.format(((((distValue) * 2.75) + 2.5) * (0.85))));

			Transport.send(message);

			System.out
					.println("sendBookingNotification >> Notification sent to "
							+ receiverId + " .");
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void main_4(String args[]) {

		Calendar cal = Calendar.getInstance();

		cal.set(cal.YEAR, 2014);
		cal.set(cal.MONTH, 1);
		cal.set(cal.DATE, 24);
		cal.set(cal.HOUR_OF_DAY, 15);
		cal.set(cal.MINUTE, 15 + (11 * 60) + 30);
		cal.set(cal.SECOND, 24);

		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
		formatter.setTimeZone(TimeZone.getDefault());

		String dateStr = formatter.format(cal.getTime());
		System.out.println("getCurrentDateFormattedString >> dateStr = "
				+ dateStr);

		System.out.println(1000 * 60 * 60 * 11 + 1000 * 60 * 30);

	}

	public static void main_3(String args[]) {

		// String jsonResult =
		// "{\"subject\":\"Hi VikingTaxee Drivers\",\"body\":\"This is a test message. Have a good Day.\",\"drivers\":[\"11\",\"12\",\"13\",\"14\"]}";

		String jsonResult = "{\"subject\":\"Hi VikingTaxee Drivers\",\"body\":\"This is a test message. Have a good Day.\",\"users\":[\"ALL\"]}";

		Gson gson = new Gson();
		// DriverNotificationPojo result =
		// gson.fromJson(jsonResult,DriverNotificationPojo.class);

		CustomerNotificationPojo result = gson.fromJson(jsonResult,
				CustomerNotificationPojo.class);

		System.out.println("Subject = " + result.subject);

		System.out.println("Body = " + result.body);

		/*
		 * System.out.println("Drivers="+ result.drivers);
		 * 
		 * System.out.println("Drivers[0]="+ result.drivers[0]);
		 */

		System.out.println("Users=" + result.users);

		System.out.println("Users[0]=" + result.users[0]);

	}

	public static void main_2(String[] args) throws InterruptedException,
			NumberFormatException, IOException {

		double distValue = ((Double
				.parseDouble((GsonJsonParser
						.getDistanceByAddress(
								"MSP Airport Terminal 1  Fort Snelling  MN  United States",
								"Downtown West, Minneapolis, MN, USA")).trim())) / (1000)) * 0.62;
		distValue = Double.valueOf(new DecimalFormat("#.##").format(distValue));
		// distValue = distValue*0.62;

		// fromAddr: MSP Airport Terminal 1 Fort Snelling MN United States
		// toAddr: Downtown Minneapolis Minneapolis MN United States

		System.out.println(distValue);

	}

	public static void main_1(String[] args) throws InterruptedException {

		TravelMaster tm = new TravelMaster();

		tm.setFrom("7300 Gallagher Drive, Minneapolis, MN 55435, USA");
		tm.setTo("3880-3942 West 76th Street, Edina, MN 55435, USA");
		tm.setDateTime(new Date());
		tm.setTravellerName("Lavlesh");

		// sendBookingNotification(tm,"9525647320@tmomail.net");

		CacheBuilder.buildCache();

		for (int i = 1; i < 101; i++) {
			sendBookingNotification(tm, "shankar.mohanty@gmail.com");
			// sendBookingNotification(tm,"nousonwork@gmail.com");

			Thread.sleep(1000);
		}

	}
	
	
	
	public static HashMap<String,String> getDateComponents(String datetime){

		HashMap<String, String> dateComponents = new HashMap<String, String>();

		String tmpArry[] = datetime.split(" ");

		dateComponents.put("date", tmpArry[0]);
		dateComponents.put("month", tmpArry[1]);
		dateComponents.put("year", tmpArry[2]);
		dateComponents.put("hour", tmpArry[4].split(":")[0]);
		dateComponents.put("min", tmpArry[4].split(":")[1]);
		dateComponents.put("ampm", tmpArry[5]);

		return dateComponents;
	}

	
	
	public static boolean getIsDatePassed(Date date){
		
		boolean status = false;
		
		long dateValue = date.getTime();
		
		long currDateValue = getCalanderFromDateStr(getCurrentDateFormattedString()).getTimeInMillis();
		
		if(dateValue < currDateValue){
			status = true;
		}else{
			status = false;
		}
		
		return status;
	}
	
	public static void main(String args[]) {
		
		System.out.println(getCurrentDateFormattedString());
	}

	public static String getCurrentDateFormattedString() {

		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
		formatter.setTimeZone(TimeZone.getTimeZone(ConfigDetails.constants.get("LOCALE_VALUE")));

		String dateStr = formatter.format(new Date());
		//log.info("getCurrentDateFormattedString >> dateStr = " + dateStr);

		return dateStr;
	}

	public static String getDateFormattedStringbyDate(Date date) {

		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
		formatter.setTimeZone(TimeZone.getTimeZone(ConfigDetails.constants.get("LOCALE_VALUE")));

		return formatter.format(date);
	}

	public static Calendar getCalanderFromDateStr(String formattedTimeStr) {

		Calendar cal = Calendar.getInstance();
		
		log.info("getCalanderFromDateStr >> FormattedTimeStr = "
				+ formattedTimeStr);

		String timeArry[] = formattedTimeStr.split(" ");

		// System.out.println("date = "+ timeArry[0]);

		String dateArry[] = timeArry[0].split("/");
		cal.set(cal.YEAR, Integer.parseInt(dateArry[0]));
		cal.set(cal.MONTH, Integer.parseInt(dateArry[1]) - 1);
		cal.set(cal.DATE, Integer.parseInt(dateArry[2]));

		String timeValueArry[] = timeArry[1].split(":");
		cal.set(cal.HOUR, Integer.parseInt(timeValueArry[0]));
		cal.set(cal.MINUTE, Integer.parseInt(timeValueArry[1]));
		cal.set(cal.SECOND, Integer.parseInt(timeValueArry[2]));

		if (timeArry[2].equalsIgnoreCase("AM")) {
			cal.set(cal.AM, cal.AM);
		} else {
			cal.set(cal.PM, cal.PM);
		}

		return cal;
	}

	public static synchronized void sendBookingNotification(TravelMaster tm,
			String receiverId) {

		try {

			double distValue = ((Double.parseDouble((GsonJsonParser
					.getDistanceByAddress(tm.getFrom(), tm.getTo())).trim())) / (1000)) * 0.62;
			distValue = Double.valueOf(new DecimalFormat("#.##")
					.format(distValue));

			/*log.info("sendBookingNotification >> Distance between PickUp and DropOff is = "
					+ distValue);*/
			DecimalFormat f = new DecimalFormat("##.00");
			String fare = f.format((((distValue) * 2.75) + 2.5));

			DatabaseManager
					.updateDistanceAndFare(distValue, Double.parseDouble(fare),
							Long.parseLong(tm.getBookingId()));

			if (Boolean.parseBoolean(ConfigDetails.constants.get("LOCAL_MAIL_SEND"))) {

				Message message = new MimeMessage(CacheBuilder.session);
				message.setFrom(new InternetAddress("VikingTaxee@gmail.com"));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(receiverId));
				message.setSubject("Booking Details");
				message.setText("Pick-Up: " + tm.getFrom() + "\nDrop-Off: "
						+ tm.getTo() + "\nCustomer Name: "
						+ tm.getTravellerName() + "\nPickUp Time: "
						+ tm.getBookingDateTime() + "\nApproximate Distance: "
						+ distValue + " Miles" + "\nExpected Fare: " + "$ "
						+ fare);
				// + "\n15% Discounted Fare:" + "$"
				// + f.format(((((distValue) * 2.75) + 2.5) * (0.85))));

				Transport.send(message);

				/*log.info("sendBookingNotification >> Notification sent to "
						+ receiverId + " .");*/

			} else {
				log.info("sendBookingNotification >> Adding receiverId "
						+ receiverId + " .");

				tm.setTotalDistanceTravelled(distValue + "");
				tm.setFromMailId("VikingTaxee@gmail.com");
				tm.setToMailId(receiverId);
				tm.setSubject("Booking Details");
				tm.setFare(fare);
				tm.setMailType(Integer.parseInt(ConfigDetails.constants.get("MAIL_TYPE_BOOKING")));

				CacheBuilder.mailSendingDataMap.put(
						(long) new Random().nextInt(100000),
						TravelMaster.getInstance(tm));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getMonth(String monthStr) {

		if (monthStr.equalsIgnoreCase("January")) {
			return 1;
		} else if (monthStr.equalsIgnoreCase("February")) {
			return 2;
		} else if (monthStr.equalsIgnoreCase("March")) {
			return 3;
		} else if (monthStr.equalsIgnoreCase("April")) {
			return 4;
		} else if (monthStr.equalsIgnoreCase("May")) {
			return 5;
		} else if (monthStr.equalsIgnoreCase("June")) {
			return 6;
		} else if (monthStr.equalsIgnoreCase("July")) {
			return 7;
		} else if (monthStr.equalsIgnoreCase("August")) {
			return 8;
		} else if (monthStr.equalsIgnoreCase("September")) {
			return 9;
		} else if (monthStr.equalsIgnoreCase("October")) {
			return 10;
		} else if (monthStr.equalsIgnoreCase("November")) {
			return 11;
		} else if (monthStr.equalsIgnoreCase("December")) {
			return 12;
		} else {
			return 13;
		}

	}

	public static String getMobileOperatorDomain(String operatorName) {

		if (operatorName.equalsIgnoreCase("Sprint")) {
			return ConfigDetails.constants.get("SMS_SPRINT");
		} else if (operatorName.equalsIgnoreCase("ATnT")) {
			return ConfigDetails.constants.get("SMS_ATnT");
		} else if (operatorName.equalsIgnoreCase("Verizon")) {
			return ConfigDetails.constants.get("SMS_VERIZON");
		} else if (operatorName.equalsIgnoreCase("TMobile")) {
			return ConfigDetails.constants.get("SMS_TMOBILE");
		} else if (operatorName.equalsIgnoreCase("VirginMobile")) {
			return ConfigDetails.constants.get("SMS_VIRGIN_MOBILE");
		} else if (operatorName.equalsIgnoreCase("BoostMobile")) {
			return ConfigDetails.constants.get("SMS_BOOST_MOBILE");
		} else {
			return "";
		}

	}

}
