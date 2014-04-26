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
import com.cabserver.pojo.TravelMaster;
import com.cabserver.util.Constants;

@DisallowConcurrentExecution
public class CheckingBookingConfirmationJob implements Job, org.quartz.InterruptableJob {

	static final Logger log = Logger
			.getLogger(com.cabserver.scheduler.CheckingBookingConfirmationJob.class.getName());

	private volatile boolean isJobInterrupted = false;
	private JobKey jobKey = null;
	private volatile Thread thisThread;

	public CheckingBookingConfirmationJob() {
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		thisThread = Thread.currentThread();
		//log.info("Thread name of the current job: " + thisThread.getName());

		jobKey = context.getJobDetail().getKey();
		//log.info("Thread name="+thisThread.getName()+", Job " + jobKey + " executing at " + new Date());

		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			//String fromKeyStr = data.getString("fromKey");
			TravelMaster tm = (TravelMaster) data.get("tm");
			jobKey = context.getJobDetail().getKey();
			//log.info("Executing Taxi Booking for from address = " + fromKeyStr);

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
				//log.info("Job " + jobKey + " did not complete");
			} else {
				//log.info("Job " + jobKey + " completed at " + new Date());
			}
		}

	}

	public synchronized boolean bookingAlgo(TravelMaster tm) {
		
		boolean bookingStatus = false;
		
		TravelMaster tm1 = DatabaseManager.searchBookingDetailsByBookingId(tm.getBookingId());
		
		if(tm1!= null && (tm1.getBookingStatusCode().equalsIgnoreCase(Constants.BOOKING_CONFORMED_CODE) 
				|| tm1.getBookingStatusCode().equalsIgnoreCase(Constants.BOOKING_ON_THE_WAY_CODE)
				|| tm1.getBookingStatusCode().equalsIgnoreCase(Constants.BOOKING_DROPPED_CODE) 
				|| tm1.getBookingStatusCode().equalsIgnoreCase(Constants.BOOKING_DENIED_CODE)) ){
			bookingStatus = true;
		}else{
			bookingStatus = false;
			if(tm1!= null){
				if(tm1.getDriverId() != null && !(tm1.getDriverId().equalsIgnoreCase("0"))){
					tm1.setBookingStatus(Constants.BOOKING_DRIVER_DID_NOT_ACCEPTED_MSG);
				}else{
					tm1.setBookingStatus(Constants.BOOKING_FAILED_MSG);
				}
				
				tm1.setBookingStatusCode(Constants.BOOKING_FAILED_CODE);
				boolean driverRemoveFromTravelMasterStatus = DatabaseManager.removeDriverFromBookingInTravelMaster(tm1);
				
				String driverId = tm1.getDriverId();
				if(driverId != null && !driverId.equalsIgnoreCase("0")){
					DatabaseManager.deleteBookingIdFromDriverMaster(driverId);
				}
				
				
			}
			
		}
		
		return bookingStatus;
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		//log.info("Job " + jobKey + "  -- INTERRUPTING --");
		isJobInterrupted = true;
		if (thisThread != null) {
			// this call causes the ClosedByInterruptException to happen
			thisThread.interrupt();
		}

	}

}