package com.cabserver.scheduler;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;

import com.cabserver.db.DatabaseManager;
import com.cabserver.pojo.DriverMaster;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.pojo.UserMaster;
import com.cabserver.util.Constants;
import com.cabserver.util.MyUtil;

@DisallowConcurrentExecution
public class ManualBookingActivationJob implements Job,
		org.quartz.InterruptableJob {

	static final Logger log = Logger
			.getLogger(com.cabserver.scheduler.ManualBookingActivationJob.class
					.getName());

	private volatile boolean isJobInterrupted = false;
	private JobKey jobKey = null;
	private volatile Thread thisThread;

	public ManualBookingActivationJob() {
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		thisThread = Thread.currentThread();
		// log.info("Thread name of the current job: " + thisThread.getName());

		jobKey = context.getJobDetail().getKey();
		// log.info("Thread name="+thisThread.getName()+", Job " + jobKey +
		// " executing at " + new Date());

		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			// String fromKeyStr = data.getString("fromKey");
			TravelMaster tm = (TravelMaster) data.get("tm");
			jobKey = context.getJobDetail().getKey();

			if (bookingAlgo(tm)) {
				try {
					context.getScheduler().interrupt(jobKey);

				} catch (UnableToInterruptJobException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isJobInterrupted) {
				// log.info("Job " + jobKey + " did not complete");
			} else {
				// log.info("Job " + jobKey + " completed at " + new Date());
			}
		}

	}

	public synchronized boolean bookingAlgo(TravelMaster tm) {

		boolean manualBookingActivationStatus = false;

		manualBookingActivationStatus = DatabaseManager
				.updateManualBookingActiveStatus(tm.getBookingId());
		log.info("bookingAlgo >> manualBookingActivationStatus in TravelMaster ="
				+ manualBookingActivationStatus);
		manualBookingActivationStatus = DatabaseManager
				.assignManualBookingDriverToDriverMaster(tm.getBookingId(),
						tm.getDriverId());
		log.info("bookingAlgo >> manualBookingActivationStatus in DriverMaster ="
				+ manualBookingActivationStatus);

		/********* Mail and SMS sending Logic Start ***/

		DriverMaster dm = DatabaseManager
				.getDriverDetails(tm.getDriverId());

		// Sending Mail and SMS to Driver
		try {

			String driverMailId = dm.getMailId();
			if (driverMailId != null
					&& driverMailId.length() > 1) {
				MyUtil.sendBookingNotification(tm,
						driverMailId);
				
				Thread.sleep(2000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String driverMobileNumber = dm
					.getPhoneNumber();
			String mobileDomain = MyUtil
					.getMobileOperatorDomain(dm
							.getMobileOperator());
			MyUtil.sendBookingNotification(tm,
					driverMobileNumber + mobileDomain);
			
			Thread.sleep(2000);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sending Mail and SMS to Customer
		try {

			String travellerMailId = (DatabaseManager
					.getEmailIdByPhone(tm
							.getTravellerPhone()))
					.getMailId();
			if (travellerMailId != null
					&& travellerMailId.length() > 1) {
				MyUtil.sendBookingNotification(tm,
						travellerMailId);
				
				Thread.sleep(2000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			MyUtil.sendBookingNotification(
					tm,
					tm.getTravellerPhone()
							+ MyUtil.getMobileOperatorDomain(tm
									.getMobileOperator()));
			
			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sending MAil and SMS to Admin
		UserMaster umAdmin = null;
		UserMaster userDetails = null;
		try {

			userDetails = DatabaseManager
					.getUserDetailsByUserId(tm.getUserId());
			umAdmin = DatabaseManager
					.getUserDetailsByUserId(userDetails
							.getAdminId());
			MyUtil.sendBookingNotification(tm,
					umAdmin.getMailId());
			
			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			MyUtil.sendBookingNotification(
					tm,
					umAdmin.getPhone()
							+ MyUtil.getMobileOperatorDomain(umAdmin
									.getMobileOperator()));

		} catch (Exception e) {
			e.printStackTrace();
		}

		/********* Mail and SMS sending Logic End ***/

		return manualBookingActivationStatus;
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// log.info("Job " + jobKey + "  -- INTERRUPTING --");
		isJobInterrupted = true;
		if (thisThread != null) {
			// this call causes the ClosedByInterruptException to happen
			thisThread.interrupt();
		}

	}

}
