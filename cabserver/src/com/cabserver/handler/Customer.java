package com.cabserver.handler;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebFilter;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cabserver.db.DatabaseManager;
import com.cabserver.parser.GsonJsonParser;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.pojo.UserMaster;
import com.cabserver.scheduler.TaxiBookingQuartz;
import com.cabserver.util.CacheBuilder;
import com.cabserver.util.ConfigDetails;
import com.cabserver.util.MyUtil;

@Path("customers")
public class Customer {
	static final Logger log = Logger
			.getLogger(com.cabserver.handler.Customer.class.getName());

	@POST
	@Path("login")
	@Produces(MediaType.TEXT_HTML)
	
	public Response login(String jsonData) {
		// String data = "";
		ResponseBuilder rb = Response.ok();
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {
			
			rb.header("Access-Control-Allow-Credentials", "true");

			// log.info("Inside Customer >> login before decoding = " +
			// jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("Inside Customer >> login >>" + jsonData);

			// jsonData = jsonData.split("=")[1];
			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("Inside Customer >> signup >> data=" + jsonData);
			}

			log.info("Inside Customer >> login >> data=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				String password = (String) obj.get("password");

				// log.info("travellerPhone =" + phone);
				// log.info("password =" + password);

				if (phone != null && password != null) {

					UserMaster um = DatabaseManager.validateUser(phone,
							password);

					if (um != null && um.getAuthLevel() == 1) {

						// log.info("Login Successfull. HTTP code is 200.");

						responseMap.put("code", "200");
						responseMap.put("msg", "Login Succesful.");
						responseMap.put("authLevel", um.getAuthLevel() + "");
						responseMap.put("userId", um.getUserId());
						
						responseMap.put("phone", um.getPhone());
						responseMap.put("mobileOperator", um.getMobileOperator());
						responseMap.put("name", um.getFirstName());
						responseMap.put("lastName", um.getLastName());
						responseMap.put("sex", um.getSex());
						responseMap.put("mailId", um.getMailId());
						responseMap.put("address", um.getAddress());
						
					} else if (um != null && um.getAuthLevel() == 2) {

						// log.info("Admin Login Successfull. HTTP code is 200.");

						responseMap.put("code", "200");
						responseMap.put("msg", "Login Succesful.");
						responseMap.put("authLevel", um.getAuthLevel() + "");
						responseMap.put("userId", um.getUserId());
						
						
						responseMap.put("phone", um.getPhone());
						responseMap.put("mobileOperator", um.getMobileOperator());
						responseMap.put("name", um.getFirstName());
						responseMap.put("lastName", um.getLastName());
						responseMap.put("sex", um.getSex());
						responseMap.put("mailId", um.getMailId());
						responseMap.put("address", um.getAddress());
						
						
					} else {
						log.info("Login Error. HTTP code is "
								+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap.put("msg", "Incorrect phone or password.");
						responseMap.put("authLevel", "");
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
			rb.status(200);
			rb.entity(jsonCreater(responseMap));
			return rb.build();
			/*return Response.status(200).entity(jsonCreater(responseMap))
					.build();*/
		} else {
			rb.status(200);
			rb.entity(jsonCreater(responseMap));
			return rb.build();
			/*return Response.status(200).entity(jsonCreater(responseMap))
					.build();*/
		}

	}

	@POST
	@Path("signup")
	@Produces(MediaType.TEXT_HTML)
	public Response signup(String jsonData) {
		HashMap<String, String> responseMap = new HashMap<String, String>();

		try {

			// log.info("Inside Customer >> signup before decoding = " +
			// jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("Inside Customer >> signup >>" + jsonData);

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("Inside Customer >> signup >> data=" + jsonData);
			}

			log.info("signup >> json data after split=" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");

				// log.info("travellerPhone =" + phone);

				String mobileOperator = (String) obj.get("mobileOperator");
				// log.info("mobileOperator =" + mobileOperator);

				if (phone != null) {

					double id_from_db = DatabaseManager
							.searchUserMasterByPhone(phone);
					if (id_from_db != 0) {

						log.info("signup >> User already exists. Please Login. HTTP bookingStatus code is 409.");

						responseMap.put("code", "409");
						responseMap.put("userId", id_from_db + "");
						responseMap
								.put("msg",
										"User already exists.Please enter other phone number.");

					} else {

						UserMaster um = new UserMaster();

						um.setAdminId(1 + "");
						um.setCompanyId(101 + "");

						if (((String) obj.get("name")).contains(" ")) {

							String tmpNameArry[] = ((String) obj.get("name"))
									.split(" ");
							um.setFirstName(tmpNameArry[0]);
							um.setLastName(tmpNameArry[1]);

						} else {
							um.setFirstName((String) obj.get("name"));
							um.setLastName((String) obj.get("name"));
						}

						um.setPhone(phone);
						um.setMobileOperator(mobileOperator);
						/*
						 * try { um.setAge(Integer.parseInt((String)
						 * obj.get("age"))); } catch (Exception e) {
						 * log.info(e.getMessage()); }
						 */
						um.setSex((String) obj.get("sex"));
						String mailId = (String) obj.get("email");
						um.setMailId(mailId != null && mailId.length() > 0 ? mailId
								: "");
						um.setAddress((String) obj.get("address"));
						um.setPassword((String) obj.get("password"));
						um.setRole("user");

						UserMaster um1 = DatabaseManager.insertUserMaster(um);

						if (um1.isDbInsertStatus()) {
							log.info("signup >> SignUp Successfull. HTTP bookingStatus code is 200.");

							responseMap.put("code", "200");
							responseMap.put("userId", um1.getUserId() + "");
							responseMap.put("msg", "SignUp Succesful.");
						} else {
							responseMap.put("code", "500");
							responseMap.put("msg", "SignUp Error.");
						}

					}

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Phone number Error.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Data Error.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("signup >> SignUp Error. HTTP bookingStatus code is 500.");

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
	public Response deleteCustomer(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			/*
			 * log.info("deleteCustomer before decoding = " + jsonData);
			 */

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info("deleteCustomer >>after decoding =" + jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("deleteCustomer >> = sign found");
			}

			log.info("deleteCustomer >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String userId = (String) obj.get("userId");

				/*
				 * log.info("deleteDriver >> driverId =" + driverId);
				 */

				boolean dbUpdateStatus = DatabaseManager.deleteCustomer(userId);

				if (dbUpdateStatus) {

					responseMap.put("code", "200");
					responseMap.put("msg", "Customer deleted.");

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Customer not deleted.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Customer data not availale.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("deleteCustomer >>"
					+ " Customer delete error. HTTP code is "
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
	@Path("get")
	@Produces(MediaType.TEXT_HTML)
	public Response getCustomerDetails(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
		try {

			/*
			 * log.info("deleteCustomer before decoding = " + jsonData);
			 */

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			/*
			 * log.info("deleteCustomer >>after decoding =" + jsonData);
			 */

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("deleteCustomer >> = sign found");
			}

			/*
			 * log.info("deleteCustomer >> after split =" + jsonData);
			 */

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String userId = (String) obj.get("userId");

				/*
				 * log.info("deleteDriver >> driverId =" + driverId);
				 */

				UserMaster userMaster = DatabaseManager
						.getUserDetailsByUserId(userId);

				if (userMaster != null) {

					responseMap.put("code", "200");
					responseMap.put("msg", "Customer details fetched.");

					responseMap.put("userId", userMaster.getUserId());
					responseMap.put("phone", userMaster.getPhone());
					responseMap.put("sex", userMaster.getSex());

					responseMap.put("firstName", userMaster.getFirstName());
					responseMap.put("lastName", userMaster.getLastName());
					responseMap.put("mailId", userMaster.getMailId());
					responseMap.put("address", userMaster.getAddress());
					responseMap.put("mobileOperator",
							userMaster.getMobileOperator());

				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "Customer not deleted.");
				}

			} else {
				responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
				responseMap.put("msg", "Customer data not availale.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseMap.size() < 1) {

			log.info("deleteCustomer >>"
					+ " Customer delete error. HTTP code is "
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
			 * JSONObject bookingDetailsRequestJson = new JSONObject();
			 * bookingDetailsRequestJson.put("travellerPhone", "1234567891");
			 * bookingDetailsRequestJson.put("bookingId", "2");
			 * 
			 * jsonData = bookingDetailsRequestJson.toJSONString();
			 */

			// log.info("Inside Customer >> getBookings before decoding = " +
			// jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("Inside Customer >> getBookings >>after decoding ="+
			// jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("Inside Customer >> getBookings >> = sign found");
			}

			log.info("getBookingDetails >> after split =" + jsonData);

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("travellerPhone");
				// log.info("Inside Customer >> getBookings >> travellerPhone ="+
				// phone);
				String bookingId = (String) obj.get("bookingId") + "";
				// log.info("Inside Customer >> getBookings >> bookingId ="+
				// bookingId);

				if (bookingId != null && bookingId.length() > 1) {
					TravelMaster tm = DatabaseManager
							.searchBookingDetailsByBookingId(bookingId);
					if (tm != null && tm.isDbStatus()) {

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
						responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						responseMap
								.put("msg", "Booking details not available.");
					}
				} else {
					responseMap.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
					responseMap.put("msg", "No Booking made.");
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

	@POST
	@Path("bookings/list")
	@Produces(MediaType.TEXT_HTML)
	public Response getBookingsList(String jsonData) {
		// String data = "";
		HashMap<String, String> responseMap = new HashMap<String, String>();
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

				// LoginInfo result = new LoginInfo();
				String phone = (String) obj.get("phone");
				// log.info("phone =" + phone);

				String userId = (String) obj.get("userId");
				// log.info("userId =" + userId);

				if (phone != null && phone.length() >= 10 && userId != null
						&& userId.length() >= 1) {

					ArrayList<TravelMaster> trvlMstrArryLst = DatabaseManager
							.getAllBookingDetailsByUserId(userId);

					if (trvlMstrArryLst.size() > 0) {
						for (TravelMaster tmpTm : trvlMstrArryLst) {
							JSONObject obj1 = new JSONObject();

							obj1.put("code", "200");
							obj1.put("msg", "Bookings list fetched.");
							obj1.put("bookingId", tmpTm.getBookingId());
							obj1.put("name", tmpTm.getTravellerName());
							obj1.put("phone", tmpTm.getTravellerPhone());
							obj1.put("datetime", tmpTm.getBookingDateTime()
									.toString());
							obj1.put("from", tmpTm.getFrom());
							obj1.put("to", tmpTm.getTo());
							obj1.put("bookingStatus", tmpTm.getBookingStatus());
							obj1.put("bookingStatusCode",
									tmpTm.getBookingStatusCode());
							obj1.put("isBefore", tmpTm.isBefore());
							obj1.put("driverName", tmpTm.getDriverName()== null ?"":tmpTm.getDriverName());
							obj1.put("driverPhone", tmpTm.getDriverPhone()==null ? "":  tmpTm.getDriverPhone());
							obj1.put("userId", userId);

							// log.info("isBefore =" + tmpTm.isBefore());

							arryTM.add(obj1);
						}
					} else {
						JSONObject errObj = new JSONObject();
						errObj.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
						errObj.put("msg", "No Bookings.");
						arryTM.add(errObj);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			log.info("getBookingsList >> Bookings Error. HTTP bookingStatus code is "
					+ ConfigDetails.constants.get("BOOKING_FAILED_CODE") + ".");

			JSONObject errObj = new JSONObject();
			errObj.put("code", ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
			errObj.put("msg", "Server Error.");
			arryTM.add(errObj);
			return Response.status(200).entity(arryTM.toJSONString()).build();
		} else {
			return Response.status(200).entity(arryTM.toJSONString()).build();
		}

	}

	@POST
	@Path("bookings/myhistory")
	@Produces(MediaType.TEXT_HTML)
	public Response myBookingsHistory(String jsonData) {
		// String data = "";
		// HashMap<String, String> responseMap = new HashMap<String, String>();
		JSONArray arryTM = new JSONArray();
		try {

			// log.info("myBookingsHistory >> before decoding =" + jsonData);

			jsonData = (URLDecoder.decode(jsonData, "UTF-8"));

			// log.info("myBookingsHistory >> after decoding =" + jsonData);

			// jsonData = jsonData.split("=")[1];

			if (jsonData.contains("=")) {
				jsonData = jsonData.split("=")[1];
				// log.info("myBookingsHistory >> = sign found");
			}

			log.info("myBookingsHistory >> after split =" + jsonData);

			TravelMaster tm = new TravelMaster();

			if (jsonData != null && jsonData.length() > 1) {

				// Gson gson = new Gson();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonData);

				String userId = (String) obj.get("userId");
				// log.info("userId =" + userId);

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

				ArrayList<TravelMaster> trvlMstrArryLst = DatabaseManager
						.getBookingDetailsByDateByUserId(fromYear + "-"
								+ fromMonth + "-" + fromDate, toYear + "-"
								+ toMonth + "-" + toDate, userId);

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

					arryTM.add(obj1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (arryTM.size() < 1) {

			log.info("myBookingsHistory >> Bookings Error. HTTP booking history error code is "
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
	@Path("bookings")
	@Produces(MediaType.TEXT_HTML)
	public Response addBookings(String jsonData) {
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

			log.info("addBookings >> after split =" + jsonData);

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
				try {
					tm.setNoOfPassengers(Integer.parseInt(noOfPassengers));
				} catch (Exception e1) {					
					//e1.printStackTrace();
				}

				String mobileOperator = (String) obj.get("mobileOperator");
				// log.info("mobileOperator >> to =" + mobileOperator);
				tm.setMobileOperator(mobileOperator);

				String airline = (String) obj.get("airline");
				// log.info("airline >> to =" + airline);
				tm.setAirline(airline);

				String flightNumber = (String) obj.get("flightNumber");
				// log.info("flightNumber >> to =" + flightNumber);
				tm.setFlightNumber(flightNumber);

				tm.setBookingType(Integer.parseInt(ConfigDetails.constants.get("SCHEDULED_BOOKING_TYPE")));
				tm.setActivationStatus(Integer.parseInt(ConfigDetails.constants.get("SCHEDULED_BOOKING_DEACTIVE_STATUS")));
				
				
				String date = null;
				String month = null;
				String year = "";

				String hour = "";
				String min = "";
				String ampm = "";
				
				String type = (String) obj.get("type");
				
				if(type != null){
					
					String datetime = (String) obj.get("datetime");
					HashMap<String, String> dateComponents = MyUtil.getDateComponents(datetime);
					
					date = dateComponents.get("date");
					month = dateComponents.get("month");
					year = dateComponents.get("year");
					hour = dateComponents.get("hour");
					min = dateComponents.get("min");
					ampm = dateComponents.get("ampm");
					
				}else{
					date = (String) obj.get("date");
					month = (String) obj.get("month");
					year = (String) obj.get("year");
					hour = (String) obj.get("hour");
					min = (String) obj.get("min");
					ampm = (String) obj.get("ampm");
					
				}
				
				
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
						um.setMobileOperator(mobileOperator);
						um.setAge(Integer.parseInt("99"));
						um.setSex("U");
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
						tm.setDriverId(000 + "");
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

							log.info("addBookings >> Exception ="
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

						// log.info("addBookings >> Booking Scheduled at:" +
						// tm.getDateTime());

						if (addressStatus
								.equalsIgnoreCase(ConfigDetails.constants.get("INCORRECT_ADDRESS_CODE"))) {
							tm.setBookingStatusCode(addressStatus);
							tm.setBookingStatus(ConfigDetails.constants.get("INCORRECT_ADDRESS_MSG"));

						} else {
							tm.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_SCHEDULED_CODE"));
							tm.setBookingStatus(ConfigDetails.constants.get("BOOKING_SCHEDULED_MSG"));
						}

						TravelMaster tmFrmDB = DatabaseManager
								.insertTravelMaster(tm);

						if (tmFrmDB.isDbStatus()) {

							int bookingStatus = TaxiBookingQuartz
									.scheduleTaxiBookingJob(tmFrmDB);

							if (bookingStatus <= 0) {

								tmFrmDB.setBookingStatusCode(ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
								tmFrmDB.setBookingStatus(ConfigDetails.constants.get("BOOKING_FAILED_MSG"));
								DatabaseManager.updateBookingStatus(tmFrmDB);

								responseMap.put("code",
										ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
								if (bookingStatus < 0) {
									responseMap
											.put("msg",
													ConfigDetails.constants.get("MIN_BOOKING_TIME_ERROR_MSG"));
								} else if (bookingStatus == 0) {
									responseMap.put("msg",
											"Booking scheduling error.");
								}

							} else {

								CacheBuilder.bookingsDataMap.put(
										Long.parseLong(tmFrmDB.getBookingId()),
										tmFrmDB);

								responseMap.put("code", "200");
								responseMap.put("msg",
										ConfigDetails.constants.get("BOOKING_SCHEDULED_MSG"));
								responseMap.put("travellerPhone", phone);
								responseMap.put(
										"bookingId",
										new Double(tmFrmDB.getBookingId())
												.longValue() + "");
								log.info("addBookings >> "
										+ "Booking Scheduled. HTTP bookingStatus code is 200. responseMap="
										+ responseMap.toString());
							}

							new NotificationThread(tm).start();

						} else {
							responseMap.put("code",
									ConfigDetails.constants.get("BOOKING_FAILED_CODE"));
							responseMap.put("msg", "Booking creation error.");
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

			log.info("addBookings >> Bookings Error. HTTP bookingStatus code is "
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

}
