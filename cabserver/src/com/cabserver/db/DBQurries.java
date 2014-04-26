package com.cabserver.db;

import com.cabserver.util.Constants;

public interface DBQurries {

	// Querries got user-master
	
	static String select_all_from_usermaster = "SELECT * FROM cabguru.usermaster";
	
	static String select_usermaster_by_phoneno = "SELECT userId FROM cabguru.usermaster where phone=?";
	
	static String search_users_from_user_master = "SELECT userId, adminId, companyId, firstName, middleName, "
			+ "lastName, phone, age, sex, mailId, address, passwd, role, mobileOperator "
			+"FROM cabguru.usermaster WHERE phone LIKE ? OR firstName LIKE ? OR mailId LIKE ? AND role=\"user\" LIMIT 25";
	
	static String get_emailid_by_phoneno = "SELECT mailId,passwd FROM cabguru.usermaster where phone=?";
	
	static String validate_user = "SELECT userId,firstName,role FROM cabguru.usermaster where phone=? AND passwd=?";
	
	static String insert_usermaster = "INSERT INTO cabguru.usermaster"
			+ "(adminId, companyId, firstName, middleName, lastName, phone, "
			+ "sex, mailId, address, passwd, role, mobileOperator)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	static String update_usermaster = "UPDATE cabguru.usermaster SET firstName = ?, "
			+ "lastName = ?, phone = ?, sex = ?, "
			+ "mailId = ?, address = ?, passwd=?, mobileOperator=?  WHERE userId=?";
	
	static String select_user_details = "SELECT userId, adminId, companyId, firstName, "
			+ "middleName, lastName, phone, age, sex, mailId, address, passwd, role, mobileOperator "
			+"FROM cabguru.usermaster WHERE userId=?";
	
	static String delete_customer_from_UserMaster = "DELETE FROM cabguru.usermaster "
													+"WHERE userId = ?";
	

	// Querries got travel-master
	
	static String insert_TravelMaster = "INSERT INTO cabguru.travelmaster"
			+ "(userId, driverId, fromAddr, toAddr, fromLongt, fromLat, "
			+ "toLongt, toLat, bookingDateTime, bookingStatus,bookingStatusCode,travellerName,travellerPhone,"
			+ "noOfPassengers,mobileOperator,airline,flightNumber,bookingType,activationStatus)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	static String assign_Driver_for_Booking_in_TravelMaster = "UPDATE cabguru.travelmaster SET driverId=?, "
			+ "bookingStatus=?, bookingStatusCode=? WHERE userId =? AND bookingId=?";	

		
	static String update_BookingStatus_in_TravelMaster = "UPDATE cabguru.travelmaster SET "
			+ "bookingStatus=?,bookingStatusCode=? WHERE userId =? AND bookingId=?";
	
	static String update_manual_Booking_active_Status_in_TravelMaster = "UPDATE cabguru.travelmaster SET "
			+ "activationStatus=? WHERE bookingId=?";
	
	static String update_distance_and_fare_in_TravelMaster = "UPDATE cabguru.travelmaster SET "
			+ "totalDistanceTravelled=?, expectedFare=? WHERE bookingId=?";
	
	static String rectify_BookingData_in_TravelMaster = "UPDATE cabguru.travelmaster "
					+"SET fromAddr = ?, toAddr = ?, bookingDateTime=?," 
					+"travellerName = ?, noOfPassengers=?, mobileOperator=?, "
					+ "airline=?, flightNumber=?, activationStatus=? WHERE bookingId=?;";
	
	static String reset_booking_in_TravelMaster = "UPDATE cabguru.travelmaster SET driverId=0, "
			+ "bookingStatus=?, bookingStatusCode=?,bookingDateTime=? WHERE bookingId=?";
	
	static String select_by_bookingId_from_TravelMaster = "SELECT bookingId, userId, driverId, fromAddr, "
			+ "toAddr, fromLongt, fromLat, toLongt, toLat, bookingDateTime, "
			+ "bookingStatus, bookingStatusCode, totalDistanceTravelled, "
			+ "feedBack, travellerName, travellerPhone "
									+"FROM cabguru.travelmaster where bookingId=?";
	
