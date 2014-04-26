package com.cabserver.scheduler;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;

import com.cabserver.db.DatabaseManager;
import com.cabserver.parser.GsonJsonParser;
import com.cabserver.pojo.DriverMaster;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.pojo.UserMaster;
import com.cabserver.util.Constants;
import com.cabserver.util.CacheBuilder;
import com.cabserver.util.MyUtil;

@DisallowConcurrentExecution
public class TaxiBookingJob implements Job, org.quartz.InterruptableJob {

	static final Logger log = Logger
			.getLogger(com.cabserver.scheduler.TaxiBookingJob.class.getName());

	private volatile boolean isJobInterrupted = false;
	private JobKey jobKey = null;
	private volatile Thread thisThread;

	public TaxiBookingJob() {
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		thisThread = Thread.currentThread();
		// log.info("Thread name of the current job: " + thisThread.getName());

		jobKey = context.getJobDetail().getKey();
		// log.info("Thread name="+thisThread.getName()+ ", Job " + jobKey +
		// " executing at " + new Date());

		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			String fromKeyStr = data.getString("fromKey");
			TravelMaster tm = (TravelMaster) data.get("tm");
			jobKey = context.getJobDetail().getKey();
			// log.info("execute >> from address = " + fromKeyStr);

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

		// HashMap<String, String> responseMap = new HashMap<String, String>();
		//TreeMap<Double, DriverMaster> distMap = null;
		TreeMap<Double, Long> distMap = null;
		//TreeMap<Long, Double> distDriverIdMap = null;
		boolean bookingStatus = false;

		try {

			distMap = new TreeMap<>();
			//distDriverIdMap = new TreeMap<Long, Double>();

			if (CacheBuilder.driversDataMap != null
					&& CacheBuilder.driversDataMap.size() > 1) {
				Set<Long> keys = CacheBuilder.driversDataMap.keySet();
				/*for (long key : keys) {
					log.info("bookingAlgo >> Before Driver Selection DriverId : "
							+ key
							+ " is: "
							+ (CacheBuilder.driversDataMap.get(key))
									.getDriverStatus());
				}*/

				for (long key : keys) {
					try {

						DriverMaster direverGD = CacheBuilder.driversDataMap
								.get(key);

						if (direverGD.getDriverStatus().equalsIgnoreCase(
								Constants.DRIVER_STATUS_FREE_STR)
								&& direverGD.getDriverCategory() == 1) {
							String distanceStr = GsonJsonParser
									.getDistanceByAddress(tm.getFrom().trim(),
											direverGD.getCurrAddr());
							double distValue = Double.parseDouble(distanceStr
									.trim());
							direverGD.setDistance(distValue + "");
							//distMap.put(new Double(distValue), direverGD);
							distMap.put(new Double(distValue), key);
							//distDriverIdMap.put(key, new Double(distValue));
							Thread.sleep(100);
						}

					} catch (Exception e) {
						log.info("bookingAlgo >> Exception =" + e.getMessage());
						// break;
					}

				}

				// log.info(distMap);

				// out("Your cab is booked,driver details will be sent to you shortly.");
				Calendar newBookingTime = Calendar.getInstance();
				newBookingTime.setTimeInMillis(tm.getBookingDateTime()
						.getTime());

				Set<Double> distKeys = distMap.keySet();
				for (double key : distKeys) {					

						long existingBookingValue = DatabaseManager
								.getBookingsForaDate(distMap.get(key) + "", newBookingTime);
						
						if (existingBookingValue == 0) {

							if (distMap != null && distMap.size() > 0) {
								log.info("Your cab is booked, Your cab  driverId ="+ distMap.get(key)+" is "
										+ Double.valueOf(new DecimalFormat("#.##").format(
												((key) / (1000))*0.62))
										+ " Miles Away from you."
										+ "His current Location is :"
										+ (CacheBuilder.driversDataMap.get(distMap.get(key)))
												.getCurrAddr());								

								DriverMaster selectedDriver = CacheBuilder.driversDataMap
										.get(distMap.get(key));
								selectedDriver
										.setDriverStatus(Constants.DRIVER_STATUS_BUSY_STR);

								CacheBuilder.driversDataMap
										.put(Long.parseLong(selectedDriver
												.getDriverId()), selectedDriver);

								/*for (long key1 : keys) {
									log.info("After Driver Selection DriverId : "
											+ key1
											+ " is: "
											+ (CacheBuilder.driversDataMap
													.get(key1))
													.getDriverStatus());
								}*/

								tm.setDriverId(distMap.get(key)+"");
								tm.setBookingStatus(Constants.BOOKING_DRIVER_ACCEPT_PENDING_MSG);
								tm.setBookingStatusCode(Constants.BOOKING_SCHEDULED_CODE);
								
								DatabaseManager.assignManualBookingDriverToTravelMaster(tm);
								DatabaseManager
										.updateDriverStatus(selectedDriver);
								DatabaseManager.
										assignManualBookingDriverToDriverMaster(
												tm.getBookingId(), tm.getDriverId());
								
								

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
									
									UserMaster um = DatabaseManager
											.getEmailIdByPhone(tm
													.getTravellerPhone());
									
									if(um != null){
										String travellerMailId = um.getMailId();
										if (travellerMailId != null
												&& travellerMailId.length() > 1) {
											MyUtil.sendBookingNotification(tm,
													travellerMailId);
											
											Thread.sleep(2000);
										}

									}

									
								} catch (Exception e) {
									e.printStackTrace();
								}

								try {
									
									String mobileOperator = tm
											.getMobileOperator();
									
									if(mobileOperator != null && mobileOperator.length() > 1){
										MyUtil.sendBookingNotification(
												tm,
												tm.getTravellerPhone()
														+ MyUtil.getMobileOperatorDomain(mobileOperator));
										
									}

									
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

								bookingStatus = true;
								break;

							} else {
								log.info("Cab is not booked. Nearby Taxi not Found. "
										+ "We will try to book it manually.");

							}

						} else {
							log.info("bookingAlgo >> DriverId =" + distMap.get(key)
									+ " has booking at "
									+ new Date(existingBookingValue));
							log.info("Cab is not booked. All drivers have booked for the requested time. "
									+ "Booking will be re-tried manually.");

						}
					

				}

				return bookingStatus;

			} else {

				log.info("Driver locations not available.");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookingStatus;

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