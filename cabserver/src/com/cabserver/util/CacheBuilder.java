package com.cabserver.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.log4j.Logger;

import com.cabserver.db.DatabaseManager;
import com.cabserver.pojo.DriverMaster;
import com.cabserver.pojo.MailMaster;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.pojo.UserMaster;
import com.cabserver.scheduler.TaxiBookingQuartz;

public class CacheBuilder {
	static final Logger log = Logger
			.getLogger(com.cabserver.util.CacheBuilder.class.getName());

	public static Hashtable<Long, DriverMaster> driversDataMap;
	public static Hashtable<Long, TravelMaster> bookingsDataMap;
	public static Hashtable<Long,TravelMaster> mailSendingDataMap;
	public static Hashtable<Long,MailMaster> mailTextDataMap;
	public static Hashtable<Long,ArrayList<DriverMaster>> driverNotificationDataMap;
	public static Hashtable<Long,ArrayList<UserMaster>> customerNotificationDataMap;	
	public static Session session;

	// public static void main(String args[]) {

	public static void buildCache() {
		// log.info("buildCache");
		try {
			
			final String username = "nousonwork@gmail.com";
			final String password = "uJkExT2PBC4aq35xyswP6A";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.mandrillapp.com");
			props.put("mail.smtp.port", "587");

			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					});	
			
			driversDataMap = new Hashtable<Long, DriverMaster>();
			bookingsDataMap = new Hashtable<Long, TravelMaster>();
			
			mailSendingDataMap = new Hashtable<Long,TravelMaster>();
			mailTextDataMap = new Hashtable<Long,MailMaster>();
			driverNotificationDataMap = new Hashtable<>();
			customerNotificationDataMap = new Hashtable<>();
			
			ArrayList<DriverMaster> driverList = DatabaseManager.getAllCompanyDriverDetails();
			if (driverList.size() > 0) {
				for (DriverMaster dm : driverList) {
					if(dm.getDriverCategory() == 1){
						driversDataMap.put(Long.parseLong(dm.getDriverId()), dm);
					}
					
					/*
					 * log.info("Inside cabserver >> buildCache >>  driverId ="
					 * + dm.getDriverId() + ", Name ="+ dm.getFirstName());
					 */
				}

				log.info("buildCache >> driversDataMap size ="
						+ driversDataMap.size());

			}

			ArrayList<TravelMaster> bookingList = DatabaseManager
					.getBookingsToMakeCacheOnStartUp();
			if (bookingList.size() > 0) {
				for (TravelMaster tm : bookingList) {
					
					/*log.info("buildCache >> adding to bookingsDataMap bookingId ="
							+ tm.getBookingId());*/
					
					bookingsDataMap.put(Long.parseLong(tm.getBookingId()), tm);
				}
				log.info("buildCache >> bookingsDataMap size ="
						+ bookingsDataMap.size());

			}

			ArrayList<TravelMaster> bookingListToReschedule = DatabaseManager
					.getBookingsToRescheduleOnStartUp();
			if (bookingListToReschedule.size() > 0) {
				for (TravelMaster tm : bookingListToReschedule) {
					int bookingStatus = TaxiBookingQuartz
							.scheduleTaxiBookingJob(tm);
								
					
					if(bookingStatus > 0 && bookingsDataMap.contains(Long.parseLong(tm.getBookingId()))){
						log.info("buildCache >> adding to bookingsDataMap after scheduling, bookingId ="
								+ tm.getBookingId());
						bookingsDataMap.put(Long.parseLong(tm.getBookingId()), tm);
					}

				}

			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