	static String select_all_by_userid_from_TravelMaster = "SELECT bookingId, userId, driverId, fromAddr, "
			+ "toAddr, fromLongt, fromLat, toLongt, toLat, bookingDateTime, bookingStatus, bookingStatusCode, "
			+ "totalDistanceTravelled, "
			+ "feedBack, travellerName, travellerPhone "
			+"FROM cabguru.travelmaster where userId=? AND DATE_FORMAT(bookingDateTime, '%m/%d/%Y %H:%i:%s') "
			+ "> DATE_FORMAT(?, '%m/%d/%Y %H:%i:%s') ORDER BY bookingId DESC LIMIT 20";
	
	
	static String select_all_by_statusCode_from_TravelMaster = "SELECT bookingId, userId, driverId, fromAddr, "
			+ "toAddr, fromLongt, fromLat, toLongt, toLat, bookingDateTime, bookingStatus, "
			+ "bookingStatusCode, totalDistanceTravelled, "
			+ "feedBack, travellerName, travellerPhone, noOfPassengers, "
			+ "mobileOperator, airline, flightNumber "
									+"FROM cabguru.travelmaster where (bookingStatusCode=? "
									+ "OR bookingStatusCode=? OR bookingStatusCode=?) "
									+ "AND DATE_FORMAT(bookingDateTime, '%m/%d/%Y %H:%i:%s') "
									+ "> DATE_FORMAT(?, '%m/%d/%Y %H:%i:%s')";
	
	
	static String select_by_date_from_TravelMaster = "SELECT * from cabguru.travelmaster "
			+ "WHERE DATE_FORMAT(`bookingDateTime`, '%Y-%m-%d') >= DATE(?) "
			+ "and DATE_FORMAT(`bookingDateTime`, '%Y-%m-%d') <= DATE(?) or bookingId = ? ORDER BY bookingId DESC";
	
	static String select_by_date_by_userId_from_TravelMaster = "SELECT * from cabguru.travelmaster "
			+ "WHERE DATE_FORMAT(`bookingDateTime`, '%Y-%m-%d') >= DATE(?) "
			+ "and DATE_FORMAT(`bookingDateTime`, '%Y-%m-%d') <= DATE(?) and userId = ? ORDER BY bookingId DESC";
	
	static String select_bookings_to_reschedule_from_TravelMaster="SELECT * FROM "
			+ "cabguru.travelmaster WHERE bookingStatusCode=201 AND "
			+ "DATE_FORMAT(bookingDateTime, '%m/%d/%Y %H:%i:%s') > DATE_FORMAT(?, '%m/%d/%Y %H:%i:%s')";
	
	static String select_bookings_to_make_cache_from_TravelMaster="SELECT * FROM cabguru.travelmaster "
			+ "WHERE DATE_FORMAT(bookingDateTime, '%m/%d/%Y %H:%i:%s') "
			+ "> DATE_FORMAT(?, '%m/%d/%Y %H:%i:%s')";
	
	/*static String check_bookings_queue_for_driverId_from_TravelMaster = "SELECT * FROM cabguru.travelmaster WHERE "
			+ "driverId=? AND DATE_FORMAT(bookingDateTime, '%m/%d/%Y %H:%i:%s') "
			+ "<= DATE_ADD(DATE_FORMAT(?, '%m/%d/%Y %H:%i:%s'), INTERVAL "
			+Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME+" MINUTE);";*/
	
	/*static String get_bookings_on_date_from_TravelMaster = "SELECT * FROM cabguru.travelmaster "
			+ "WHERE driverId=? AND DATE_FORMAT(bookingDateTime, '%Y-%m-%d') = DATE(?) "
			+ "AND DATE_FORMAT(bookingDateTime, '%Y-%m-%d') = DATE(?) "
			+ "AND DATE_FORMAT(bookingDateTime, '%Y-%m-%d') = DATE(?)";*/
	
	static String get_bookings_on_date_from_TravelMaster = "SELECT * from cabguru.travelmaster "
			+ "WHERE driverId=? AND bookingStatusCode != \"204\" AND bookingStatusCode != \"205\" "
			+ "AND DATE_FORMAT(bookingDateTime, '%Y-%m-%d') >= DATE(?) "
			+ "and DATE_FORMAT(bookingDateTime, '%Y-%m-%d') <= DATE(?) ORDER BY bookingId DESC";
	
	
	
	
	
	
	
	// Querries got driver-master	
	
