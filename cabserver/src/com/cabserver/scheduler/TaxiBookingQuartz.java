package com.cabserver.scheduler;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.cabserver.pojo.TravelMaster;
import com.cabserver.util.CacheBuilder;
import com.cabserver.util.Constants;
import com.cabserver.util.MyUtil;

public class TaxiBookingQuartz {

	static final Logger log = Logger
			.getLogger(com.cabserver.scheduler.TaxiBookingQuartz.class
					.getName());

	public static Scheduler scheduler = null;

	static {

		try {
			scheduler = new StdSchedulerFactory().getScheduler();

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {

			for (int i = 1; i < 10; i++) {

				Calendar bookingTime = Calendar.getInstance();
				bookingTime.set(Calendar.HOUR_OF_DAY, 22);
				bookingTime.set(Calendar.MINUTE, 32 + i);

				int bookingType = validateTime(bookingTime.getTimeInMillis());
				log.info("bookingType = " + bookingType + "Counter = " + i);

				JobDetail job = JobBuilder
						.newJob(TaxiBookingJob.class)
						.withIdentity(
								"BookingJob_" + bookingTime.getTimeInMillis(),
								"Group1").build();
				job.getJobDataMap().put(
						"fromKey",
						"WING-D, Agrawal Road, Nirmal Nagar, Mulund West, Mumbai, "
								+ "Maharashtra 400082, India");

				if (bookingType == 1 || bookingType == 2) {
					triggerBooking(bookingTime.getTime(), job, bookingType,
							"UrgentBooking", "UrgentBookingGroup", null);
				} else if (bookingType == 3) {
					triggerBooking(bookingTime.getTime(), job, bookingType,
							"DelayedBooking", "DelayedBookingGroup", null);
				}

				Thread.sleep(10);

			}

			/*
			 * Calendar bookingTime = Calendar.getInstance();
			 * bookingTime.set(Calendar.HOUR_OF_DAY, 19);
			 * bookingTime.set(Calendar.MINUTE, 32);
			 * 
			 * int bookingType = validateTime(bookingTime.getTimeInMillis());
			 * log.info("bookingType = " + bookingType);
			 * 
			 * if(bookingType == 1 || bookingType == 2){
			 * scheduleUrgentBooking(); }else if(bookingType == 3){
			 * scheduleDelayedBooking(bookingTime.getTime()); }
			 */

			/*
			 * Calendar bookingTime1 = Calendar.getInstance();
			 * bookingTime1.set(Calendar.HOUR_OF_DAY, 19);
			 * bookingTime1.set(Calendar.MINUTE, 35);
			 * 
			 * int bookingType1 = validateTime(bookingTime1.getTimeInMillis());
			 * log.info("bookingType1 = " + bookingType1);
			 * 
			 * if(bookingType1 == 1 || bookingType1 == 2){
			 * scheduleUrgentBooking(); }else if(bookingType1 == 3){
			 * scheduleDelayedBooking(bookingTime1.getTime()); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main_1(String[] args) {

		try {
			// Grab the Scheduler instance from the Factory

			JobDetail job = JobBuilder.newJob(TaxiBookingJob.class)
					.withIdentity("HelloJob", "group1").build();

			/*
			 * Trigger trigger = TriggerBuilder.newTrigger()
			 * .withIdentity("dummyTriggerName", "group1") .withSchedule(
			 * SimpleScheduleBuilder.simpleSchedule()
			 * .withIntervalInSeconds(5).repeatForever()) .build();
			 */

			// HolidayCalendar cal = new HolidayCalendar();
			// cal.addExcludedDate( someDate );
			// cal.addExcludedDate( someOtherDate );

			Calendar rightNow = Calendar.getInstance();

			log.info(rightNow.getTime().getHours() + ":"
					+ rightNow.getTime().getMinutes());

			rightNow.setTimeInMillis((rightNow.getTimeInMillis() + (1000 * 60 * 1)));

			log.info(rightNow.getTime().getHours() + ":"
					+ rightNow.getTime().getMinutes());

			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity("dummyTriggerName", "group1")
					.withSchedule(
							SimpleScheduleBuilder.repeatSecondlyForever(5)) // execute
																			// job
																			// daily
																			// at
																			// 9:30
					.startAt(rightNow.getTime()).build();

			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			// scheduler.addCalendar("myHolidays", cal, false);
			scheduler.start();
			scheduler.scheduleJob(job, trigger);

			scheduler.start();

			// scheduler.shutdown();

		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	public static int scheduleTaxiBookingJob(TravelMaster tm) {
		int bookingType = 0;
		try {

			Calendar bookingTime = Calendar.getInstance();
			bookingTime.setTimeInMillis((tm.getDateTime().getTime())
					+ Constants.TIME_DIFF);
			log.info("scheduleTaxiBookingJob >> Server adjusted bookingTime = "
					+ new Date(bookingTime.getTimeInMillis()));
			log.info("scheduleTaxiBookingJob >> booking category(Scheduled =2,Manual=1) Type = "
					+ tm.getBookingType());

			try {

				TravelMaster tmFrmCache = CacheBuilder.bookingsDataMap.get(Long
						.parseLong(tm.getBookingId()));

				if (tmFrmCache != null) {
					boolean schedulerRemovedStatus = TaxiBookingQuartz
							.unscheduleBooking(tmFrmCache);
					/*log.info("scheduleTaxiBookingJob schedulerRemovedStatus ="
							+ schedulerRemovedStatus);*/
				}

			} catch (Exception e) {
				log.info("updateBookingStatus exception =" + e.getMessage());
			}

			if (tm.getBookingType() == Constants.SCHEDULED_BOOKING_TYPE) {

				bookingType = validateTime(bookingTime.getTimeInMillis());
				log.info("scheduleTaxiBookingJob >> booking Time Delay Type = "
						+ bookingType);
				JobDetail job = JobBuilder
						.newJob(TaxiBookingJob.class)
						.withIdentity(
								"BookingJob_" + bookingTime.getTimeInMillis(),
								"Group1").build();
				job.getJobDataMap().put("fromKey", tm.getFrom());
				job.getJobDataMap().put("tm", tm);
				JobDetail checkingBookingConfirmationJob = JobBuilder
						.newJob(CheckingBookingConfirmationJob.class)
						.withIdentity(
								"CheckingBookingConfirmationJob_"
										+ bookingTime.getTimeInMillis(),
								"Group1").build();
				checkingBookingConfirmationJob.getJobDataMap().put("tm", tm);
				if (bookingType == 1 || bookingType == 2) {
					Calendar currTime = Calendar.getInstance();
					int currentMin = currTime.get(Calendar.MINUTE);
					currTime.set(Calendar.MINUTE, currentMin + 1);
					triggerBooking(currTime.getTime(), job, bookingType,
							"UrgentBooking", "UrgentBookingGroup", tm);
					currTime.set(Calendar.MINUTE, currentMin + 3);
					triggerBooking(currTime.getTime(),
							checkingBookingConfirmationJob, bookingType,
							"CheckingBookingConfirmationJob",
							"CheckingBookingConfirmationJobGroup", tm);
				} else if (bookingType == 3) {
					Calendar deleyedBookingTime = Calendar.getInstance();
					deleyedBookingTime
							.setTimeInMillis(((bookingTime.getTimeInMillis()) - (1000 * 60 * (Constants.BOOKING_ACTIVATION_TIME))));
					triggerBooking(deleyedBookingTime.getTime(), job,
							bookingType, "DelayedBooking",
							"DelayedBookingGroup", tm);

					int bookingTimeMin = deleyedBookingTime
							.get(Calendar.MINUTE);
					deleyedBookingTime.set(Calendar.MINUTE, bookingTimeMin + 3);
					triggerBooking(deleyedBookingTime.getTime(),
							checkingBookingConfirmationJob, bookingType,
							"DelayedBookingConfirmationChecking",
							"DelayedCheckingBookingConfirmationJobGroup", tm);
				} else {
					bookingType = 0;
					log.info("scheduleTaxiBookingJob >> Taxi Booking time Invalid.");
				}
			} else if (tm.getBookingType() == Constants.MANUAL_BOOKING_TYPE) {

				bookingType = validateServerAdjustedAdminTime(bookingTime.getTimeInMillis());

				if (bookingType == 2) {
					JobDetail job = JobBuilder
							.newJob(TaxiBookingJob.class)
							.withIdentity(
									"ManualBookingJob_"
											+ bookingTime.getTimeInMillis(),
									"ManualBookingGroup").build();
					job.getJobDataMap().put("tm", tm);
					JobDetail manualBookingActivationJob = JobBuilder
							.newJob(ManualBookingActivationJob.class)
							.withIdentity(
									"ManualBookingActivationJob_"
											+ bookingTime.getTimeInMillis(),
									"ManualBookingGroup").build();
					manualBookingActivationJob.getJobDataMap().put("tm", tm);

					Calendar deleyedBookingTime = Calendar.getInstance();
					deleyedBookingTime
							.setTimeInMillis(((bookingTime.getTimeInMillis()) - (1000 * 60 * (Constants.MANUAL_BOOKING_ACTIVATION_TIME))));

					triggerBooking(deleyedBookingTime.getTime(),
							manualBookingActivationJob, bookingType,
							"ManualBookingActivationJob",
							"ManualBookingActivationJobGroup", tm);
				}

			}
			return bookingType;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bookingType;
	}

	public static void triggerBooking(Date bookingTime, JobDetail job,
			int bookingType, String bookingName, String bookingGroupName,
			TravelMaster tm) {

		try {
			log.info("triggerBooking >> Booking Type= " + bookingName
					+ ", scheduled at " + bookingTime.getHours() + ":"
					+ bookingTime.getMinutes());
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(bookingName + "_" + bookingTime.getTime(),
							bookingGroupName + "_" + bookingType)
					.withSchedule(
							SimpleScheduleBuilder
									.repeatMinutelyForTotalCount(1))
					.startAt(bookingTime).build();

			scheduleJob(job, trigger);

			// scheduler.shutdown(true);

			tm.setScheduler(scheduler);
			tm.setTriggerKey(trigger.getKey());

		} catch (Exception se) {
			se.printStackTrace();
		}

	}

	public static void scheduleJob(JobDetail job, Trigger trigger) {

		try {
			scheduler.start();
			scheduler.scheduleJob(job, trigger);

			// scheduler.unscheduleJob(TriggerKey)

			// log.info("Job is scheduled.");
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static int validateTime(long bookingTime) {

		int scheduleType = 0;

		Calendar rightNow = Calendar.getInstance(); // MyUtil.getCalanderFromDateStr(MyUtil.getCurrentDateFormattedString());

		Calendar bookingTimeCal = Calendar.getInstance();
		bookingTimeCal.setTimeInMillis(bookingTime);

		// log.info("Current Time in MilliSec = "+ rightNow.getTimeInMillis());
		// log.info("Booking Time in MilliSec = "+ bookingTime);
		// log.info("CurrentTime = "+ rightNow.getTime().getHours() + ":" +
		// rightNow.getTime().getMinutes());
		// log.info("BookingTime = "+ bookingTimeCal.getTime().getHours() + ":"
		// + bookingTimeCal.getTime().getMinutes());

		// long _20MinBeforeBookingTime = (bookingTimeCal.getTimeInMillis() -
		// (1000*60*20));

		// long _10MinBeforeBookingTime = (bookingTimeCal.getTimeInMillis() -
		// (1000*60*10));

		long timeDiff = (bookingTimeCal.getTimeInMillis())
				- rightNow.getTimeInMillis();

		// log.info("timeDiff = "+ timeDiff);

		if (timeDiff <= 0) {

			scheduleType = -1;

		} else if ((timeDiff <= (1000 * 60 * (Constants.MAX_BOOKING_TIME)))
				&& (timeDiff >= (1000 * 60 * (Constants.MIN_BOOKING_TIME)))) {

			scheduleType = 1;

		}/*
		 * else if((timeDiff <= (1000*60*15)) && (timeDiff >= (1000*60*10))){
		 * 
		 * scheduleType = 2;
		 * 
		 * }
		 */else if ((timeDiff > (1000 * 60 * (Constants.MAX_BOOKING_TIME)))) {

			scheduleType = 3;

		} else {
			scheduleType = 0;
		}

		return scheduleType;

	}
	
	private static int validateServerAdjustedAdminTime(long bookingTime) {

		int scheduleType = 0;

		Calendar rightNow = Calendar.getInstance(); // MyUtil.getCalanderFromDateStr(MyUtil.getCurrentDateFormattedString()); 

		Calendar bookingTimeCal = Calendar.getInstance();
		bookingTimeCal.setTimeInMillis(bookingTime);

		long timeDiff = (bookingTimeCal.getTimeInMillis())
				- rightNow.getTimeInMillis();

		// log.info("timeDiff = "+ timeDiff);

		if (timeDiff <= 0) {

			scheduleType = -1;

		} else if ((timeDiff >= (1000 * 60 * (5)))
				&& (timeDiff <= (1000 * 60 * (Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME)))) {

			scheduleType = 1;

		} else if ((timeDiff > (1000 * 60 * (Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME)))) {

			scheduleType = 2;

		} else {
			scheduleType = 0;
		}

		return scheduleType;

	}

	public static int validateAdminTime(long bookingTime) {

		int scheduleType = 0;

		Calendar rightNow = MyUtil.getCalanderFromDateStr(MyUtil.getCurrentDateFormattedString()); // Calendar.getInstance(); // 

		Calendar bookingTimeCal = Calendar.getInstance();
		bookingTimeCal.setTimeInMillis(bookingTime);

		long timeDiff = (bookingTimeCal.getTimeInMillis())
				- rightNow.getTimeInMillis();

		// log.info("timeDiff = "+ timeDiff);

		if (timeDiff <= 0) {

			scheduleType = -1;

		} else if ((timeDiff >= (1000 * 60 * (5)))
				&& (timeDiff <= (1000 * 60 * (Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME)))) {

			scheduleType = 1;

		} else if ((timeDiff > (1000 * 60 * (Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME)))) {

			scheduleType = 2;

		} else {
			scheduleType = 0;
		}

		return scheduleType;

	}

	public static boolean unscheduleBooking(TravelMaster tm) {
		boolean bookingCancelFlag = false;

		try {

			// log.info("tm.getScheduler() = " + tm.getScheduler());
			// log.info("tm.getTriggerKey() = " + tm.getTriggerKey());

			
			
			Scheduler scheduler = tm.getScheduler();
			
			if(scheduler != null){
				log.info("unscheduleBooking >> unscheduling bookingId = " + tm.getBookingId());
				bookingCancelFlag = tm.getScheduler().unscheduleJob(
						tm.getTriggerKey());
				CacheBuilder.bookingsDataMap.remove(Long.parseLong(tm
						.getBookingId()));
			}else{
				log.info("unscheduleBooking >> bookingId-"+tm.getBookingId()+" no existing scheduler found.");
			}

			

		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		return bookingCancelFlag;
	}

}