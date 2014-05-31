package com.cabserver.handler;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import com.cabserver.pojo.DriverMaster;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.util.CacheBuilder;
import com.cabserver.util.ConfigDetails;
import com.cabserver.util.MyUtil;

@Path("drivers")
public class Driver {
	static final Logger log = Logger
			.getLogger(com.cabserver.handler.Driver.class.getName());

	@POST
	@Path("login")
	@Produces(MediaType.TEXT_HTML)
	public Response login(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			// log.info("Inside Driver >> login before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("Inside Driver >> login >>" + jsonData);

			// jsonData = jsonData.split("=")[1];
			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("Inside Driver >> signup >> data=" + jsonData);
			}

			log.info("login >> data=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				// String password = (String) obj.get("password");

				// log.info("Driver Phone =" + phone);
				// log.info("password =" + password);

				if (phone != null) {

					DriverMaster driverMaster = DatabaseManager
							.validateDriver(phone);

					if (driverMaster != null) {

						// log.info("Inside Driver >> Login Successfull. HTTP bookingStatus code is 200.");

						responseMap.put("code", "200");
						responseMap.put("msg", "Login Succesful.");
						responseMap.put("driverId", driverMaster.getDriverId());
						responseMap.put("firstName",
								driverMaster.getFirstName());
						responseMap.put("phoneNumber",
								driverMaster.getPhoneNumber());
						responseMap.put("driverStatus",
								driverMaster.getDriverStatus());
						responseMap.put("currAddr", driverMaster.getCurrAddr());
						responseMap.put("currLongt",
								driverMaster.getCurrLongt());
						responseMap.put("currLat", driverMaster.getCurrLat());
						responseMap.put("bookingId",
								driverMaster.getBookingId());

						log.info("login >>  Login response = "
								+ responseMap.toString());

					} else {
						log.info("login >> Login Error. HTTP bookingStatus code is "
								+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap.put("msg", "Driver phone doesn't exists.");
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
	@Path("signup")
	@Produces(MediaType.TEXT_HTML)
	public Response signup(String jsonData) {
		HashMap<String, String> responseMap = new HashMap<String, String>();

		try {

			// log.info("signup before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("signup >>" + jsonData);

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("signup >> data=" + jsonData);
			}

			log.info("signup >> json data after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				// log.info("phone =" + phone);

				String mobileOperator = (String) obj.get("mobileOperator");
				// log.info("mobileOperator =" + mobileOperator);

				if (phone != null) {

					DriverMaster driverMaster = DatabaseManager
							.validateDriver(phone);

					if (driverMaster != null) {

						log.info("signup >> Driver already exists. Please Login. HTTP code is 409.");

						responseMap.put("code", "409");
						responseMap.put("driverId", driverMaster.getDriverId());
						responseMap
								.put("msg",
										"Driver already exists.Please enter other phone number.");

					} else {

						DriverMaster dm = new DriverMaster();

						dm.setAdminId(1 + "");
						dm.setCompanyId(101 + "");

						if (((String) obj.get("name")).contains(" ")) {

							String tmpNameArry[] = ((String) obj.get("name"))
									.split(" ");
							dm.setFirstName(tmpNameArry[0]);
							dm.setLastName(tmpNameArry[1]);

						} else {
							dm.setFirstName((String) obj.get("name"));
							dm.setLastName((String) obj.get("name"));
						}

						dm.setPhoneNumber(phone);
						dm.setMobileOperator(mobileOperator);
						try {
							dm.setAge(Integer.parseInt((String) obj.get("age")));
						} catch (Exception e) {
							log.info(e.getMessage());
						}
						dm.setSex((String) obj.get("sex"));
						dm.setMailId((String) obj.get("mailId"));
						dm.setDriverLicense((String) obj.get("licNumber"));
						dm.setAddress((String) obj.get("address"));
						int driverCategory = Integer.parseInt((String) obj
								.get("driverCategory"));
						dm.setDriverCategory(driverCategory);

						DriverMaster dm1 = DatabaseManager
								.insertDriverMaster(dm);

						if (dm1.isDbInsertStatus()) {

							if (driverCategory == 1) {
								CacheBuilder.driversDataMap.put(
										Long.parseLong(dm1.getDriverId()), dm);
							}

							log.info("signup >> Driver SignUp Successfull. HTTP code is 200.");

							responseMap.put("code", "200");
							responseMap.put("driverId", dm1.getDriverId() + "");
							responseMap.put("msg", "Driver SignUp Succesful.");
						} else {
							responseMap.put("code", "500");
							responseMap.put("msg", "Driver SignUp Error.");
						}

					}

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Driver Phone number Error.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Data Error.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("signup >> Driver SignUp Error. HTTP bookingStatus code is 500.");

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
	@Path("delete")
	@Produces(MediaType.TEXT_HTML)
	public Response deleteDriver(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			/*
			 * log.info("Inside Driver >> deleteDriver before decoding = " +
			 * jsonData);
			 */

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info("Inside Driver >> deleteDriver >>after decoding =" +
			 * jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("Inside Driver >> deleteDriver >> = sign found");
			}

			log.info("deleteDriver >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String driverId = (String) obj.get("driverId");

				/*
				 * log.info("deleteDriver >> driverId =" + driverId);
				 */

				boolean dbUpdateStatus = DatabaseManager.deleteDriver(driverId);

				if (dbUpdateStatus) {

					responseMap.put("code", "200");
					responseMap.put("msg", "Driver deleted.");

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Driver not deleted.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Driver data not availale.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("deleteDriver >>" + " Driver delete error. HTTP code is "
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
	@Path("bookings/get")
	@Produces(MediaType.TEXT_HTML)
	public Response getBookingDetails(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			/*
			 * JSONObject driverBookingDetailsRequestJson = new JSONObject();
			 * driverBookingDetailsRequestJson.put("phone", "1234567891");
			 * driverBookingDetailsRequestJson.put("driverId", "2");
			 * 
			 * jsonData = driverBookingDetailsRequestJson.toJSONString();
			 */

			// log.info("getBookingDetails before decoding = " + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("getBookingDetails >>after decoding =" + jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("getBookingDetails >> = sign found");
			}

			log.info("getBookingDetails >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				// log.info("getBookingDetails >>driver Phone =" + phone);
				String driverId = (String) obj.get("driverId");
				// log.info("getBookingDetails >> driverId =" + driverId);

				long bookingId = DatabaseManager
						.searchBookingIdFromDriverMaster(driverId);
				log.info("getBookingDetails >> searched bookingId ="
						+ bookingId);
				
				if (bookingId != 0) {
					
					TravelMaster tm = DatabaseManager
							.searchBookingDetailsByBookingId(bookingId + "");
					if (tm != null && tm.isDbStatus()
							&& !tm.getDriverId().equalsIgnoreCase("0")
							&& !MyUtil.getIsDatePassed(tm.getDateTime())) {

						// if (tm != null && tm.isDbStatus() &&
						// !tm.getDriverId().equalsIgnoreCase("0") ) {

						responseMap.put("code", "200");
						responseMap.put("msg", "Booking details fetched.");
						responseMap.put("bookingId", tm.getBookingId() + "");
						responseMap.put("userId", tm.getUserId() + "");
						responseMap.put("driverId", tm.getDriverId() + "");
						responseMap.put("from", tm.getFrom());
						responseMap.put("to", tm.getTo());
						responseMap.put("time", tm.getDateTime().toString());
						responseMap.put("bookingStatus", tm.getBookingStatus());
						responseMap.put("bookingStatusCode",
								tm.getBookingStatusCode());
						responseMap.put("travellerName", tm.getTravellerName());
						responseMap.put("driverName", tm.getDriverName());
						responseMap.put("driverPhone", tm.getDriverPhone());
						responseMap.put("vehicleNo", tm.getVehicleNo());
						responseMap.put("driverStatus", tm.getDriverStatus());
						responseMap.put("driverCurrAddr",
								tm.getDriverCurrAddress());
						responseMap.put("travellerPhone",
								tm.getTravellerPhone());

					} else {

						if (tm.getDriverId().equalsIgnoreCase("0")) {
							DatabaseManager
									.deleteBookingIdFromDriverMaster(driverId);
						}

						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap
								.put("msg", "Booking details not available.");
					}
				}else{
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap
							.put("msg", "Booking details not available.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Booking data not available.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("getBookingDetails >> Bookings Error. HTTP bookingStatus code is "
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

	@GET
	@Path("locations")
	@Produces(MediaType.TEXT_HTML)
	public Response getDriversLocation() {
		// String data = "";

		JSONArray jsonArray = null;
		try {

			// log.info("Inside Driver >> getDriversLocation");

			if (CacheBuilder.driversDataMap.size() > 0) {

				Set<Long> keys = CacheBuilder.driversDataMap.keySet();
				jsonArray = new JSONArray();

				for (long key : keys) {
					try {

						DriverMaster dm = CacheBuilder.driversDataMap.get(key);
						
						long currentTime = MyUtil.getCalanderFromDateStr(
								MyUtil.getCurrentDateFormattedString()).getTimeInMillis();
						long driverLastLocUpdateTime = dm.getLocationUpdateTime().getTime();
						
						long driverInActiveTimeDiff = currentTime - driverLastLocUpdateTime;
						
						if( driverInActiveTimeDiff < Long.parseLong(ConfigDetails.constants.get("DRIVER_MAP_ICON_DISABLE_TIME"))){
							
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("driverId", dm.getDriverId() + "");
							map.put("firstName", dm.getFirstName());
							map.put("phoneNumber", dm.getPhoneNumber());
							map.put("driverStatus", dm.getDriverStatus());
							map.put("currAddr", dm.getCurrAddr());
							map.put("currLongt", dm.getCurrLongt());
							map.put("currLat", dm.getCurrLat());
							map.put("bookingId", dm.getBookingId());
							map.put("code", "200");
							map.put("msg", "Driver details fetched.");
							JSONObject obj = new JSONObject();
							obj.putAll(map);
							jsonArray.add(obj);

						}else{
							log.info("getDriversLocation >> DriverId= "+ dm.getDriverId() + " is Inactive");
						}

						
					} catch (Exception e) {

					}
				}

			} else {

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				map.put("msg", "No drivers are provisioned.");
				JSONObject obj = new JSONObject();
				obj.putAll(map);
				jsonArray.add(obj);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (jsonArray.size() < 1) {

			log.info("getDriversLocation >> Bookings Error. HTTP bookingStatus code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			map.put("msg", "Server Error.");
			JSONObject obj = new JSONObject();
			obj.putAll(map);
			jsonArray.add(obj);

			return Response.status(200).entity(jsonArray.toJSONString())
					.build();
		} else {
			return Response.status(200).entity(jsonArray.toJSONString())
					.build();
		}

	}

	@POST
	@Path("myloc")
	@Produces(MediaType.TEXT_HTML)
	public Response updateDriverLocation(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info
			 * ("Inside Driver >> updateDriverStatus >>after decoding =" +
			 * jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				/*
				 * log.info
				 * ("Inside Driver >> updateDriverStatus >> = sign found");
				 */
			}

			log.debug("updateDriverStatus >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				String currAddr = (String) obj.get("currAddr");
				String currLongt = (String) obj.get("currLongt");
				String currLat = (String) obj.get("currLat");
				String driverId = (String) obj.get("driverId");
				// String phoneNumber = (String) obj.get("phoneNumber");

				/*
				 * log.info("driverStatus=" + driverStatus);
				 * log.info("currAddr=" + currAddr); log.info("currLongt=" +
				 * currLongt); log.info("currLat=" + currLat);
				 * log.info("driverId=" + driverId); log.info("phoneNumber=" +
				 * phoneNumber);
				 */
				
				if(!currAddr.equalsIgnoreCase("NoAddress")){
					
					DriverMaster dm = new DriverMaster();
					// dm.setDriverStatus(driverStatus);
					dm.setCurrAddr(currAddr);
					dm.setCurrLat(currLat);
					dm.setCurrLongt(currLongt);
					dm.setDriverId(driverId);
					// dm.setPhoneNumber(phoneNumber);

					boolean dbUpdateStatus = DatabaseManager
							.updateDriverLocation(dm);

					if (dbUpdateStatus) {

						DriverMaster dm1 = CacheBuilder.driversDataMap.get(Long
								.parseLong(driverId));
						if (dm1 != null) {
							
							dm1.setDriverId(driverId);							
							dm1.setCurrAddr(currAddr);
							dm1.setCurrLat(currLat);
							dm1.setCurrLongt(currLongt);
							dm1.setLocationUpdateTime(new Date(
									MyUtil
									.getCalanderFromDateStr(
											MyUtil
											.getCurrentDateFormattedString()
											).getTimeInMillis()
										)
							);

							CacheBuilder.driversDataMap.put(
									Long.parseLong(driverId), dm1);

							responseMap.put("code", "200");
							responseMap.put("msg", "Driver Status updated.");

							DriverMaster driverMaster1 = CacheBuilder.driversDataMap
									.get(Long.parseLong(driverId));

							log.debug("updateDriverStatus >> DriverDetails and Location="
									+ driverMaster1.getFirstName()
									+ "-"
									+ driverMaster1.getDriverId()
									+ ", "
									+ driverMaster1.getCurrAddr()
									+ ", lat="
									+ currLat + ", longt=" + currLongt);
						}

					} else {
						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap.put("msg", "Driver location not updated.");
					}
					
				}

				

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Driver location null.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			/*
			 * log.info (
			 * "Inside Driver >> updateDriverStatus >> Bookings Error. HTTP bookingStatus code is "
			 * +Constants.BOOKING_FAILED_CODE+"." );
			 */

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
	@Path("statustemp")
	@Produces(MediaType.TEXT_HTML)
	public Response updateDriverStatusTemp(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info
			 * ("Inside Driver >> updateDriverStatus >>after decoding =" +
			 * jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				/*
				 * log.info
				 * ("Inside Driver >> updateDriverStatus >> = sign found");
				 */
			}

			// log.info("updateDriverStatus >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				// String driverStatus = (String) obj.get("driverStatus");
				// //String currAddr = (String) obj.get("currAddr");
				// String currLongt = (String) obj.get("currLongt");
				// String currLat = (String) obj.get("currLat");
				String driverId = (String) obj.get("driverId");
				String phoneNumber = (String) obj.get("phoneNumber");

				/*
				 * log.info("driverStatus=" + driverStatus);
				 * log.info("currAddr=" + currAddr); log.info("currLongt=" +
				 * currLongt); log.info("currLat=" + currLat);
				 * log.info("driverId=" + driverId); log.info("phoneNumber=" +
				 * phoneNumber);
				 */

				long bookingId = DatabaseManager
						.searchBookingIdFromDriverMaster(driverId);

				/*
				 * log.info("updateDriverStatus >> searched bookingId =" +
				 * bookingId);
				 */

				if (bookingId != 0) {
					TravelMaster tm = DatabaseManager
							.searchBookingDetailsByBookingId(bookingId + "");
					if (tm != null && tm.isDbStatus()) {

						/*
						 * log.info("updateDriverStatus >> searched BookingStatus ="
						 * + tm.getBookingStatus());
						 */

						if (tm.getBookingStatus().equalsIgnoreCase(
								ConfigDetails.constants.get("BOOKING_SCHEDULED_MSG"))) {
							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_SCHEDULED_CODE"));
						} else if (tm.getBookingStatus().equalsIgnoreCase(
								ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"))) {
							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_CONFORMED_CODE"));
						} else if (tm.getBookingStatus().equalsIgnoreCase(
								ConfigDetails.constants.get("BOOKING_ON_THE_WAY_MSG"))) {
							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_ON_THE_WAY_CODE"));
						}

						responseMap.put("msg", "Booking details fetched.");
						responseMap.put("bookingId", tm.getBookingId() + "");
						responseMap.put("userId", tm.getUserId() + "");
						responseMap.put("driverId", tm.getDriverId() + "");
						responseMap.put("from", tm.getFrom());
						responseMap.put("to", tm.getTo());
						responseMap.put("time", tm.getDateTime().toString());
						responseMap.put("bookingStatus", tm.getBookingStatus());
						responseMap.put("bookingStatusCode",
								tm.getBookingStatusCode());
						responseMap.put("travellerName", tm.getTravellerName());
						responseMap.put("driverName", tm.getDriverName());
						responseMap.put("driverPhone", tm.getDriverPhone());
						responseMap.put("vehicleNo", tm.getVehicleNo());
						responseMap.put("driverStatus", tm.getDriverStatus());
						responseMap.put("driverCurrAddr",
								tm.getDriverCurrAddress());
						responseMap.put("travellerPhone",
								tm.getTravellerPhone());

					} else if (bookingId == 0) {
						responseMap.put("code", ConfigDetails.constants.get("BOOKING_DROPPED_CODE"));
					}
				} else if (bookingId == 0) {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_DROPPED_CODE"));
				}

				/*
				 * log.info (
				 * "Inside Driver >> updateDriverStatus >> booking responseMap ="
				 * + responseMap.toString());
				 */

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Booking data not available.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			/*
			 * log.info (
			 * "Inside Driver >> updateDriverStatus >> Bookings Error. HTTP bookingStatus code is "
			 * +Constants.BOOKING_FAILED_CODE+"." );
			 */

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
	@Path("status")
	@Produces(MediaType.TEXT_HTML)
	public Response updateDriverStatus(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info
			 * ("Inside Driver >> updateDriverStatus >>after decoding =" +
			 * jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				/*
				 * log.info
				 * ("Inside Driver >> updateDriverStatus >> = sign found");
				 */
			}

			// log.info("updateDriverStatus >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String driverStatus = (String) obj.get("driverStatus");
				String currAddr = (String) obj.get("currAddr");
				String currLongt = (String) obj.get("currLongt");
				String currLat = (String) obj.get("currLat");
				String driverId = (String) obj.get("driverId");
				String phoneNumber = (String) obj.get("phoneNumber");

				/*
				 * log.info("driverStatus=" + driverStatus);
				 * log.info("currAddr=" + currAddr); log.info("currLongt=" +
				 * currLongt); log.info("currLat=" + currLat);
				 * log.info("driverId=" + driverId); log.info("phoneNumber=" +
				 * phoneNumber);
				 */
				
				if(!currAddr.equalsIgnoreCase("NoAddress")){
					
					
					
				}

				DriverMaster dm = new DriverMaster();
				dm.setDriverStatus(driverStatus);
				dm.setCurrAddr(currAddr);
				dm.setCurrLat(currLat);
				dm.setCurrLongt(currLongt);
				dm.setDriverId(driverId);
				dm.setPhoneNumber(phoneNumber);

				boolean dbUpdateStatus = DatabaseManager.updateDriverStatus(dm);

				if (dbUpdateStatus) {

					DriverMaster dm1 = CacheBuilder.driversDataMap.get(Long
							.parseLong(driverId));
					if (dm1 != null) {

						dm1.setDriverStatus(driverStatus);
						dm1.setCurrAddr(currAddr);
						dm1.setCurrLat(currLat);
						dm1.setCurrLongt(currLongt);
						dm1.setLocationUpdateTime(new Date(
								MyUtil
								.getCalanderFromDateStr(
										MyUtil
										.getCurrentDateFormattedString()
										).getTimeInMillis()
									)
						);

						CacheBuilder.driversDataMap.put(
								Long.parseLong(driverId), dm1);

						responseMap.put("code", "200");
						responseMap.put("msg", "Driver Status updated.");

						DriverMaster driverMaster1 = CacheBuilder.driversDataMap
								.get(Long.parseLong(driverId));

						log.info("updateDriverStatus >> DriverId , Address , Lat, Long = "
								+ driverMaster1.getFirstName()
								+ "-"
								+ driverMaster1.getDriverId()
								+ ", "
								+ driverMaster1.getCurrAddr()
								+ ", lat="
								+ currLat + ", longt=" + currLongt);
					}

					long bookingId = DatabaseManager
							.searchBookingIdFromDriverMaster(driverId);

					/*
					 * log.info("updateDriverStatus >> searched bookingId =" +
					 * bookingId);
					 */

					if (bookingId != 0) {
						TravelMaster tm = DatabaseManager
								.searchBookingDetailsByBookingId(bookingId + "");
						if (tm != null && tm.isDbStatus()) {

							/*
							 * log.info(
							 * "updateDriverStatus >> searched BookingStatus ="
							 * + tm.getBookingStatus());
							 */

							if (tm.getBookingStatus().equalsIgnoreCase(
									ConfigDetails.constants.get("BOOKING_SCHEDULED_MSG"))) {
								responseMap.put("code",
										ConfigDetails.constants.get("BOOKING_SCHEDULED_CODE"));
							} else if (tm.getBookingStatus().equalsIgnoreCase(
									ConfigDetails.constants.get("BOOKING_CONFORMED_MSG"))) {
								responseMap.put("code",
										ConfigDetails.constants.get("BOOKING_CONFORMED_CODE"));
							} else if (tm.getBookingStatus().equalsIgnoreCase(
									ConfigDetails.constants.get("BOOKING_ON_THE_WAY_MSG"))) {
								responseMap.put("code",
										ConfigDetails.constants.get("BOOKING_ON_THE_WAY_CODE"));
							}

							responseMap.put("msg", "Booking details fetched.");
							responseMap
									.put("bookingId", tm.getBookingId() + "");
							responseMap.put("userId", tm.getUserId() + "");
							responseMap.put("driverId", tm.getDriverId() + "");
							responseMap.put("from", tm.getFrom());
							responseMap.put("to", tm.getTo());
							responseMap
									.put("time", tm.getDateTime().toString());
							responseMap.put("bookingStatus",
									tm.getBookingStatus());
							responseMap.put("bookingStatusCode",
									tm.getBookingStatusCode());
							responseMap.put("travellerName",
									tm.getTravellerName());
							responseMap.put("driverName", tm.getDriverName());
							responseMap.put("driverPhone", tm.getDriverPhone());
							responseMap.put("vehicleNo", tm.getVehicleNo());
							responseMap.put("driverStatus",
									tm.getDriverStatus());
							responseMap.put("driverCurrAddr",
									tm.getDriverCurrAddress());
							responseMap.put("travellerPhone",
									tm.getTravellerPhone());

						} else if (bookingId == 0) {
							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_DROPPED_CODE"));
						}
					} else if (bookingId == 0) {
						responseMap.put("code", ConfigDetails.constants.get("BOOKING_DROPPED_CODE"));
					}

					/*
					 * log.info (
					 * "Inside Driver >> updateDriverStatus >> booking responseMap ="
					 * + responseMap.toString());
					 */

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Booking details not available.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Booking data not available.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			/*
			 * log.info (
			 * "Inside Driver >> updateDriverStatus >> Bookings Error. HTTP bookingStatus code is "
			 * +Constants.BOOKING_FAILED_CODE+"." );
			 */

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
	@Path("bookings/status")
	@Produces(MediaType.TEXT_HTML)
	public Response updateBookingStatusByDriver(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			/*
			 * log.info(
			 * "Inside Driver >> updateDriverBookingStatus before decoding = " +
			 * jsonData);
			 */

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info(
			 * "Inside Driver >> updateDriverBookingStatus >>after decoding =" +
			 * jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("Inside Driver >> updateDriverBookingStatus >> = sign found");
			}

			log.info("updateBookingStatusByDriver >> after split =" + jsonData);

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

				/*
				 * log.info("updateDriverBookingStatus >> bookingId =" +
				 * bookingId);
				 */

				TravelMaster tm = new TravelMaster();
				tm.setBookingStatus(bookingStatus);
				tm.setBookingStatusCode(bookingStatusCode);
				tm.setUserId(userId);
				tm.setBookingId(bookingId);

				boolean dbUpdateStatus = DatabaseManager
						.updateBookingStatus(tm);

				if (dbUpdateStatus) {

					responseMap.put("code", "200");
					responseMap.put("msg", "Driver Booking Status updated.");

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg",
							"Driver Booking details not updated.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Booking data not available.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("updateDriverBookingStatus >>"
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

	String jsonCreater(Map mp) {

		JSONObject obj = new JSONObject();

		// obj.put("code", "200");
		// obj.put("msg", "Login Succesful.");
		obj.putAll(mp);

		// log.info("Formed JSON = "+ obj.toJSONString());

		return obj.toJSONString();
	}

}