	static String insert_DriverMaster = "INSERT INTO cabguru.drivermaster (adminId, companyId, "
			+ "firstName, lastName, "
			+ "age, sex, mailId, address, driverLicense, phoneNumber, driverStatus, "
			+ "driverCategory,mobileOperator) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	
	static String update_Driver_Status_in_DriverMaster = "UPDATE cabguru.drivermaster "
									+"SET driverStatus =?, currAddr =?, currLongt =?, currLat =?" 
									+" WHERE driverId=? AND phoneNumber=?";
	
	static String update_Driver_Location_in_DriverMaster = "UPDATE cabguru.drivermaster "
			+"SET currAddr =?, currLongt =?, currLat =?, locationUpdateTime=?" 
			+" WHERE driverId=?";
	
	static String assign_BookingId_to_driver_in_DriverMaster = "UPDATE cabguru.drivermaster "
										+"SET bookingId = ?, driverStatus = ?" 
										+" WHERE  driverId=?";
	
	static String update_Driver_Details_DriverMaster = "UPDATE cabguru.drivermaster SET firstName =?, lastName =?, age =?, "
			+ "sex =?, mailId=?, address =?, driverLicense =?, "
			+ "phoneNumber =?, driverCategory=?, mobileOperator=? WHERE driverId=?";
	
	static String delete_BookingId_for_Driver_in_DriverMaster = "UPDATE cabguru.drivermaster "
			+"SET bookingId = 0" 
			+" WHERE  driverId=?";	
	
	
	static String get_BookingId_by_driverId_from_DriverMaster = "SELECT bookingId FROM "
			+ "cabguru.drivermaster where driverId=?";
	
	static String select_all_Company_Drivers_from_DriverMaster = "SELECT * "
			+ "FROM cabguru.drivermaster WHERE driverCategory=1";
	
	static String select_all_Non_Company_Drivers_from_DriverMaster = "SELECT driverId,adminId,companyId,firstName,lastName,"
			+ " phoneNumber,mailId, address, driverLicense, phoneNumber,vehicleNo, "
			+ "driverStatus, currAddr, currLongt, currLat, bookingId,driverCategory, mobileOperator "
												+"FROM cabguru.drivermaster WHERE driverCategory=0";
	
	static String search_Drivers_in_DriverMaster = "SELECT driverId, firstName, lastName, phoneNumber, age, sex, mailId, "
			+ "driverLicense, address, currAddr, driverStatus, driverCategory, mobileOperator "
						+"FROM cabguru.drivermaster WHERE phoneNumber LIKE ? "
						+ "OR firstName LIKE ? OR driverLicense LIKE ? LIMIT 25";
	
	static String authenticate_Driver_in_DriverMaster = "SELECT driverId,adminId,companyId,firstName,lastName,"
			+ "phoneNumber,mailId,address,driverLicense,phoneNumber,vehicleNo,"
			+ "driverStatus,currAddr, currLongt, currLat, bookingId,driverCategory,mobileOperator "
			+"FROM cabguru.drivermaster WHERE phoneNumber=?";
	
	static String select_Driver_in_DriverMaster = "SELECT driverId,adminId,companyId,firstName,lastName,"
			+ "phoneNumber,mailId,address,driverLicense,phoneNumber,vehicleNo,"
			+ "driverStatus,currAddr, currLongt, currLat, bookingId,driverCategory,mobileOperator "
			+"FROM cabguru.drivermaster WHERE driverId=?";
	
	static String delete_driver_from_DriverMaster = "DELETE FROM cabguru.drivermaster "
													+"WHERE driverId = ?";
	

	// Querries for bookings-master
	
	/*static String insert_in_BookingsMaster = "INSERT INTO cabguru.bookingsmaster"
			+"(bookingId, driverId, bookingDateTime, driverCategory) "
			+" VALUES (?, ?, ?, ?)";
	
	static String update_BookingData_in_BookingMaster ="UPDATE cabguru.bookingsmaster" 
					+"SET  driverId = ?, bookingDateTime = ?, driverCategory = ? "
					+"WHERE bookingId = ?;";
	
	static String delete_BookingData_from_BookingsMaster ="DELETE FROM cabguru.bookingsmaster "
			+ "WHERE bookingId = ?";
	
	static String select_Bookings_from_BookingsMaster = "SELECT bookingId, driverId, "
			+ "bookingDateTime, driverCategory FROM cabguru.bookingsmaster"; */
}
