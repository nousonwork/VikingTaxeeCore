package com.cabserver.handler;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cabserver.db.DatabaseManager;
import com.cabserver.gson.pojo.CustomerNotificationPojo;
import com.cabserver.gson.pojo.DriverNotificationPojo;
import com.cabserver.parser.GsonJsonParser;
import com.cabserver.pojo.DriverMaster;
import com.cabserver.pojo.MailMaster;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.pojo.UserMaster;
import com.cabserver.scheduler.TaxiBookingQuartz;
import com.cabserver.util.CacheBuilder;
import com.cabserver.util.ConfigDetails;
import com.cabserver.util.MyUtil;
import com.google.gson.Gson;

@Path("admin")
public class Admin {

	static final Logger log = Logger
			.getLogger(com.cabserver.handler.Admin.class.getName());

	@POST
	@Path("customers/update-customer-data")
	@Produces(MediaType.TEXT_HTML)
	public Response updateCustomerData(String jsonData) {
		HashMap<String, String> responseMap = new HashMap<String, String>();

		try {

			// log.info("updateCustomerData before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("updateCustomerData >>" + jsonData);

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("updateCustomerData >> data=" + jsonData);
			}

			log.info("updateCustomerData >> data after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				UserMaster um = new UserMaster();

				um.setUserId((String) obj.get("userId"));
				um.setFirstName((String) obj.get("firstName"));
				um.setLastName((String) obj.get("lastName"));
				um.setPhone((String) obj.get("phone"));
				um.setSex((String) obj.get("sex"));
				um.setMailId((String) obj.get("mailId"));
				um.setAddress((String) obj.get("address"));
				um.setPassword((String) obj.get("password"));
				um.setMobileOperator((String) obj.get("mobileOperator"));

				UserMaster um1 = DatabaseManager.updateUserMaster(um);

				if (um1.isDbInsertStatus()) {
					// log.info("Customer Update Successfull. HTTP bookingStatus code is 200.");

					responseMap.put("code", "200");
					responseMap.put("userId", um1.getUserId() + "");
					responseMap.put("msg", "Customer Update Succesful.");
				} else {
					responseMap.put("code", "500");
					responseMap.put("msg", "Customer Update Error.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Customer Update Data Error.");
			}

			log.info("updateCustomerData >> response data ="
					+ jsonCreater(responseMap));

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("Customer Update Error. HTTP update driverStatus code is 500.");

			responseMap.put("code", "500");
			responseMap.put("msg", "Server Error.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@POST
	@Path("drivers/update-driver-data")
	@Produces(MediaType.TEXT_HTML)
	public Response updateDriverData(String jsonData) {
		HashMap<String, String> responseMap = new HashMap<String, String>();

		try {

			// log.info("updateDriverData >> before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("updateDriverData after decoding >>" + jsonData);

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
			}
			log.info("updateDriverData >>after split jsonData =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				DriverMaster dm = new DriverMaster();

				dm.setDriverId((String) obj.get("driverId"));
				dm.setPhoneNumber((String) obj.get("phone"));
				dm.setFirstName((String) obj.get("firstName"));
				dm.setLastName((String) obj.get("lastName"));
				dm.setMobileOperator((String) obj.get("mobileOperator"));

				try {
					dm.setAge(Integer.parseInt((String) obj.get("age")));
				} catch (Exception e) {
					log.info("updateDriverData >> " + e.getMessage());
				}
				dm.setSex((String) obj.get("sex"));
				dm.setMailId((String) obj.get("mailId"));
				dm.setDriverLicense((String) obj.get("licNumber"));
				dm.setAddress((String) obj.get("address"));
				int driverCategory = Integer.parseInt((String) obj
						.get("driverCategory"));
				dm.setDriverCategory(driverCategory);

				DriverMaster dm1 = DatabaseManager.updateDriverMaster(dm);

				if (dm1.isDbInsertStatus()) {

					if (driverCategory == 1) {
						CacheBuilder.driversDataMap.put(
								Long.parseLong((String) obj.get("driverId")),
								dm1);
					}

					responseMap.put("code", "200");
					responseMap.put("driverId", dm1.getDriverId() + "");
					responseMap.put("msg", "Driver Update Succesful.");
				} else {
					responseMap.put("code", "500");
					responseMap.put("msg", "Driver Update Error.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Driver Update Data Error.");
			}

			log.info("updateDriverData >> response data = "
					+ jsonCreater(responseMap));

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("updateDriverData >> Error. HTTP code is 500.");

			responseMap.put("code", "500");
			responseMap.put("msg", "Driver Update Server Error.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@POST
	@Path("bookings/update-booking-data")
	@Produces(MediaType.TEXT_HTML)
	public Response updateBookingData(String jsonData) {
		HashMap<String, String> responseMap = new HashMap<String, String>();

		try {

			// log.info("updateBookingData >> before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("updateBookingData >> jsonData:" + jsonData);

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
			}

			log.info("updateBookingData >> after split jsonData=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				TravelMaster tm = new TravelMaster();

				tm.setUserId((String) obj.get("userId"));
				tm.setTravellerPhone((String) obj.get("phone"));
				tm.setBookingId((String) obj.get("bookingId"));
				tm.setDriverId((String) obj.get("driverId"));
				tm.setTravellerName((String) obj.get("name"));
				tm.setBookingStatus((String) obj.get("bookingStatus"));
				tm.setBookingStatusCode((String) obj.get("bookingStatusCode"));
				tm.setFrom((String) obj.get("from"));
				tm.setTo((String) obj.get("to"));
				tm.setNoOfPassengers(Integer.parseInt((String) obj
						.get("noOfPassengers")));
				tm.setMobileOperator((String) obj.get("mobileOperator"));
				tm.setAirline((String) obj.get("airline"));
				tm.setFlightNumber((String) obj.get("flightNumber"));

				String date = (String) obj.get("date");
				String month = (String) obj.get("month");
				String year = (String) obj.get("year");
				String hour = (String) obj.get("hour");
				String min = (String) obj.get("min");
				String ampm = (String) obj.get("ampm");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, Integer.parseInt(date));
				cal.set(Calendar.MONTH, (MyUtil.getMonth(month) - 1));
				cal.set(Calendar.YEAR, Integer.parseInt(year));
				if (ampm.equalsIgnoreCase("PM")) {
					if (hour.equalsIgnoreCase("12")) {
						cal.set(Calendar.HOUR_OF_DAY, 12);
					} else {
						cal.set(Calendar.HOUR_OF_DAY,
								Integer.parseInt(hour) + 12);
					}

				} else {
					if (hour.equalsIgnoreCase("12")) {
						cal.set(Calendar.HOUR_OF_DAY, 00);
					} else {
						cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
					}

				}
				cal.set(Calendar.MINUTE, Integer.parseInt(min));

				tm.setDateTime(cal.getTime());
				
				Calendar oldTime = Calendar.getInstance();
				oldTime.setTimeInMillis(CacheBuilder.bookingsDataMap
						.get(Long.parseLong(tm.getBookingId()))
						.getBookingDateTime().getTime());

				// log.info("updateBookingData >> CacheBuilder.bookingsDataMap.size= "+
				// CacheBuilder.bookingsDataMap.size());
				// log.info("updateBookingData >> CacheBuilder.bookingsDataMap.size= "+
				// CacheBuilder.bookingsDataMap.size());

				// Set<Long> bookingKeys =
				// CacheBuilder.bookingsDataMap.keySet();

				/*
				 * for(long key : bookingKeys){ log.info(
				 * "updateBookingData >> CacheBuilder.bookingsDataMap key = "+
				 * key); log.info(
				 * "updateBookingData >> CacheBuilder.bookingsDataMap tm  = "+
				 * CacheBuilder.bookingsDataMap.get(key)); }
				 */

				boolean assignDriverStatus;

				if (tm.getDriverId() != null && tm.getDriverId().length() > 0
						&& !tm.getDriverId().equalsIgnoreCase("0")
						&& !tm.getDriverId().equalsIgnoreCase("null")) {

					tm.setBookingType(Integer.parseInt(ConfigDetails.constants.get("MANUAL_BOOKING_TYPE")));

					assignDriverStatus = assignDriver(tm);

					if (assignDriverStatus) {

						TravelMaster tmTmpWithOldTime = CacheBuilder.bookingsDataMap
								.get(Long.parseLong(tm.getBookingId()));
						if (tmTmpWithOldTime != null) {
							boolean dbUpdateStatus = DatabaseManager
									.deleteBookingIdFromDriverMaster(tmTmpWithOldTime
											.getDriverId());
						} else {
							TravelMaster tmFrmDB = DatabaseManager
									.searchBookingDetailsByBookingId(tm
											.getBookingId());
							CacheBuilder.bookingsDataMap.put(
									Long.parseLong(tm.getBookingId()), tmFrmDB);

							boolean dbUpdateStatus = DatabaseManager
									.deleteBookingIdFromDriverMaster(tmFrmDB
											.getDriverId());
						}

						tm.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_CONFORMED_CODE"));
						tm.setBookingStatus(ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"));
						DatabaseManager.updateBookingStatus(tm);

						TravelMaster tm1 = DatabaseManager
								.updateBookingData(tm);

						if (tm1.isDbStatus()) {
							responseMap.put("code", "200");
							responseMap.put("msg", "Booking Update Succesful.");

							CacheBuilder.bookingsDataMap.put(
									Long.parseLong(tm.getBookingId()), tm1);

							DriverMaster dm = CacheBuilder.driversDataMap
									.get(Long.parseLong(tm.getDriverId()));

							if (dm != null) {
								dm.setBookingId(tm.getBookingId());
								dm.setDriverStatus(ConfigDetails.constants.get("DRIVER_STATUS_BUSY_STR"));

								CacheBuilder.driversDataMap.put(
										Long.parseLong(tm.getDriverId()), dm);
							}

						} else {
							responseMap.put("code", "500");
							responseMap.put("msg", "Booking Update Error.");
						}

					} else {
						log.info("updateBookingData >> assignDriverStatus = "
								+ assignDriverStatus);
						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap.put("msg",
								"Driver is already booked for this time.");
						responseMap.put(
								"bookingTime",
								oldTime.get(Calendar.YEAR) + "-"
										+ (oldTime.get(Calendar.MONTH) + 1)
										+ "-" + oldTime.get(Calendar.DATE)
										+ " "
										+ oldTime.get(Calendar.HOUR_OF_DAY)
										+ ":"
										+ oldTime.get(Calendar.MINUTE)
										+ ":"
										+ oldTime.get(Calendar.SECOND)
										+ ".0");
					}

				} else {

					TravelMaster tmTmpWithOldTime = CacheBuilder.bookingsDataMap
							.get(Long.parseLong(tm.getBookingId()));
					if (tmTmpWithOldTime == null) {
						TravelMaster tmFrmDB = DatabaseManager
								.searchBookingDetailsByBookingId(tm
										.getBookingId());
						CacheBuilder.bookingsDataMap.put(
								Long.parseLong(tm.getBookingId()), tmFrmDB);
					}

					Calendar newTime = Calendar.getInstance();
					newTime.setTimeInMillis(tm.getBookingDateTime().getTime());

				

					if (newTime.get(Calendar.YEAR) != oldTime
							.get(Calendar.YEAR)
							|| newTime.get(Calendar.MONTH) != oldTime
									.get(Calendar.MONTH)
							|| newTime.get(Calendar.DATE) != oldTime
									.get(Calendar.DATE)
							|| newTime.get(Calendar.HOUR_OF_DAY) != oldTime
									.get(Calendar.HOUR_OF_DAY)
							|| newTime.get(Calendar.MINUTE) != oldTime
									.get(Calendar.MINUTE)) {

						log.info("updateBookingData >> No driverId and booking time changed.");
						tm.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_SCHEDULED_CODE"));
						tm.setBookingStatus(ConfigDetails.constants.get("BOOKING_SCHEDULED_MSG"));
						tm.setBookingType(Integer.parseInt(ConfigDetails.constants.get("SCHEDULED_BOOKING_TYPE")));

						int bookingStatus = TaxiBookingQuartz
								.scheduleTaxiBookingJob(tm);

						if (bookingStatus > 0) {

							DatabaseManager.updateBookingStatus(tm);
							TravelMaster tm1 = DatabaseManager
									.updateBookingData(tm);

							if (tm1.isDbStatus()) {
								responseMap.put("code", "200");
								responseMap.put("msg",
										"Booking Update Succesful.");

								CacheBuilder.bookingsDataMap.put(
										Long.parseLong(tm.getBookingId()), tm1);
							} else {
								responseMap.put("code", "500");
								responseMap.put("msg", "Booking Update Error.");
							}

						} else {

							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
							responseMap.put("msg",
									ConfigDetails.constants.get("MIN_BOOKING_TIME_ERROR_MSG"));
							responseMap.put(
									"bookingTime",
									oldTime.get(Calendar.YEAR) + "-"
											+ (oldTime.get(Calendar.MONTH) + 1)
											+ "-" + oldTime.get(Calendar.DATE)
											+ " "
											+ oldTime.get(Calendar.HOUR_OF_DAY)
											+ ":"
											+ oldTime.get(Calendar.MINUTE)
											+ ":"
											+ oldTime.get(Calendar.SECOND)
											+ ".0");

						}

					} else {

						log.info("updateBookingData >> No driverId and booking time not changed.");
						TravelMaster tm1 = DatabaseManager
								.updateBookingData(tm);

						if (tm1.isDbStatus()) {
							responseMap.put("code", "200");
							responseMap.put("msg", "Booking Update Succesful.");

							CacheBuilder.bookingsDataMap.put(
									Long.parseLong(tm.getBookingId()), tm1);
						} else {
							responseMap.put("code", "500");
							responseMap.put("msg", "Booking Update Error.");
						}

					}

				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Booking Update Data Error.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("updateBookingData >> Error. HTTP code is 500.");

			responseMap.put("code", "500");
			responseMap.put("msg", "Booking Update Server Error.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@POST
	@Path("bookings/list")
	@Produces(MediaType.TEXT_HTML)
	public Response getBookingsList(String jsonData) {
		// String data = "";
		// HashMap<String, String> responseMap = new HashMap<String, String>();
		JSONArray arryTM = new JSONArray();
		try {

			// log.info("getBookingsList >> before decoding =" + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("getBookingsList >> after decoding =" + jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("getBookingsList >> = sign found");
			}

			log.info("getBookingsList >> after split =" + jsonData);

			TravelMaster tm = new TravelMaster();

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				String fromDate = (String) obj.get("fromDate");
				// log.info("fromDate =" + fromDate);
				String fromMonth = (String) obj.get("fromMonth");
				// log.info("fromMonth =" + fromMonth);
				String fromYear = (String) obj.get("fromYear");
				// log.info("fromYear =" + fromYear);

				String toDate = (String) obj.get("toDate");
				// log.info("toDate =" + toDate);
				String toMonth = (String) obj.get("toMonth");
				// log.info("toMonth =" + toMonth);
				String toYear = (String) obj.get("toYear");
				// log.info("toYear =" + toYear);
				String bookingId = (String) obj.get("bookingId");
				// log.info("bookingId =" + bookingId);

				ArrayList<TravelMaster> trvlMstrArryLst = DatabaseManager
						.getBookingDetailsByDate(fromYear + "-" + fromMonth
								+ "-" + fromDate, toYear + "-" + toMonth + "-"
								+ toDate, bookingId);

				for (TravelMaster tmpTm : trvlMstrArryLst) {
					JSONObject obj1 = new JSONObject();

					obj1.put("code", "200");
					obj1.put("msg", "Bookings list fetched.");
					obj1.put("bookingId", tmpTm.getBookingId());
					obj1.put("driverId", tmpTm.getDriverId());
					obj1.put("userId", tmpTm.getUserId());
					obj1.put("name", tmpTm.getTravellerName());
					obj1.put("phone", tmpTm.getTravellerPhone());
					obj1.put("datetime", tmpTm.getBookingDateTime().toString());
					obj1.put("from", tmpTm.getFrom());
					obj1.put("to", tmpTm.getTo());
					obj1.put("bookingStatus", tmpTm.getBookingStatus());
					obj1.put("bookingStatusCode", tmpTm.getBookingStatusCode());
					obj1.put("isBefore", tmpTm.isBefore());

					obj1.put("noOfPassengers", tmpTm.getNoOfPassengers());
					obj1.put("mobileOperator", tmpTm.getMobileOperator());
					obj1.put("airline", tmpTm.getAirline());
					obj1.put("flightNumber", tmpTm.getFlightNumber());

					arryTM.add(obj1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			log.info("getBookingsList >> Bookings Error. HTTP booking history error code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");
			
			

			JSONObject obj1 = new JSONObject();
			obj1.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			obj1.put("msg", "Bookings list not found.");
			arryTM.add(obj1);

			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@POST
	@Path("drivers/list")
	@Produces(MediaType.TEXT_HTML)
	public Response getDriversList(String jsonData) {
		// String data = "";
		// HashMap<String, String> responseMap = new HashMap<String, String>();
		JSONArray arryTM = new JSONArray();
		try {

			// log.info("getDriversList >> before decoding =" + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("getDriversList >> after decoding =" + jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("getDriversList >> = sign found");
			}

			log.info("getDriversList >> after split =" + jsonData);

			// TravelMaster tm = new TravelMaster();

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				String phone = (String) obj.get("phone");
				// log.info("phone =" + phone);
				String name = (String) obj.get("name");
				// log.info("name =" + name);
				String licNumber = (String) obj.get("licNumber");
				// log.info("licNumber =" + licNumber);

				ArrayList<DriverMaster> driverMasterArryList = DatabaseManager
						.searchDriversFromDriverMaster(phone, name, licNumber);

				for (DriverMaster tmpDm : driverMasterArryList) {
					JSONObject obj1 = new JSONObject();

					obj1.put("code", "200");
					obj1.put("msg", "Drivers list fetched.");
					obj1.put("driverId", tmpDm.getDriverId());
					obj1.put("firstName", tmpDm.getFirstName());
					obj1.put("lastName", tmpDm.getLastName());
					obj1.put("phone", tmpDm.getPhoneNumber());
					obj1.put("age", tmpDm.getAge() + "");
					obj1.put("sex", tmpDm.getSex());
					obj1.put("mailId", tmpDm.getMailId());
					obj1.put("licNumber", tmpDm.getDriverLicense());
					obj1.put("address", tmpDm.getAddress());
					obj1.put("currAddress", tmpDm.getCurrAddr());
					obj1.put("driverStatus", tmpDm.getDriverStatus());
					obj1.put("driverCategory", tmpDm.getDriverCategory());
					obj1.put("mobileOperator", tmpDm.getMobileOperator());

					arryTM.add(obj1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			log.info("getDriversList >> Drivers search ErrorCcode is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			JSONObject obj1 = new JSONObject();
			obj1.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			obj1.put("msg", "Drivers list not found.");
			arryTM.add(obj1);

			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@GET
	@Path("drivers/list/company")
	@Produces(MediaType.TEXT_HTML)
	public Response getCompanyDriversList() {
		JSONArray arryTM = new JSONArray();
		try {
			TravelMaster tm = new TravelMaster();

			ArrayList<DriverMaster> driverMasterArryList = DatabaseManager
					.getAllCompanyDriverDetails();

			for (DriverMaster tmpDm : driverMasterArryList) {
				JSONObject obj1 = new JSONObject();

				obj1.put("code", "200");
				obj1.put("msg", "Drivers list fetched.");
				obj1.put("driverId", tmpDm.getDriverId());
				obj1.put("firstName", tmpDm.getFirstName());
				obj1.put("lastName", tmpDm.getLastName());
				obj1.put("phone", tmpDm.getPhoneNumber());
				obj1.put("age", tmpDm.getAge() + "");
				obj1.put("sex", tmpDm.getSex());
				obj1.put("licNumber", tmpDm.getDriverLicense());
				obj1.put("address", tmpDm.getAddress());
				obj1.put("currAddress", tmpDm.getCurrAddr());
				obj1.put("driverStatus", tmpDm.getDriverStatus());
				obj1.put("driverCategory", tmpDm.getDriverCategory());

				arryTM.add(obj1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			/*
			 * log.info("getCompanyDriversList >> Drivers search ErrorCcode is "
			 * + Constants.BOOKING_FAILED_CODE + ".");
			 */

			JSONObject obj1 = new JSONObject();
			obj1.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			obj1.put("msg", "Drivers list not found.");
			arryTM.add(obj1);

			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@GET
	@Path("drivers/list/non-company")
	@Produces(MediaType.TEXT_HTML)
	public Response getNonCompanyDriversList() {
		JSONArray arryTM = new JSONArray();
		try {

			TravelMaster tm = new TravelMaster();

			ArrayList<DriverMaster> driverMasterArryList = DatabaseManager
					.getAllNonCompanyDriverDetails();

			for (DriverMaster tmpDm : driverMasterArryList) {
				JSONObject obj1 = new JSONObject();

				obj1.put("code", "200");
				obj1.put("msg", "Drivers list fetched.");
				obj1.put("driverId", tmpDm.getDriverId());
				obj1.put("firstName", tmpDm.getFirstName());
				obj1.put("lastName", tmpDm.getLastName());
				obj1.put("phone", tmpDm.getPhoneNumber());
				obj1.put("age", tmpDm.getAge() + "");
				obj1.put("sex", tmpDm.getSex());
				obj1.put("licNumber", tmpDm.getDriverLicense());
				obj1.put("address", tmpDm.getAddress());
				obj1.put("currAddress", tmpDm.getCurrAddr());
				obj1.put("driverStatus", tmpDm.getDriverStatus());
				obj1.put("driverCategory", tmpDm.getDriverCategory());

				arryTM.add(obj1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			/*
			 * log.info("getNonCompanyDriversList >> Drivers search ErrorCode is "
			 * + Constants.BOOKING_FAILED_CODE + ".");
			 */

			JSONObject obj1 = new JSONObject();
			obj1.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			obj1.put("msg", "Drivers list not found.");
			arryTM.add(obj1);

			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@POST
	@Path("customers/list")
	@Produces(MediaType.TEXT_HTML)
	public Response getCustomersList(String jsonData) {
		// String data = "";
		// HashMap<String, String> responseMap = new HashMap<String, String>();
		JSONArray arryTM = new JSONArray();
		try {

			// log.info("getCustomersList >> before decoding =" + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("getCustomersList >> after decoding =" + jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("getCustomersList >> = sign found");
			}

			log.info("getCustomersList >> after split =" + jsonData);

			TravelMaster tm = new TravelMaster();

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				String phone = (String) obj.get("phone");
				// log.info("phone =" + phone);
				String name = (String) obj.get("name");
				// log.info("name =" + name);
				String mailId = (String) obj.get("mailId");
				// log.info("mailId =" + mailId);

				ArrayList<UserMaster> userMasterArryList = DatabaseManager
						.searchUsersFromUserMaster(phone, name, mailId);

				for (UserMaster tmpUm : userMasterArryList) {
					JSONObject obj1 = new JSONObject();

					obj1.put("code", "200");
					obj1.put("msg", "Customers list fetched.");
					obj1.put("userId", tmpUm.getUserId());
					obj1.put("firstName", tmpUm.getFirstName());
					obj1.put("lastName", tmpUm.getLastName());
					obj1.put("phone", tmpUm.getPhone());
					obj1.put("sex", tmpUm.getSex());
					obj1.put("mailId", tmpUm.getMailId());
					obj1.put("address", tmpUm.getAddress());
					obj1.put("mobileOperator", tmpUm.getMobileOperator());

					arryTM.add(obj1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			log.info("getCustomersList >> Customers search Error. HTTP customers search error code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			JSONObject obj1 = new JSONObject();
			obj1.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			obj1.put("msg", "Customers list not found.");
			arryTM.add(obj1);

			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@GET
	@Path("mails/item")
	@Produces(MediaType.TEXT_HTML)
	public Response getMailSendingDetails(String jsonData) {

		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			if (CacheBuilder.mailSendingDataMap != null
					&& CacheBuilder.mailSendingDataMap.size() > 0) {

				Set<Long> keys = CacheBuilder.mailSendingDataMap.keySet();

				Iterator<Long> itr = keys.iterator();

				if (itr.hasNext()) {

					long key = itr.next();

					TravelMaster tmpUm = CacheBuilder.mailSendingDataMap
							.get(key);
					CacheBuilder.mailSendingDataMap.remove(key);

					responseMap.put("code", "200");
					responseMap.put("msg", "Mail sending details fetched.");

					if (tmpUm.getMailType() == Integer.parseInt(ConfigDetails.constants.get("MAIL_TYPE_BOOKING"))) {
						responseMap.put("distValue",
								tmpUm.getTotalDistanceTravelled());
						responseMap.put("fare", tmpUm.getFare());
						responseMap.put("fromMailId", tmpUm.getFromMailId());
						responseMap.put("toMailId", tmpUm.getToMailId());
						log.info("getMailSendingDetails >> tmpUm.getToMailId() ="
								+ tmpUm.getToMailId());
						responseMap.put("subject", tmpUm.getSubject());
						responseMap.put("from", tmpUm.getFrom());
						responseMap.put("to", tmpUm.getTo());
						responseMap.put("travellerName",
								tmpUm.getTravellerName());
						responseMap.put("bookingDateTime",
								tmpUm.getBookingDateTime() + "");
						responseMap.put("mailType", tmpUm.getMailType() + "");
					} else if (tmpUm.getMailType() == Integer.parseInt(ConfigDetails.constants.get("MAIL_TYPE_FORGOT_PASSWORD"))) {

						responseMap.put("fromMailId", tmpUm.getFromMailId());
						responseMap.put("toMailId", tmpUm.getToMailId());
						responseMap.put("subject", tmpUm.getSubject());
						responseMap.put("mailText", tmpUm.getMailText());
						responseMap.put("mailType", tmpUm.getMailType() + "");

					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			/*
			 * log.info(
			 * "getMailSendingDetails >> Mail details fetch Error. HTTP error code is "
			 * + Constants.BOOKING_FAILED_CODE + ".");
			 */
			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Mail details list not found.");

			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@GET
	@Path("mails/drivers/items")
	@Produces(MediaType.TEXT_HTML)
	public Response getDriversNotificationItems(String jsonData) {

		HashMap<String, String> responseMap = new HashMap<String, String>();
		String responseData = "{";
		try {

			if (CacheBuilder.driverNotificationDataMap != null
					&& CacheBuilder.driverNotificationDataMap.size() > 0) {

				Set<Long> keys = CacheBuilder.driverNotificationDataMap
						.keySet();

				Iterator<Long> itr = keys.iterator();

				if (itr.hasNext()) {

					long key = itr.next();

					ArrayList<DriverMaster> tmpDmArryList = CacheBuilder.driverNotificationDataMap
							.get(key);
					CacheBuilder.driverNotificationDataMap.remove(key);

					// responseMap.put("code", "200");
					// responseMap.put("msg", "Mail sending details fetched.");

					MailMaster mm = CacheBuilder.mailTextDataMap.get(key);
					CacheBuilder.mailTextDataMap.remove(key);

					if (mm != null && tmpDmArryList.size() > 0) {

						responseData += "\"subject\":" + "\"" + mm.getSubject()
								+ "\"," + "\"body\":" + "\"" + mm.getBody()
								+ "\",\"drivers\":[";

						for (int i = 0; i < tmpDmArryList.size(); i++) {

							DriverMaster tmpDm = tmpDmArryList.get(i);

							responseData += "\"" + tmpDm.getMailId() != null ? tmpDm
									.getMailId() : ""
									+ "\","
									+ "\""
									+ tmpDm.getPhoneNumber()
									+ MyUtil.getMobileOperatorDomain(tmpDm
											.getMobileOperator()) + "\"";
							if (i != tmpDmArryList.size() - 1) {

								responseData += ",";
							}

						}

						responseData += "]}";
					}

					responseMap.put("code", "200");
					responseMap.put("msg", "Notification sent to Drivers.");

				}

			} else {
				/*
				 * log.info(
				 * "getDriversNotificationItems >> No driver notifications.");
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			/*
			 * log.info(
			 * "getMailSendingDetails >> Mail details fetch Error. HTTP error code is "
			 * + Constants.BOOKING_FAILED_CODE + ".");
			 */
			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Drivers notification details not found.");

			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(responseData).build();
		}

	}

	@GET
	@Path("mails/customers/items")
	@Produces(MediaType.TEXT_HTML)
	public Response getCustomersNotificationItems(String jsonData) {

		HashMap<String, String> responseMap = new HashMap<String, String>();
		String responseData = "{";
		try {

			if (CacheBuilder.customerNotificationDataMap != null
					&& CacheBuilder.customerNotificationDataMap.size() > 0) {

				Set<Long> keys = CacheBuilder.customerNotificationDataMap
						.keySet();

				Iterator<Long> itr = keys.iterator();

				if (itr.hasNext()) {

					long key = itr.next();

					ArrayList<UserMaster> tmpUmArryList = CacheBuilder.customerNotificationDataMap
							.get(key);
					CacheBuilder.customerNotificationDataMap.remove(key);

					// responseMap.put("code", "200");
					// responseMap.put("msg", "Mail sending details fetched.");

					MailMaster mm = CacheBuilder.mailTextDataMap.get(key);
					CacheBuilder.mailTextDataMap.remove(key);

					if (mm != null && tmpUmArryList.size() > 0) {

						responseData += "\"subject\":" + "\"" + mm.getSubject()
								+ "\"," + "\"body\":" + "\"" + mm.getBody()
								+ "\",\"users\":[";

						for (int i = 0; i < tmpUmArryList.size(); i++) {

							UserMaster tmpUm = tmpUmArryList.get(i);

							responseData += "\"" + tmpUm.getMailId() != null ? tmpUm
									.getMailId() : ""
									+ "\","
									+ "\""
									+ tmpUm.getPhone()
									+ MyUtil.getMobileOperatorDomain(tmpUm
											.getMobileOperator()) + "\"";
							if (i != tmpUmArryList.size() - 1) {

								responseData += ",";
							}

						}

						responseData += "]}";
					}

					responseMap.put("code", "200");
					responseMap.put("msg", "Notification sent to Drivers.");

				}

			} else {
				/*
				 * log.info(
				 * "getCustomersNotificationItems >> No customers notifications."
				 * );
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			/*
			 * log.info(
			 * "getMailSendingDetails >> Mail details fetch Error. HTTP error code is "
			 * + Constants.BOOKING_FAILED_CODE + ".");
			 */
			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Customers notification details not found.");

			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(responseData).build();
		}

	}

	@POST
	@Path("bookings/delete-driver-cancel-booking")
	@Produces(MediaType.TEXT_HTML)
	public Response deleteDriverCancelBooking(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			// log.info("deleteDriverCancelBooking before decoding = " +
			// jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("deleteDriverCancelBooking >>after decoding =" +
			// jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("deleteDriverCancelBooking >> = sign found");
			}

			log.info("deleteDriverCancelBooking >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String bookingStatus = (String) obj.get("bookingStatus");
				String bookingStatusCode = (String) obj
						.get("bookingStatusCode");
				String userId = (String) obj.get("userId");
				String bookingId = (String) obj.get("bookingId");
				String driverId = (String) obj.get("driverId");

				// log.info("deleteDriverCancelBooking >> bookingId =" +
				// bookingId);

				TravelMaster tm = new TravelMaster();
				tm.setBookingStatus(bookingStatus);
				tm.setBookingStatusCode(bookingStatusCode);
				tm.setUserId(userId);
				tm.setBookingId(bookingId);
				tm.setDriverId(driverId);

				boolean dbUpdateStatus = DatabaseManager
						.removeDriverFromBookingInTravelMaster(tm);

				if (dbUpdateStatus) {

					responseMap.put("code", "200");
					responseMap.put("msg", "Driver Booking Status updated.");

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg",
							"Manual Booking details not updated.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Manual Booking data not available.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("deleteDriverCancelBooking >>"
					+ " Bookings Error. HTTP bookingStatus code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Server Error.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	/*
	 * @POST
	 * 
	 * @Path("bookings/assign-driver")
	 * 
	 * @Produces(MediaType.TEXT_HTML) public Response assignDriver(String
	 * jsonData) {
	 */
	public boolean assignDriver(TravelMaster tm) {

		boolean assignDriverStatus = false;
		try {

			Calendar cal = Calendar.getInstance();
			cal.setTime(tm.getBookingDateTime());

			long existingBookingValue = DatabaseManager.getBookingsForaDate(
					tm.getDriverId(), cal);

			log.info("assignDriver >> existingBookingValue  ="
					+ existingBookingValue);

			if (existingBookingValue == 0) {

				tm.setBookingStatus(ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"));
				tm.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_CONFORMED_CODE"));

				int bookingType = TaxiBookingQuartz.scheduleTaxiBookingJob(tm);

				log.info("assignDriver >> bookingType = " + bookingType);

				if (bookingType == 1) {
					// Immediate manual booking insert and driver assign

					boolean dbUpdateStatus = DatabaseManager
							.assignManualBookingDriverToTravelMaster(tm);
					boolean dbUpdateStatusDM = DatabaseManager
							.assignManualBookingDriverToDriverMaster(
									tm.getBookingId(), tm.getDriverId());

					if (dbUpdateStatus && dbUpdateStatusDM) {

						assignDriverStatus = true;
						
						new NotificationThread(tm).start();
						

					} else {
						assignDriverStatus = false;
						log.info("assignDriver >> error in updating database =");

					}

				} else if (bookingType == 2) {

					boolean dbUpdateStatus = DatabaseManager
							.assignManualBookingDriverToTravelMaster(tm);

					/*
					 * boolean dbUpdateStatusTM = DatabaseManager
					 * .updateBookingStatus(tm);
					 */

					if (dbUpdateStatus) {
						assignDriverStatus = true;
						
						new NotificationThread(tm).start();
					}
				}

			} else {
				assignDriverStatus = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return assignDriverStatus;
	}

	@GET
	@Path("bookings/pending")
	@Produces(MediaType.TEXT_HTML)
	public Response getPendingBookingsList(String jsonData) {
		// String data = "";
		JSONArray arryTM = new JSONArray();
		try {

			ArrayList<TravelMaster> trvlMstrArryLst = DatabaseManager
					.getAllBookingDetailsByStatusCode();

			for (TravelMaster tmpTm : trvlMstrArryLst) {
				JSONObject obj1 = new JSONObject();

				obj1.put("code", "200");
				obj1.put("msg", "Bookings list fetched.");
				obj1.put("bookingId", tmpTm.getBookingId());
				obj1.put("userId", tmpTm.getUserId());
				obj1.put("name", tmpTm.getTravellerName());
				obj1.put("phone", tmpTm.getTravellerPhone());
				obj1.put("datetime", tmpTm.getBookingDateTime().toString());
				obj1.put("from", tmpTm.getFrom());
				obj1.put("to", tmpTm.getTo());
				obj1.put("bookingStatus", tmpTm.getBookingStatus());
				obj1.put("bookingStatusCode", tmpTm.getBookingStatusCode());
				obj1.put("isBefore", tmpTm.isBefore());

				obj1.put("noOfPassengers", tmpTm.getNoOfPassengers());
				obj1.put("mobileOperator", tmpTm.getMobileOperator());
				obj1.put("airline", tmpTm.getAirline());
				obj1.put("flightNumber", tmpTm.getFlightNumber());

				arryTM.add(obj1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			log.info("getPendingBookingsList >> No pending bookings found. HTTP bookingStatus code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			JSONObject obj1 = new JSONObject();
			obj1.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			obj1.put("msg", "No pending bookings.");
			arryTM.add(obj1);

			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@POST
	@Path("forgot-password")
	@Produces(MediaType.TEXT_HTML)
	public Response forgotPassword(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			// log.info("forgotPassword before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("forgotPassword >>" + jsonData);

			// jsonData = jsonData.split("=")[1];
			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("forgotPassword >> data=" + jsonData);
			}

			log.info("forgotPassword >> json data=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				// String password = (String) obj.get("password");

				// log.info("phone =" + phone);
				// log.info("password =" + password);

				if (phone != null) {

					UserMaster um = DatabaseManager.getEmailIdByPhone(phone);

					if (um != null) {

						log.info("forgotPassword >> Email fetched by phone number. HTTP code is 200.");

						responseMap.put("code", "200");
						responseMap
								.put("msg",
										"Password is sent to your registered E-Mail ID.");
						responseMap.put("phone", phone);
						// responseMap.put("password", um.getPassword());
						// responseMap.put("mailid", um.getMailId());

						try {

							if (Boolean.parseBoolean(ConfigDetails.constants.get("LOCAL_MAIL_SEND"))) {

								Message message = new MimeMessage(
										CacheBuilder.session);
								message.setFrom(new InternetAddress(
										"VikingTaxee@gmail.com"));
								message.setRecipients(Message.RecipientType.TO,
										InternetAddress.parse(um.getMailId()));
								message.setSubject("VikingTaxee Account Password Information");
								message.setText("Dear Customer You password is : "
										+ "\n\n " + um.getPassword());

								Transport.send(message);

								log.info("forgotPassword >> Password is sent to your registered E-Mail ID.");

							} else {

								TravelMaster tm = new TravelMaster();

								tm.setFromMailId("VikingTaxee@gmail.com");
								tm.setToMailId(um.getMailId());
								tm.setSubject("VikingTaxee Account Password Information");
								tm.setMailText(um.getPassword());
								tm.setMailType(Integer.parseInt(ConfigDetails.constants.get("MAIL_TYPE_FORGOT_PASSWORD")));

								CacheBuilder.mailSendingDataMap
										.put((long) new Random()
												.nextInt(100000), tm);
							}

						} catch (Exception e) {
							throw new RuntimeException(e);
						}

					} else {
						log.info("forgotPassword >> Password reset not done. Please call customer Care.");

						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap
								.put("msg",
										"Forgot password process can not be completed. Please call customer Care.");

					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("Login Error. HTTP bookingStatus code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Server Error.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@POST
	@Path("notify/drivers")
	@Produces(MediaType.TEXT_HTML)
	public Response notifyDrivers(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			// log.info("notifyDrivers before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("notifyDrivers >>" + jsonData);

			// jsonData = jsonData.split("=")[1];
			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("notifyDrivers >> data=" + jsonData);
			}

			log.info("notifyDrivers >> json data=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				// JSONParser parser = new JSONParser();
				// JSONArray driversArry = (JSONArray) parser.parse(jsonData);

				Gson gson = new Gson();
				DriverNotificationPojo result = gson.fromJson(jsonData,
						DriverNotificationPojo.class);

				ArrayList<DriverMaster> driversList = new ArrayList<>();

				for (int i = 0; i < result.drivers.length; i++) {

					String driverId = result.drivers[i];

					if (driverId != null) {

						DriverMaster dm = DatabaseManager
								.getDriverDetails(driverId);
						driversList.add(dm);
					}

				}
				
				HashMap driverNotificationDataMap = new HashMap<>();
				driverNotificationDataMap.put("subject", result.subject);
				driverNotificationDataMap.put("body", result.body);
				driverNotificationDataMap.put("driverlist", driversList);

				new DriversNotificationThread(driverNotificationDataMap)
						.start();

				responseMap.put("code", "200");
				responseMap.put("msg",
						"Notification Sent to " + driversList.size()
								+ " Drivers.");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("Driver notification Error.");

			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Notification Error. Please try again.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@POST
	@Path("notify/customers")
	@Produces(MediaType.TEXT_HTML)
	public Response notifyCustomers(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			// log.info("notifyCustomers before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("notifyCustomers >>" + jsonData);

			// jsonData = jsonData.split("=")[1];
			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("notifyCustomers >> data=" + jsonData);
			}

			log.info("notifyCustomers >> json data=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				Gson gson = new Gson();
				CustomerNotificationPojo result = gson.fromJson(jsonData,
						CustomerNotificationPojo.class);

				String userId1 = result.users[0];

				ArrayList<UserMaster> usersList = new ArrayList<>();

				if (userId1.equalsIgnoreCase("ALL")) {

					usersList = DatabaseManager.getAllUsersFromUserMaster();

				} else {

					for (int i = 0; i < result.users.length; i++) {

						String userId = result.users[i];

						if (userId != null) {

							UserMaster dm = DatabaseManager

							.getUserDetailsByUserId(userId);
							usersList.add(dm);
						}
					}
				}

				/*
				 * long key = (long) new Random().nextInt(100000);
				 * 
				 * MailMaster mm = new MailMaster();
				 * mm.setSubject(result.subject); mm.setBody(result.body);
				 * 
				 * CacheBuilder.mailTextDataMap.put(key, mm);
				 * 
				 * CacheBuilder.customerNotificationDataMap.put(key, usersList);
				 */

				HashMap customersNotificationDataMap = new HashMap<>();
				customersNotificationDataMap.put("subject", result.subject);
				customersNotificationDataMap.put("body", result.body);
				customersNotificationDataMap.put("customerlist", usersList);

				new CustomersNotificationThread(customersNotificationDataMap)
						.start();

				responseMap.put("code", "200");
				responseMap.put("msg", "Notification Sent to Customers.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("Customer notification Error.");

			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Notification Error. Please try again.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	@POST
	@Path("bookings/manual")
	@Produces(MediaType.TEXT_HTML)
	public Response addManualBookings(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			/*
			 * log.info("addBookings >> before decoding =" + jsonData);
			 */

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info("addBookings >> after decoding =" + jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("addBookings >> = sign found");
			}

			/*
			 * log.info("addBookings >> after split =" + jsonData);
			 */

			TravelMaster tm = new TravelMaster();

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				// log.info("addBookings >> travellerPhone =" + phone);

				String name = (String) obj.get("name");
				// log.info("addBookings >> travellerName =" + name);

				String from = (String) obj.get("from");
				// log.info("addBookings >> from =" + from);
				String to = (String) obj.get("to");
				// log.info("addBookings >> to =" + to);

				String noOfPassengers = (String) obj.get("noOfPassengers");
				// log.info("noOfPassengers >> to =" + noOfPassengers);
				tm.setNoOfPassengers(Integer.parseInt(noOfPassengers));

				String mobileOperator = (String) obj.get("mobileOperator");
				// log.info("mobileOperator >> to =" + mobileOperator);
				tm.setMobileOperator(mobileOperator);

				String airline = (String) obj.get("airline");
				// log.info("airline >> to =" + airline);
				tm.setAirline(airline);

				String flightNumber = (String) obj.get("flightNumber");
				// log.info("flightNumber >> to =" + flightNumber);
				tm.setFlightNumber(flightNumber);

				String driverId = (String) obj.get("driverId");
				// log.info("driverId >> to =" + driverId);
				tm.setDriverId(driverId);

				tm.setBookingType(Integer.parseInt(ConfigDetails.constants.get("MANUAL_BOOKING_TYPE")));
				tm.setActivationStatus(Integer.parseInt(ConfigDetails.constants.get("MANUAL_BOOKING_DEACTIVE_STATUS")));

				String date = (String) obj.get("date");
				String month = (String) obj.get("month");
				String year = (String) obj.get("year");

				String hour = (String) obj.get("hour");
				String min = (String) obj.get("min");
				String ampm = (String) obj.get("ampm");
				Calendar cal = Calendar.getInstance();

				if (phone != null && phone.length() >= 10) {
					tm.setTravellerPhone(phone);
					long id_from_db = DatabaseManager
							.searchUserMasterByPhone(phone);
					if (id_from_db == 0) {

						UserMaster um = new UserMaster();

						um.setAdminId(1 + "");
						um.setCompanyId(101 + "");

						if (name.contains(" ")) {

							String tmpNameArry[] = name.split(" ");
							um.setFirstName(tmpNameArry[0]);
							um.setLastName(tmpNameArry[1]);

						} else {
							um.setFirstName(name);
							um.setLastName(name);
						}

						um.setPhone(phone);
						um.setAge(Integer.parseInt("99"));
						um.setSex("u");
						um.setUid("0000");
						um.setAddress(from);
						um.setPassword("12345");
						um.setRole("user");
						UserMaster userMaster = DatabaseManager
								.insertUserMaster(um);
						if (userMaster.isDbInsertStatus()) {
							tm.setUserId(userMaster.getUserId());

						} else {
							// / send formward error
							responseMap.put("code", "200");
							responseMap.put("msg", "Customer details error.");
						}

					} else {

						tm.setUserId(id_from_db + "");
					}

					if (Double.parseDouble(tm.getUserId()) > 0) {
						tm.setTravellerName(name);
						tm.setFrom(from);
						tm.setTo(to);

						Map fromLatLng;
						Map toLatLng;
						String addressStatus = "";
						try {

							fromLatLng = GsonJsonParser
									.getLatLongtByAddress(from);
							tm.setFromLat((String) fromLatLng.get("lat"));
							tm.setFromLongt((String) fromLatLng.get("longt"));

							toLatLng = GsonJsonParser.getLatLongtByAddress(to);
							tm.setToLat((String) toLatLng.get("lat"));
							tm.setToLongt((String) toLatLng.get("lat"));

						} catch (Exception e) {
							addressStatus = ConfigDetails.constants.get("INCORRECT_ADDRESS_CODE");

							tm.setFromLat("123");
							tm.setFromLongt("123");
							tm.setToLat("123");
							tm.setToLongt("123");

							log.info("addManualBookings >> Exception ="
									+ e.getMessage());
						}

						cal.set(Calendar.DATE, Integer.parseInt(date));
						cal.set(Calendar.MONTH, (MyUtil.getMonth(month) - 1));
						cal.set(Calendar.YEAR, Integer.parseInt(year));
						if (ampm.equalsIgnoreCase("PM")) {
							if (hour.equalsIgnoreCase("12")) {
								cal.set(Calendar.HOUR_OF_DAY, 12);
							} else {
								cal.set(Calendar.HOUR_OF_DAY,
										Integer.parseInt(hour) + 12);
							}

						} else {
							if (hour.equalsIgnoreCase("12")) {
								cal.set(Calendar.HOUR_OF_DAY, 00);
							} else {
								cal.set(Calendar.HOUR_OF_DAY,
										Integer.parseInt(hour));
							}

						}
						cal.set(Calendar.MINUTE, Integer.parseInt(min));

						tm.setDateTime(cal.getTime());

						Calendar bookingTime = Calendar.getInstance();
						bookingTime.setTimeInMillis(tm.getDateTime().getTime());

						log.info("addManualBookings >> Manual Booking Time = "
								+ new Date(tm.getDateTime().getTime()));

						int bookingType = TaxiBookingQuartz
								.validateAdminTime(bookingTime
										.getTimeInMillis());
						log.info("addManualBookings >> booking scheduleType"
								+ "(2=ManualScheduled,1=NonScheduled) = " + bookingType);

						if (bookingType <= 0) {
							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
							responseMap.put("msg",
									ConfigDetails.constants.get("ADMIN_MIN_BOOKING_TIME_ERROR_MSG"));
						} else {

							if (addressStatus
									.equalsIgnoreCase(ConfigDetails.constants.get("INCORRECT_ADDRESS_CODE"))) {
								tm.setBookingStatus(ConfigDetails.constants.get("INCORRECT_ADDRESS_MSG"));
								tm.setBookingStatusCode(addressStatus);

							} else {
								tm.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_CONFORMED_CODE"));
								tm.setBookingStatus(ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"));
							}

							// TBD for 2 Hours checking logic

							long existingBookingValue = DatabaseManager
									.getBookingsForaDate(driverId, cal);

							// TBD booking activation logic

							if (existingBookingValue == 0) {

								TravelMaster tmFrmDB = DatabaseManager
										.insertTravelMaster(tm);
								if (tmFrmDB.isDbStatus()) {

									int bookingStatus = TaxiBookingQuartz
											.scheduleTaxiBookingJob(tmFrmDB);
									
									log.info("addManualBookings >> bookingSchedueStatus = "+ bookingStatus);

									if (bookingStatus == 1) {
										// Immediate manual booking insert and
										// driver assign

										boolean manualBookingActivationStatus = false;

										manualBookingActivationStatus = DatabaseManager
												.updateManualBookingActiveStatus(tmFrmDB
														.getBookingId());
										log.info("addManualBookings >> updating manualBookingActivationStatus "
												+ "in TravelMaster ="
												+ manualBookingActivationStatus);
										manualBookingActivationStatus = DatabaseManager
												.assignManualBookingDriverToDriverMaster(
														tmFrmDB.getBookingId(),
														tmFrmDB.getDriverId());
										log.info("addManualBookings >> updating manualBookingActivationStatus "
												+ "in DriverMaster ="
												+ manualBookingActivationStatus);

										CacheBuilder.bookingsDataMap.put(Long
												.parseLong(tmFrmDB
														.getBookingId()),
												tmFrmDB);

										responseMap.put("code", "200");
										responseMap
												.put("msg",
														ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"));
										responseMap
												.put("travellerPhone", phone);
										responseMap.put(
												"bookingId",
												new Double(tmFrmDB
														.getBookingId())
														.longValue()
														+ "");

										new NotificationThread(tm).start();

										log.info("addManualBookings >> "
												+ "Booking Added. HTTP bookingStatus code is 200. responseMap="
												+ responseMap.toString());

									} else if (bookingStatus == 2) {
										
										new NotificationThread(tm).start();

										responseMap.put("code", "200");
										responseMap
												.put("msg",
														ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"));

									} else if (bookingStatus <= 0) {

										tmFrmDB.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
										tmFrmDB.setBookingStatus(ConfigDetails.constants.get("BOOKING_FAILED_MSG"));
										DatabaseManager
												.updateBookingStatus(tmFrmDB);

										responseMap.put("code",
												ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
										if (bookingStatus < 0) {
											responseMap
													.put("msg",
															ConfigDetails.constants.get("MIN_BOOKING_TIME_ERROR_MSG"));
										} else if (bookingStatus == 0) {
											responseMap
													.put("msg",
															"Booking scheduling error.");
										}

									}

								} else {
									responseMap.put("code",
											ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
									responseMap.put("msg",
											"Booking creation error.");
								}
							} else {
								responseMap.put("code",
										ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
								responseMap
										.put("msg",
												"Driver has a booking at "
														+ new Date(
																existingBookingValue));
							}

						}

					} else {
						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap.put("msg", "Server data booking details.");
					}

				} else {

					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Incorrect booking details.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Incorrect booking data.");
			}

		} catch (Exception e) {
			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Incorrect booking data.");
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("addManualBookings >> Bookings Error. HTTP bookingStatus code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			responseMap.put("msg", "Server Error.");
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		} else {
			return Response.status(200).entity(jsonCreater(responseMap))
					.build();
		}

	}

	String jsonCreater(Map mp) {

		JSONObject obj = new JSONObject();

		// obj.put("code", "200");
		// obj.put("msg", "Login Succesful.");
		obj.putAll(mp);

		// log.info("Formed JSON = "+ obj.toJSONString());

		return obj.toJSONString();
	}
	
		

	class NotificationThread extends Thread {

		TravelMaster tm;

		public NotificationThread(TravelMaster tm1) {
			this.tm = tm1;

		}

		public void run() {

			/********* Mail and SMS sending Logic Start ***/

			/*
			 * DriverMaster dm = DatabaseManager
			 * .getDriverDetails(tm.getDriverId());
			 * 
			 * // Sending Mail and SMS to Driver try {
			 * 
			 * String driverMailId = dm.getMailId(); if (driverMailId != null &&
			 * driverMailId.length() > 1) { MyUtil.sendBookingNotification(tm,
			 * driverMailId); }
			 * 
			 * } catch (Exception e) { e.printStackTrace(); }
			 * 
			 * try { String driverMobileNumber = dm .getPhoneNumber(); String
			 * mobileDomain = MyUtil .getMobileOperatorDomain(dm
			 * .getMobileOperator()); MyUtil.sendBookingNotification(tm,
			 * driverMobileNumber + mobileDomain); } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			// Sending Mail and SMS to Customer
			try {
				
				log.info("tm.getTravellerPhone() = "+ tm
						.getTravellerPhone());

				String travellerMailId = (DatabaseManager.getEmailIdByPhone(tm
						.getTravellerPhone())).getMailId();
				if (travellerMailId != null && travellerMailId.length() > 1) {
					MyUtil.sendBookingNotification(tm, travellerMailId);
				}

				Thread.sleep(2000);

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

				userDetails = DatabaseManager.getUserDetailsByUserId(tm
						.getUserId());
				umAdmin = DatabaseManager.getUserDetailsByUserId(userDetails
						.getAdminId());
				MyUtil.sendBookingNotification(tm, umAdmin.getMailId());

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

		}

	}

	class DriversNotificationThread extends Thread {

		HashMap driversMailMap;

		public DriversNotificationThread(HashMap driverNotificationDataMap) {
			this.driversMailMap = driverNotificationDataMap;
		}

		public void run() {

			sendDriversNotification();

		}

		public void sendDriversNotification() {

			try {

				if (this.driversMailMap != null
						&& this.driversMailMap.size() > 0) {

					String subject = (String) this.driversMailMap
							.get("subject");
					String body = (String) this.driversMailMap.get("body");
					ArrayList<DriverMaster> driversList = (ArrayList) this.driversMailMap
							.get("driverlist");

					for (int i = 0; i < driversList.size(); i++) {
						try {
							Message message = new MimeMessage(
									CacheBuilder.session);
							message.setFrom(new InternetAddress(
									"VikingTaxee@gmail.com"));
							message.setRecipients(Message.RecipientType.TO,
									InternetAddress.parse(driversList.get(i)
											.getMailId()));
							message.setSubject(subject);
							message.setText(body);
							Transport.send(message);

							log.info("sendDriversNotification >> Notification sent to DriverId = "
									+ driversList.get(i).getDriverId()
									+ ", sent-to = "
									+ driversList.get(i).getMailId() + ".");
						} catch (Exception e) {
							// e.printStackTrace();
						}

						Thread.sleep(500);

						try {
							Message message = new MimeMessage(
									CacheBuilder.session);
							message.setFrom(new InternetAddress(
									"VikingTaxee@gmail.com"));

							String driverMobileNumber = driversList.get(i)
									.getPhoneNumber();
							String mobileDomain = MyUtil
									.getMobileOperatorDomain(driversList.get(i)
											.getMobileOperator());
							String receiverId = driverMobileNumber
									+ mobileDomain;

							message.setRecipients(Message.RecipientType.TO,
									InternetAddress.parse(receiverId));
							message.setSubject(subject);
							message.setText(body);
							Transport.send(message);

							log.info("sendDriversNotification >> Notification sent to DriverId = "
									+ driversList.get(i).getDriverId()
									+ ", sent-to = " + receiverId + ".");
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	class CustomersNotificationThread extends Thread {

		HashMap customersMailMap;

		public CustomersNotificationThread(HashMap customersNotificationDataMap) {
			this.customersMailMap = customersNotificationDataMap;
		}

		public void run() {

			sendCustomersNotification();

		}

		public void sendCustomersNotification() {

			try {

				if (this.customersMailMap != null
						&& this.customersMailMap.size() > 0) {

					String subject = (String) this.customersMailMap
							.get("subject");
					String body = (String) this.customersMailMap.get("body");
					ArrayList<UserMaster> customersList = (ArrayList) this.customersMailMap
							.get("customerlist");

					for (int i = 0; i < customersList.size(); i++) {
						try {
							Message message = new MimeMessage(
									CacheBuilder.session);
							message.setFrom(new InternetAddress(
									"VikingTaxee@gmail.com"));
							message.setRecipients(Message.RecipientType.TO,
									InternetAddress.parse(customersList.get(i)
											.getMailId()));
							message.setSubject(subject);
							message.setText(body);
							Transport.send(message);

							log.info("sendCustomersNotification >> Notification sent to UserId = "
									+ customersList.get(i).getUserId()
									+ ", sent-to = "
									+ customersList.get(i).getMailId() + ".");
						} catch (Exception e) {
							// e.printStackTrace();
						}

						Thread.sleep(500);

						try {
							Message message = new MimeMessage(
									CacheBuilder.session);
							message.setFrom(new InternetAddress(
									"VikingTaxee@gmail.com"));

							String driverMobileNumber = customersList.get(i)
									.getPhone();
							String mobileDomain = MyUtil
									.getMobileOperatorDomain(customersList.get(
											i).getMobileOperator());
							String receiverId = driverMobileNumber
									+ mobileDomain;

							message.setRecipients(Message.RecipientType.TO,
									InternetAddress.parse(receiverId));
							message.setSubject(subject);
							message.setText(body);
							Transport.send(message);

							log.info("sendCustomersNotification >> Notification sent to UserId = "
									+ customersList.get(i).getUserId()
									+ ", sent-to = " + receiverId + ".");
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
