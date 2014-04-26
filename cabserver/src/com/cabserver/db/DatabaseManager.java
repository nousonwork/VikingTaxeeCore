package com.cabserver.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.cabserver.pojo.DriverMaster;
import com.cabserver.pojo.TravelMaster;
import com.cabserver.pojo.UserMaster;
import com.cabserver.scheduler.TaxiBookingQuartz;
import com.cabserver.util.CacheBuilder;
import com.cabserver.util.Constants;
import com.cabserver.util.MyUtil;

public class DatabaseManager {

	public static Connection conn = null;
	

	static final Logger log = Logger
			.getLogger(com.cabserver.db.DatabaseManager.class.getName());

	synchronized public static Connection getConnection() {

		try {
			if ((DatabaseManager.conn == null)
					|| (DatabaseManager.conn.isClosed() == true)) {

				String url = "jdbc:mysql://" + Constants.DATABASE_IP + ":"
						+ Constants.DATABASE_PORT + "/"
						+ Constants.DATABASE_NAME;
				Class.forName("com.mysql.jdbc.Driver").newInstance();

				DatabaseManager.conn = DriverManager.getConnection(url,
						Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
				// log.info("Inside DatabaseManager >> getConnection >> Database connection established");

			} else {
				// log.info("Inside DatabaseManager >> getConnection >> Got previous connection reference");
				return DatabaseManager.conn;
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> Cannot connect to database server " +
			 * e.getMessage() + "\nRetrying connection with new reference");
			 */
			DatabaseManager.conn = DatabaseManager.refreshConnection();

		}

		return DatabaseManager.conn;

	}

	synchronized public static Connection refreshConnection() {

		try {

			String url = "jdbc:mysql://" + Constants.DATABASE_IP + ":"
					+ Constants.DATABASE_PORT + "/" + Constants.DATABASE_NAME;
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			DatabaseManager.conn = DriverManager.getConnection(url,
					Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
			// conn.setTransactionIsolation(com.mysql.jdbc.Connection.TRANSACTION_READ_COMMITTED);
			// log.info("Inside DatabaseManager >> getConnection >> Refreshed Database connection established");

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside refreshConnection >>Retrying connection also failed" +
			 * e.getMessage());
			 */
			// System.exit(0);
		}
		return DatabaseManager.conn;
	}

	public static UserMaster insertUserMaster(UserMaster um) {

		PreparedStatement query = null;
		UserMaster umast = um;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(DBQurries.insert_usermaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, um.getAdminId());
			query.setString(2, um.getCompanyId());
			query.setString(3, um.getFirstName());
			query.setString(4, um.getMiddleName());
			query.setString(5, um.getLastName());
			query.setString(6, um.getPhone());
			query.setString(7, um.getSex());
			query.setString(8, um.getMailId());
			query.setString(9, um.getAddress());
			query.setString(10, um.getPassword());
			query.setString(11, um.getRole());
			query.setString(12, um.getMobileOperator());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				umast.setDbInsertStatus(true);
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				ResultSet _reg_rs = query.getGeneratedKeys();

				long _gen_id = _reg_rs.next() ? _reg_rs.getInt(1) : 0;

				_reg_rs.close();

				umast.setUserId(_gen_id + "");
				log.info(" insertUserMaster >>New customer added with userId="
						+ _gen_id);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return umast;
	}

	public static UserMaster updateUserMaster(UserMaster um) {

		PreparedStatement query = null;
		UserMaster umast = um;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(DBQurries.update_usermaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, um.getFirstName());
			query.setString(2, um.getLastName());
			query.setString(3, um.getPhone());
			query.setString(4, um.getSex());
			query.setString(5, um.getMailId());
			query.setString(6, um.getAddress());
			query.setString(7, um.getPassword());
			query.setString(8, um.getMobileOperator());
			query.setString(9, um.getUserId());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				umast.setDbInsertStatus(true);
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				ResultSet _reg_rs = query.getGeneratedKeys();

				long _gen_id = _reg_rs.next() ? _reg_rs.getInt(1) : 0;

				_reg_rs.close();

				umast.setUserId(_gen_id + "");
				log.info("Updated UserMaster  >> userId=" + _gen_id);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return umast;
	}

	public static DriverMaster updateDriverMaster(DriverMaster dm) {

		PreparedStatement query = null;
		DriverMaster dmast = dm;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.update_Driver_Details_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, dm.getFirstName());
			query.setString(2, dm.getLastName());
			query.setInt(3, dm.getAge());
			query.setString(4, dm.getSex());
			query.setString(5, dm.getMailId());
			query.setString(6, dm.getAddress());
			query.setString(7, dm.getDriverLicense());
			query.setString(8, dm.getPhoneNumber());
			query.setInt(9, dm.getDriverCategory());
			query.setString(10, dm.getMobileOperator());
			query.setString(11, dm.getDriverId());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				dmast.setDbInsertStatus(true);
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				ResultSet _reg_rs = query.getGeneratedKeys();

				long _gen_id = _reg_rs.next() ? _reg_rs.getInt(1) : 0;

				_reg_rs.close();

				dmast.setDriverId(_gen_id + "");
				log.info("Uupdated DriverMaster >> driverId=" + _gen_id);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return dmast;
	}

	public static TravelMaster updateBookingData(TravelMaster tm) {

		PreparedStatement query = null;
		TravelMaster tmast = tm;
		conn = DatabaseManager.getConnection();

		try {
			conn.setAutoCommit(false);
			query = conn.prepareStatement(
					DBQurries.rectify_BookingData_in_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);

			// query.setString(1, tm.getDriverId());
			query.setString(1, tm.getFrom());
			query.setString(2, tm.getTo());
			// query.setString(4, tm.getStatus());
			// query.setString(3, Constants.BOOKING_CONFORMED_MSG);
			// query.setString(5, tm.getStatusCode());
			// query.setString(4, Constants.BOOKING_CONFORMED_CODE);
			Calendar cal = Calendar.getInstance();
			cal.setTime(tm.getDateTime());
			query.setTimestamp(3, new java.sql.Timestamp(cal.getTimeInMillis()));
			query.setString(4, tm.getTravellerName());
			query.setInt(5, tm.getNoOfPassengers());
			query.setString(6, tm.getMobileOperator());
			query.setString(7, tm.getAirline());
			query.setString(8, tm.getFlightNumber());
			query.setInt(9, Constants.MANUAL_BOOKING_ACTIVE_STATUS);
			query.setString(10, tm.getBookingId());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				
				if (conn.getAutoCommit() == false) {
					conn.commit();
					tmast.setDbStatus(true);
				}
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertUserMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return tmast;
	}

	public static DriverMaster insertDriverMaster(DriverMaster dm) {

		DriverMaster dmast = dm;
		PreparedStatement query = null;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(DBQurries.insert_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, dm.getAdminId());
			query.setString(2, dm.getCompanyId());
			query.setString(3, dm.getFirstName());
			query.setString(4, dm.getLastName());
			query.setInt(5, dm.getAge());
			query.setString(6, dm.getSex());
			query.setString(7, dm.getMailId());
			query.setString(8, dm.getAddress());
			query.setString(9, dm.getDriverLicense());
			query.setString(10, dm.getPhoneNumber());
			query.setString(11, Constants.DRIVER_STATUS_FREE_STR);
			query.setInt(12, (dm.getDriverCategory()) > 0 ? 1 : 0);
			query.setString(13, dm.getMobileOperator());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				dmast.setDbInsertStatus(true);
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				log.info("insertDriverMaster >>Inserted Values into DriverMaster table");

				ResultSet _reg_rs = query.getGeneratedKeys();

				long _gen_id = _reg_rs.next() ? _reg_rs.getInt(1) : 0;

				_reg_rs.close();

				dmast.setDriverId(_gen_id + "");

				if (dm.getDriverCategory() == 1) {					
					CacheBuilder.driversDataMap.put(_gen_id, dmast);
				}

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> insertDriverMaster >> Exception occurred "
			 * + e.getMessage());
			 */

			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				log.info("insertDriverMaster >> Exception occurred "
						+ e.getMessage());
				e.printStackTrace();

			}
		}
		return dmast;
	}

	public static TravelMaster insertTravelMaster(TravelMaster tm) {

		PreparedStatement query = null;
		conn = DatabaseManager.getConnection();
		TravelMaster tmTemp = tm;

		try {

			query = conn.prepareStatement(DBQurries.insert_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);

			/*
			 * log.info("insertTravelMaster >> tm.getDriverId= " +
			 * tm.getDriverId());
			 */

			query.setBigDecimal(1, new BigDecimal(tm.getUserId()));
			query.setBigDecimal(2, new BigDecimal(tm.getDriverId()));
			query.setString(3, tm.getFrom());
			query.setString(4, tm.getTo());
			query.setString(5, tm.getFromLongt());
			query.setString(6, tm.getToLongt());
			query.setString(7, tm.getFromLat());
			query.setString(8, tm.getToLat());
			Calendar cal = Calendar.getInstance();
			cal.setTime(tm.getDateTime());
			query.setTimestamp(9, new java.sql.Timestamp(cal.getTimeInMillis()));

			query.setString(10, tm.getBookingStatus());
			query.setString(11, tm.getBookingStatusCode());
			query.setString(12, tm.getTravellerName());
			query.setString(13, tm.getTravellerPhone());

			if (tm.getNoOfPassengers() != 0) {
				query.setInt(14, tm.getNoOfPassengers());
			} else {
				query.setInt(14, 0);
			}

			if (tm.getMobileOperator() != null
					&& tm.getMobileOperator().length() > 1) {
				query.setString(15, tm.getMobileOperator());
			} else {
				query.setString(15, "");
			}

			if (tm.getAirline() != null && tm.getAirline().length() > 1) {
				query.setString(16, tm.getAirline());
			} else {
				query.setString(16, "");
			}

			if (tm.getFlightNumber() != null
					&& tm.getFlightNumber().length() > 1) {
				query.setString(17, tm.getFlightNumber());
			} else {
				query.setString(17, "");
			}

			query.setInt(18, tm.getBookingType());
			query.setInt(19, tm.getActivationStatus());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				tmTemp.setDbStatus(true);
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				/*log.info("insertTravelMaster >>"
						+ "Inserted Values into TravelMaster table");*/

				ResultSet _reg_rs = query.getGeneratedKeys();

				long _gen_id = _reg_rs.next() ? _reg_rs.getInt(1) : 0;

				_reg_rs.close();

				tmTemp.setBookingId(_gen_id + "");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return tmTemp;
	}

	public static boolean assignManualBookingDriverToTravelMaster(
			TravelMaster tm) {
		/*log.info("assignManualBookingDriverToTravelMaster:" + "driverId="
				+ tm.getDriverId() + ", userId=" + tm.getUserId()
				+ ", bookingId=" + tm.getBookingId());*/
		PreparedStatement query1 = null;

		boolean flag = false;

		conn = DatabaseManager.getConnection();

		try {

			query1 = conn.prepareStatement(
					DBQurries.assign_Driver_for_Booking_in_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);

			query1.setBigDecimal(1, new BigDecimal(tm.getDriverId()));
			query1.setString(2, tm.getBookingStatus());
			query1.setString(3, tm.getBookingStatusCode());
			query1.setBigDecimal(4, new BigDecimal(tm.getUserId()));
			query1.setBigDecimal(5, new BigDecimal(tm.getBookingId()));

			int i = 0;
			i = query1.executeUpdate();
			if (i > 0) {
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}
				
				flag = true;
				log.info("assignManualBookingDriverToTravelMaster >>"
						+ "Updated booking data into TravelMaster table");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateTravelMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query1 != null) {
					query1.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static boolean assignManualBookingDriverToDriverMaster(
			String bookingId, String driverId) {

		PreparedStatement query2 = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {
			// Updating Driver Master .. assigning bookingId

			query2 = conn.prepareStatement(
					DBQurries.assign_BookingId_to_driver_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);
			query2.setBigDecimal(1, new BigDecimal(bookingId));
			query2.setString(2, Constants.DRIVER_STATUS_BUSY_STR);
			query2.setBigDecimal(3, new BigDecimal(driverId));

			int j = 0;
			j = query2.executeUpdate();
			if (j > 0) {
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}
				flag = true;

				
				  log.info("assignManualBookingDriverToDriverMaster >> " +
				  "Updated booking data DriverMaster table .. manual bookingId assigned"
				 );
				 
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateTravelMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query2 != null) {
					query2.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static boolean removeDriverFromBookingInTravelMaster(TravelMaster tm) {
		
		  /*log.info("removeDriverFromBookingInTravelMaster:" + "driverId=" +
		  tm.getDriverId() + "userId=" + tm.getUserId() + "bookingId=" +
		  tm.getBookingId());*/
		 
		PreparedStatement query1 = null;
		PreparedStatement query2 = null;

		boolean flag = false;

		conn = DatabaseManager.getConnection();

		try {

			if (tm.getBookingStatusCode().equalsIgnoreCase(
					Constants.INCORRECT_ADDRESS_CODE)
					|| tm.getBookingStatusCode().equalsIgnoreCase(
							Constants.BOOKING_SCHEDULED_CODE)) {

				try {

					boolean schedulerRemovedStatus = TaxiBookingQuartz
							.unscheduleBooking(CacheBuilder.bookingsDataMap
									.get(Long.parseLong(tm.getBookingId())));
					/*
					 * log.info(
					 * "removeDriverFromBookingInTravelMaster schedulerRemovedStatus ="
					 * + schedulerRemovedStatus);
					 */
				} catch (Exception e) {
					log.info("removeDriverFromBookingInTravelMaster ="
							+ e.getMessage());
				}

			}

			conn.setAutoCommit(false);

			TravelMaster tempTm = searchBookingDetailsByBookingId(tm
					.getBookingId());

			query1 = conn.prepareStatement(
					DBQurries.reset_booking_in_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query1.setString(1, tm.getBookingStatus());
			query1.setString(2, tm.getBookingStatusCode());
			query1.setTimestamp(3, (Timestamp) tempTm.getBookingDateTime());
			query1.setBigDecimal(4, new BigDecimal(tm.getBookingId()));

			int i = 0;
			i = query1.executeUpdate();
			if (i > 0) {
				if (conn.getAutoCommit() == false) {
					// conn.commit();
				}

				log.info("removeDriverFromBookingInTravelMaster >>"
						+ "Updated booking status into TravelMaster table");
			}

			if (tm.getBookingStatusCode().equalsIgnoreCase(
					Constants.BOOKING_CONFORMED_CODE)) {
				query2 = conn.prepareStatement(
						DBQurries.delete_BookingId_for_Driver_in_DriverMaster,
						Statement.RETURN_GENERATED_KEYS);
				// query2.setBigDecimal(1, new BigDecimal(tm.getBookingId()));
				query2.setBigDecimal(1, new BigDecimal(tm.getDriverId()));
				int j = 0;
				j = query2.executeUpdate();
				if (j > 0) {
					flag = true;
					if (conn.getAutoCommit() == false) {
						conn.commit();
					}

					log.info("removeDriverFromBookingInTravelMaster >> "
							+ "Updated Values into DriverMaster table .. driver removed");
				} else {
					conn.rollback();
					log.info("removeDriverFromBookingInTravelMaster >> "
							+ "Booking details update rolled Back.");
				}

			} else {
				conn.commit();
				flag = true;
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateTravelMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query1 != null) {
					query1.close();
				}
				if (query2 != null) {
					query2.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	
	public static boolean deleteBookingIdFromDriverMaster(String driverId) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.delete_BookingId_for_Driver_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, driverId);

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {

					conn.commit();
				}

				log.info("deleteBookingIdFromDriverMaster >>"
						+ "Deleted bookingId from DriverMaster table.");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> deleteBookingIdFromDriverMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> deleteBookingIdFromDriverMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}
	
	
	
	
	public static boolean deleteCustomer(String userId) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.delete_customer_from_UserMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, userId);

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {

					conn.commit();
				}

				log.info("deleteCustomer >>"
						+ "Deleted customer from UserMaster table.");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> deleteCustomer >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> deleteCustomer >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static boolean deleteDriver(String driverId) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.delete_driver_from_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, driverId);

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {

					conn.commit();
				}

				log.info("deleteDriver >>"
						+ "Deleted driver from DriverMaster table.");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> deleteDriver >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> deleteDriver >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static boolean updateBookingStatus(TravelMaster tm) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {		

			query = conn.prepareStatement(
					DBQurries.update_BookingStatus_in_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, tm.getBookingStatus());
			query.setString(2, tm.getBookingStatusCode());
			query.setBigDecimal(3, new BigDecimal(tm.getUserId()));
			query.setBigDecimal(4, new BigDecimal(tm.getBookingId()));
			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {

					conn.commit();
				}

				log.info("updateBookingStatus >>"
						+ "Updated booking details into TravelMaster.");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateBookingStatus >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> updateBookingStatus >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static boolean updateManualBookingActiveStatus(String bookingId) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn
					.prepareStatement(
							DBQurries.update_manual_Booking_active_Status_in_TravelMaster,
							Statement.RETURN_GENERATED_KEYS);

			query.setInt(1, Constants.MANUAL_BOOKING_ACTIVE_STATUS);
			query.setString(2, bookingId);

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				// log.info("updateDriverStatus >>Inserted driver driverStatus Values into DriverMaster table");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateDriverStatus >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static boolean updateDistanceAndFare(double distance, double fare,
			long bookingId) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.update_distance_and_fare_in_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setDouble(1, distance);
			query.setDouble(2, fare);
			query.setDouble(3, bookingId);

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {
					conn.commit();
				}

				// log.info("updateDistanceAndFare >>Inserted driver driverStatus Values into DriverMaster table");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateDistanceAndFare >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> updateDistanceAndFare >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	
	
	public static boolean updateDriverLocation(DriverMaster dm) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.update_Driver_Location_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			
			query.setString(1, dm.getCurrAddr());
			query.setString(2, dm.getCurrLongt());
			query.setString(3, dm.getCurrLat());			
			query.setTimestamp(4, new java.sql.Timestamp(
					(
							MyUtil.getCalanderFromDateStr(
									MyUtil.getCurrentDateFormattedString()
									)
					)
					.getTimeInMillis()
					)
			);
			query.setBigDecimal(5, new BigDecimal(dm.getDriverId()));
			
			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {

					conn.commit();
				}

				// log.info("updateDriverLocation >>Inserted driver driverStatus Values into DriverMaster table");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateDriverLocation >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> updateDriverLocation >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}
	
	
	
	public static boolean updateDriverStatus(DriverMaster dm) {

		PreparedStatement query = null;
		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.update_Driver_Status_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, dm.getDriverStatus());
			query.setString(2, dm.getCurrAddr());
			query.setString(3, dm.getCurrLongt());
			query.setString(4, dm.getCurrLat());
			query.setBigDecimal(5, new BigDecimal(dm.getDriverId()));
			query.setString(6, dm.getPhoneNumber());

			int i = 0;
			i = query.executeUpdate();
			if (i > 0) {
				flag = true;
				if (conn.getAutoCommit() == false) {

					conn.commit();
				}

				log.info("updateDriverStatus >>updated driverStatus Values into DriverMaster table");
			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> updateDriverStatus >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> insertTravelMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return flag;
	}

	public static ArrayList<TravelMaster> getAllBookingDetailsByUserId(
			String userId) {

		PreparedStatement query = null;
		TravelMaster tm = null;
		ArrayList<TravelMaster> trvlMstrArryLst = new ArrayList<TravelMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_all_by_userid_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, userId);
			query.setString(2, MyUtil.getCurrentDateFormattedString());
			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				tm.setDriverId(new BigDecimal(rs1.getDouble("driverId"))
						.longValueExact() + "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));

				if ((rs1.getTimestamp("bookingDateTime")).compareTo(new Date()) < 0) {
					tm.setBefore(true);
				} else {
					tm.setBefore(false);
				}

				try {

					DriverMaster assignedDriver = getDriverDetails(tm
							.getDriverId());
					if (assignedDriver != null) {
						tm.setDriverPhone(assignedDriver.getPhoneNumber());
						tm.setDriverStatus(assignedDriver.getDriverStatus());
						tm.setDriverName(assignedDriver.getFirstName());
						tm.setDriverCurrAddress(assignedDriver.getCurrAddr());
						tm.setVehicleNo(assignedDriver.getVehicleNo());
					}

				} catch (Exception e) {
					log.info("searchBookingDetailsByPhone >> "
							+ e.getMessage());
				}

				trvlMstrArryLst.add(tm);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return trvlMstrArryLst;
	}

	public static ArrayList<TravelMaster> getBookingsToRescheduleOnStartUp() {
		// log.info("getBookingsToRescheduleOnStartUp");

		PreparedStatement query = null;
		TravelMaster tm = null;
		ArrayList<TravelMaster> trvlMstrArryLst = new ArrayList<TravelMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_bookings_to_reschedule_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, MyUtil.getCurrentDateFormattedString());
			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				tm.setDriverId(new BigDecimal(rs1.getDouble("driverId"))
						.longValueExact() + "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setFromLongt(rs1.getString("fromLongt"));
				tm.setFromLat(rs1.getString("fromLat"));
				tm.setToLongt(rs1.getString("toLongt"));
				tm.setToLat(rs1.getString("toLat"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));
				tm.setBookingType(rs1.getInt("bookingType"));

				trvlMstrArryLst.add(tm);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return trvlMstrArryLst;
	}

	public static ArrayList<TravelMaster> getBookingsToMakeCacheOnStartUp() {

		PreparedStatement query = null;
		TravelMaster tm = null;
		ArrayList<TravelMaster> trvlMstrArryLst = new ArrayList<TravelMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_bookings_to_make_cache_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, MyUtil.getCurrentDateFormattedString());
			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				tm.setDriverId(new BigDecimal(rs1.getDouble("driverId"))
						.longValueExact() + "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setFromLongt(rs1.getString("fromLongt"));
				tm.setFromLat(rs1.getString("fromLat"));
				tm.setToLongt(rs1.getString("toLongt"));
				tm.setToLat(rs1.getString("toLat"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));

				trvlMstrArryLst.add(tm);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return trvlMstrArryLst;
	}

	/*public static boolean checkBookingsToDoAdminManualAdvanceBooking(
			String driverId) {

		PreparedStatement query = null;
		conn = DatabaseManager.getConnection();
		boolean checkFlag = false;

		try {

			query = conn
					.prepareStatement(
							DBQurries.check_bookings_queue_for_driverId_from_TravelMaster,
							Statement.RETURN_GENERATED_KEYS);
			query.setString(1, driverId);
			query.setString(2, MyUtil.getCurrentDateFormattedString());
			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				checkFlag = true;

			}

		} catch (Exception e) {
			
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 
				e.printStackTrace();

			}
		}
		return checkFlag;
	}*/

	public static ArrayList<TravelMaster> getAllBookingDetailsByStatusCode() {

		PreparedStatement query = null;
		TravelMaster tm = null;
		ArrayList<TravelMaster> trvlMstrArryLst = new ArrayList<TravelMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_all_by_statusCode_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, Constants.BOOKING_FAILED_CODE);
			query.setString(2, Constants.BOOKING_DENIED_CODE);
			query.setString(3, Constants.INCORRECT_ADDRESS_CODE);
			query.setString(4, MyUtil.getCurrentDateFormattedString());
			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				tm.setDriverId(new BigDecimal(rs1.getDouble("driverId"))
						.longValueExact() + "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));
				tm.setNoOfPassengers(rs1.getInt("noOfPassengers"));
				tm.setMobileOperator(rs1.getString("mobileOperator"));
				tm.setAirline(rs1.getString("airline"));
				tm.setFlightNumber(rs1.getString("flightNumber"));

				if ((rs1.getTimestamp("bookingDateTime")).compareTo(new Date()) < 0) {
					tm.setBefore(true);
				} else {
					tm.setBefore(false);
					trvlMstrArryLst.add(tm);
				}

			}

			log.info("getAllBookingDetailsByStatusCode >> trvlMstrArryLst.size()="
					+ trvlMstrArryLst.size());

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return trvlMstrArryLst;
	}

	public static ArrayList<TravelMaster> getBookingDetailsByDate(String from,
			String to, String bookingId) {

		PreparedStatement query = null;
		TravelMaster tm = null;
		ArrayList<TravelMaster> trvlMstrArryLst = new ArrayList<TravelMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_by_date_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, from);
			query.setString(2, to);
			query.setString(3, bookingId);

			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				
				long driverId = new BigDecimal(rs1.getDouble("driverId")).longValueExact();
				tm.setDriverId(driverId+ "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));

				if ((rs1.getTimestamp("bookingDateTime")).compareTo(new Date()) < 0) {
					tm.setBefore(true);
				} else {
					tm.setBefore(false);
				}
				
				
				tm.setNoOfPassengers(rs1.getInt("noOfPassengers"));
				tm.setMobileOperator(rs1.getString("mobileOperator"));
				tm.setAirline(rs1.getString("airline"));
				tm.setFlightNumber(rs1.getString("flightNumber"));
				
				if(driverId != 0){
					DriverMaster dm = getDriverDetails(driverId+"");
					
					tm.setDriverId(driverId+"");
					tm.setDriverName(dm.getFirstName());
					tm.setDriverPhone(dm.getPhoneNumber());
					tm.setDriverStatus(dm.getDriverStatus());
					tm.setDriverCurrAddress(dm.getCurrAddr());
				}
								

				trvlMstrArryLst.add(tm);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return trvlMstrArryLst;
	}

	public static ArrayList<TravelMaster> getBookingDetailsByDateByUserId(
			String from, String to, String userId) {

		PreparedStatement query = null;
		TravelMaster tm = null;
		ArrayList<TravelMaster> trvlMstrArryLst = new ArrayList<TravelMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_by_date_by_userId_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, from);
			query.setString(2, to);
			query.setString(3, userId);

			ResultSet rs1 = query.executeQuery();
			while (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				tm.setDriverId(new BigDecimal(rs1.getDouble("driverId"))
						.longValueExact() + "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));

				if ((rs1.getTimestamp("bookingDateTime")).compareTo(new Date()) < 0) {
					tm.setBefore(true);
				} else {
					tm.setBefore(false);
				}

				trvlMstrArryLst.add(tm);

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return trvlMstrArryLst;
	}

	public static long getBookingsForaDate(String driverId, Calendar bookingDate) {

		PreparedStatement query = null;
		boolean isFree = true;
		long existingBooking = 0;
		log.info("getBookingsForaDate >> driverId = "+ driverId);

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.get_bookings_on_date_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, driverId);
			
			 String dateStr_curr = bookingDate.get(Calendar.YEAR) + "-" +
			 (bookingDate.get(Calendar.MONTH)+1) + "-" +
			 bookingDate.get(Calendar.DATE);
			 

			String dateStr_back = bookingDate.get(Calendar.YEAR) + "-"
					+ (bookingDate.get(Calendar.MONTH) + 1) + "-"
					+ (bookingDate.get(Calendar.DATE) - 1);

			String dateStr_fwd = bookingDate.get(Calendar.YEAR) + "-"
					+ (bookingDate.get(Calendar.MONTH) + 1) + "-"
					+ (bookingDate.get(Calendar.DATE) + 1);

			
			  log.info("dateStr_curr=" + dateStr_curr +" " +
			  bookingDate.get(Calendar.HOUR_OF_DAY) +" " +
			  bookingDate.get(Calendar.MINUTE)+ " " +
			  bookingDate.get(Calendar.SECOND));
			  
			  log.info("dateStr_back=" + dateStr_back +" " +
			  bookingDate.get(Calendar.HOUR_OF_DAY) +" " +
			  bookingDate.get(Calendar.MINUTE)+ " " +
			  bookingDate.get(Calendar.SECOND));
			  
			  log.info("dateStr_fwd=" + dateStr_fwd +" " +
			  bookingDate.get(Calendar.HOUR_OF_DAY) +" " +
			  bookingDate.get(Calendar.MINUTE)+ " " +
			  bookingDate.get(Calendar.SECOND));
			 

			query.setString(2, dateStr_back);
			query.setString(3, dateStr_fwd);

			long booknigTimeStamp = bookingDate.getTimeInMillis();
			ResultSet rs1 = query.executeQuery();
			ArrayList<Long> timestamps = new ArrayList<Long>();
			HashMap<Long, Long> bookingMap = new HashMap<Long, Long>();

			while (rs1.next()) {

				// System.out.println("Bookings = ");
				/*
				 * System.out.println(new BigDecimal(rs1.getDouble("bookingId"))
				 * .longValueExact() + ", " + new
				 * BigDecimal(rs1.getDouble("driverId")) .longValueExact() +
				 * ", " + rs1.getTimestamp("bookingDateTime"));
				 */

				long timeTmp = rs1.getTimestamp("bookingDateTime").getTime();

				timestamps.add(timeTmp);
				bookingMap.put(timeTmp,
						new BigDecimal(rs1.getDouble("bookingId"))
								.longValueExact());

				/*
				 * tm = new TravelMaster(); tm.setBookingId(new
				 * BigDecimal(rs1.getDouble("bookingId")) .longValueExact() +
				 * ""); tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
				 * .longValueExact() + ""); tm.setDriverId(new
				 * BigDecimal(rs1.getDouble("driverId")) .longValueExact() +
				 * ""); tm.setFrom(rs1.getString("fromAddr"));
				 * tm.setTo(rs1.getString("toAddr"));
				 * tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				 * tm.setBookingStatus(rs1.getString("bookingStatus"));
				 * tm.setTravellerName(rs1.getString("travellerName"));
				 * tm.setTravellerPhone(rs1.getString("travellerPhone"));
				 * tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));
				 */

			}

			
			// System.out.println("From Arraylist");
			  
			  for(Long bookedTime : timestamps){
			  
			  Calendar calTemp = Calendar.getInstance();
			  calTemp.setTimeInMillis(bookedTime);
			  
			  log.info("Existing Bookings = "+calTemp.get(Calendar.YEAR)+"-"
			  +calTemp.get(Calendar.MONTH)+"-" +calTemp.get(Calendar.DATE)+"-"
			  +calTemp.get(Calendar.HOUR_OF_DAY)+"-"
			  +calTemp.get(Calendar.MINUTE)+"-" +calTemp.get(Calendar.SECOND));
			  }
			 

			long booking_margin_time = ((1000 * 60) * Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME);
			long currentTime = MyUtil.getCalanderFromDateStr(MyUtil.getCurrentDateFormattedString()).getTimeInMillis();//new Date().getTime();
			log.info("currentTime = " + new Date(currentTime));
			log.info("booknigTime = " + new Date(booknigTimeStamp));

			if (booknigTimeStamp > currentTime) {
				for (Long bookedTime : timestamps) {
					/*
					 * System.out.println("aaa bookedTime=" + bookedTime);
					 * System.out.println("aaa booking_margin_time=" +
					 * booking_margin_time);
					 * System.out.println("aaa booknigTimeStamp=" +
					 * booknigTimeStamp); System.out.println(
					 * "aaa booknigTimeStamp+Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME="
					 * + (booknigTimeStamp+booking_margin_time));
					 * System.out.println(
					 * "aaa booknigTimeStamp-Constants.ADMIN_ADV_MANUAL_BOOKING_MIN_TIME="
					 * + (booknigTimeStamp -booking_margin_time));
					 * System.out.println();
					 */

					/*
					 * System.out.println(
					 * "bookedTime -(booknigTimeStamp + booking_margin_time)=" +
					 * ((bookedTime)-(booknigTimeStamp+booking_margin_time)));
					 * System.out.println("bookedTime -(booknigTimeStamp)=" +
					 * (bookedTime-booknigTimeStamp));
					 * 
					 * System.out.println(
					 * "bookedTime -(booknigTimeStamp - booking_margin_time)=" +
					 * ((bookedTime)-(booknigTimeStamp-booking_margin_time)));
					 */

					// System.out.println();

					if (bookedTime > currentTime) {
						if (bookedTime < (booknigTimeStamp + booking_margin_time)
								&& bookedTime > booknigTimeStamp) {
							// System.out.println("bbb");
							/*
							 * for (Long bookedTime2 : timestamps) {
							 * System.out.println("ccc"); if (bookedTime2 >
							 * (booknigTimeStamp - booking_margin_time) &&
							 * bookedTime2 < booknigTimeStamp) {
							 * System.out.println("ddd"); isFree = false; break;
							 * } }
							 */

							existingBooking = bookedTime;
							isFree = false;
							break;

						} else if (bookedTime > (booknigTimeStamp - booking_margin_time)
								&& bookedTime < booknigTimeStamp) {
							// System.out.println("eee");
							/*
							 * for (Long bookedTime2 : timestamps) {
							 * System.out.println("fff"); if (bookedTime2 <
							 * (booknigTimeStamp + booking_margin_time) &&
							 * bookedTime2 > booknigTimeStamp) {
							 * System.out.println("ggg"); isFree = false; break;
							 * }
							 * 
							 * }
							 */

							existingBooking = bookedTime;
							isFree = false;
							break;
						}
					} else {
						/*
						 * System.out.println("bookingId =" +
						 * bookingMap.get(bookedTime) + " is passed.");
						 */
					}
				}
			} else {
				log.info("getBookingsForaDate >> New Booking time has passed");
			}
			log.info("getBookingsForaDate >> The driver status to accept new booking time is="
					+ isFree);
		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return existingBooking;
	}

	public static TravelMaster searchBookingDetailsByBookingId(String bookingId) {

		PreparedStatement query = null;
		TravelMaster tm = null;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_by_bookingId_from_TravelMaster,
					Statement.RETURN_GENERATED_KEYS);
			query.setString(1, bookingId);
			ResultSet rs1 = query.executeQuery();
			if (rs1.next()) {

				tm = new TravelMaster();

				tm.setDbStatus(true);
				tm.setBookingId(new BigDecimal(rs1.getDouble("bookingId"))
						.longValueExact() + "");
				tm.setUserId(new BigDecimal(rs1.getDouble("userId"))
						.longValueExact() + "");
				tm.setDriverId(new BigDecimal(rs1.getDouble("driverId"))
						.longValueExact() + "");
				tm.setFrom(rs1.getString("fromAddr"));
				tm.setTo(rs1.getString("toAddr"));
				tm.setDateTime(rs1.getTimestamp("bookingDateTime"));
				tm.setBookingStatus(rs1.getString("bookingStatus"));
				tm.setTravellerName(rs1.getString("travellerName"));
				tm.setTravellerPhone(rs1.getString("travellerPhone"));
				tm.setBookingStatusCode(rs1.getString("bookingStatusCode"));

				try {

					DriverMaster assignedDriver = getDriverDetails(tm
							.getDriverId());
					if (assignedDriver != null) {
						tm.setDriverPhone(assignedDriver.getPhoneNumber());
						tm.setDriverStatus(assignedDriver.getDriverStatus());
						tm.setDriverName(assignedDriver.getFirstName());
						tm.setDriverCurrAddress(assignedDriver.getCurrAddr());
						tm.setVehicleNo(assignedDriver.getVehicleNo());
					}

				} catch (Exception e) {
					log.info("searchBookingDetailsByPhone >> "
							+ e.getMessage());
				}

			}

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingDetailsByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return tm;
	}

	public static ArrayList<DriverMaster> getAllCompanyDriverDetails() {

		PreparedStatement query = null;
		ArrayList<DriverMaster> companyDriversList = new ArrayList<DriverMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_all_Company_Drivers_from_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = query.executeQuery();
			while (rs.next()) {

				DriverMaster dm = new DriverMaster();
				dm.setDriverId(new BigDecimal(rs.getString("driverId"))
						.longValueExact() + "");
				dm.setAdminId(new BigDecimal(rs.getString("adminId"))
						.longValueExact() + "");
				dm.setCompanyId(new BigDecimal(rs.getString("companyId"))
						.longValueExact() + "");
				dm.setFirstName(rs.getString("firstName"));
				dm.setLastName(rs.getString("lastName"));
				dm.setPhoneNumber(rs.getString("phoneNumber"));
				dm.setMailId(rs.getString("mailId"));
				dm.setAddress(rs.getString("address"));
				dm.setDriverLicense(rs.getString("driverLicense"));
				dm.setPhoneNumber(rs.getString("phoneNumber"));
				dm.setVehicleNo(rs.getString("vehicleNo"));
				dm.setDriverStatus(rs.getString("driverStatus"));
				dm.setCurrAddr(rs.getString("currAddr"));
				dm.setCurrLongt(rs.getString("currLongt"));
				dm.setCurrLat(rs.getString("currLat"));
				dm.setBookingId(rs.getString("bookingId"));
				dm.setDriverCategory(rs.getInt("driverCategory"));				
				dm.setLocationUpdateTime( rs.getTimestamp("locationUpdateTime"));

				companyDriversList.add(dm);

			}

			/*
			 * log.info("searchBookingIdFromCriverMaster" +
			 * " >>Searched All driver details = " + companyDriversList.size());
			 */

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingIdFromCriverMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingIdFromCriverMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return companyDriversList;
	}

	public static ArrayList<DriverMaster> getAllNonCompanyDriverDetails() {
		// log.info("getAllDriverDetails");
		PreparedStatement query = null;
		ArrayList<DriverMaster> nonCompanyDriversList = new ArrayList<DriverMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_all_Non_Company_Drivers_from_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			// query.setString(1, driverId);

			ResultSet rs = query.executeQuery();
			while (rs.next()) {

				DriverMaster dm = new DriverMaster();
				dm.setDriverId(new BigDecimal(rs.getString("driverId"))
						.longValueExact() + "");
				dm.setAdminId(new BigDecimal(rs.getString("adminId"))
						.longValueExact() + "");
				dm.setCompanyId(new BigDecimal(rs.getString("companyId"))
						.longValueExact() + "");
				dm.setFirstName(rs.getString("firstName"));
				dm.setLastName(rs.getString("lastName"));
				dm.setPhoneNumber(rs.getString("phoneNumber"));
				dm.setMailId(rs.getString("mailId"));
				dm.setAddress(rs.getString("address"));
				dm.setDriverLicense(rs.getString("driverLicense"));
				dm.setPhoneNumber(rs.getString("phoneNumber"));
				dm.setVehicleNo(rs.getString("vehicleNo"));
				dm.setDriverStatus(rs.getString("driverStatus"));
				dm.setCurrAddr(rs.getString("currAddr"));
				dm.setCurrLongt(rs.getString("currLongt"));
				dm.setCurrLat(rs.getString("currLat"));
				dm.setBookingId(rs.getString("bookingId"));
				dm.setDriverCategory(rs.getInt("driverCategory"));

				nonCompanyDriversList.add(dm);

			}

			/*
			 * log.info("searchBookingIdFromCriverMaster" +
			 * " >>Searched All driver details = " + nonCompanyDriversList.size());
			 */

		} catch (Exception e) {
			/*
			 * log.info(
			 * "Inside DatabaseManager >> searchBookingIdFromCriverMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingIdFromCriverMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return nonCompanyDriversList;
	}

	public static long searchBookingIdFromDriverMaster(String driverId) {
		// log.info("searchBookingIdFromDriverMaster");
		PreparedStatement query = null;
		long bookingId = 0;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.get_BookingId_by_driverId_from_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, driverId);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				bookingId = new BigDecimal(rs.getDouble("bookingId"))
						.longValueExact();
				/*
				 * log.info("searchBookingIdFromCriverMaster" +
				 * " >>Searched Values into Driver master table bookingId="+
				 * bookingId);
				 */
			}

		} catch (Exception e) {
			log.info("searchBookingIdFromCriverMaster >> Exception occurred "
					+ e.getMessage());
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info(
				 * "Inside DatabaseManager >> searchBookingIdFromCriverMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return new BigDecimal(bookingId).longValueExact();
	}

	public static long searchUserMasterByPhone(String phone) {
		// log.info("searchUserMasterByPhone");
		PreparedStatement query = null;
		long bd = 0;

		boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_usermaster_by_phoneno,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, phone);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				bd = new BigDecimal(rs.getDouble("userId")).longValueExact();
				/*
				 * log.info("searchuserMasterByPhone" +
				 * " >>Searched userId from UserMaster table=" + bd);
				 */
			}

		} catch (Exception e) {
			/*
			 * log.info (
			 * "Inside DatabaseManager >> searchuserMasterByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> searchuserMasterByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return bd;
	}

	public static ArrayList<UserMaster> searchUsersFromUserMaster(String phone,
			String name, String mailid) {
		// log.info("searchUsersFromUserMaster");
		PreparedStatement query = null;
		ArrayList<UserMaster> userMasterArryList = new ArrayList<UserMaster>();
		// long bd = 0;

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.search_users_from_user_master,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, "%" + phone + "%");
			query.setString(2, "%" + name + "%");
			query.setString(3, "%" + mailid + "%");

			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				UserMaster um = new UserMaster();
				um.setUserId(rs.getString("userId"));
				um.setFirstName(rs.getString("firstName"));
				um.setLastName(rs.getString("lastName"));
				um.setPhone(rs.getString("phone"));
				um.setSex(rs.getString("sex"));
				um.setMailId(rs.getString("mailId"));
				um.setAddress(rs.getString("address"));
				um.setMobileOperator(rs.getString("mobileOperator"));

				userMasterArryList.add(um);

			}

			/*log.info("searchUsersFromUserMaster"
					+ " >>Searched users from UserMaster table="
					+ userMasterArryList);*/

		} catch (Exception e) {
			/*
			 * log.info (
			 * "Inside DatabaseManager >> searchUsersFromUserMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> searchUsersFromUserMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return userMasterArryList;
	}

	
	
	public static ArrayList<UserMaster> getAllUsersFromUserMaster() {
		// log.info("getAllUsersFromUserMaster");
		PreparedStatement query = null;
		ArrayList<UserMaster> userMasterArryList = new ArrayList<UserMaster>();

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_all_from_usermaster,
					Statement.RETURN_GENERATED_KEYS);			

			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				UserMaster um = new UserMaster();
				um.setUserId(rs.getString("userId"));
				um.setFirstName(rs.getString("firstName"));
				um.setLastName(rs.getString("lastName"));
				um.setPhone(rs.getString("phone"));
				um.setSex(rs.getString("sex"));
				um.setMailId(rs.getString("mailId"));
				um.setAddress(rs.getString("address"));
				um.setMobileOperator(rs.getString("mobileOperator"));

				userMasterArryList.add(um);

			}

			/*log.info("getAllUsersFromUserMaster"
					+ " >>Selected all users from UserMaster table="
					+ userMasterArryList);*/

		} catch (Exception e) {
			/*
			 * log.info (
			 * "Inside DatabaseManager >> getAllUsersFromUserMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> getAllUsersFromUserMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return userMasterArryList;
	}
	
	
	public static ArrayList<DriverMaster> searchDriversFromDriverMaster(
			String phone, String name, String licNumber) {
		// log.info("searchUsersFromUserMaster");
		PreparedStatement query = null;
		ArrayList<DriverMaster> driverMasterArryList = new ArrayList<DriverMaster>();
		// long bd = 0;

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.search_Drivers_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, "%" + phone + "%");
			query.setString(2, "%" + name + "%");
			query.setString(3, "%" + licNumber + "%");

			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				DriverMaster dm = new DriverMaster();
				dm.setDriverId(rs.getString("driverId"));
				dm.setFirstName(rs.getString("firstName"));
				dm.setLastName(rs.getString("lastName"));
				dm.setPhoneNumber(rs.getString("phoneNumber"));
				dm.setAge(Integer.parseInt(rs.getString("age")));
				dm.setSex(rs.getString("sex"));
				dm.setMailId(rs.getString("mailId"));
				dm.setDriverLicense(rs.getString("driverLicense"));
				dm.setAddress(rs.getString("address"));
				dm.setCurrAddr(rs.getString("currAddr"));
				dm.setDriverStatus(rs.getString("driverStatus"));
				dm.setDriverCategory(rs.getInt("driverCategory"));
				dm.setMobileOperator(rs.getString("mobileOperator"));

				driverMasterArryList.add(dm);

			}

			/*log.info("searchDriversFromDriverMaster"
					+ " >>Searched users from UserMaster table="
					+ driverMasterArryList);*/

		} catch (Exception e) {
			/*
			 * log.info (
			 * "Inside DatabaseManager >> searchDriversFromDriverMaster >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> searchDriversFromDriverMaster >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return driverMasterArryList;
	}

	public static UserMaster getEmailIdByPhone(String phone) {
		// log.info("getEmailIdByPhone");
		PreparedStatement query = null;
		UserMaster um = null;

		// boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(DBQurries.get_emailid_by_phoneno,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, phone);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				um = new UserMaster();
				um.setMailId(rs.getString("mailId"));
				um.setPassword(rs.getString("passwd"));
				/*log.info("getEmailIdByPhone" + " >>Fetched emailId by Phone="
						+ rs.getString("mailId") + ", password = "
						+ rs.getString("passwd"));*/
			}

		} catch (Exception e) {
			/*
			 * log.info (
			 * "Inside DatabaseManager >> getEmailIdByPhone >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> getEmailIdByPhone >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return um;
	}

	public static UserMaster getUserDetailsByUserId(String userId) {
		// log.info("getUserDetailsByUserId");
		PreparedStatement query = null;
		UserMaster um = null;

		// boolean flag = false;
		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(DBQurries.select_user_details,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, userId);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				um = new UserMaster();
				um.setUserId(userId);
				um.setAdminId(rs.getString("adminId"));
				um.setFirstName(rs.getString("firstName"));
				um.setLastName(rs.getString("lastName"));
				um.setPhone(rs.getString("phone"));
				um.setSex(rs.getString("sex"));
				um.setMobileOperator(rs.getString("mobileOperator"));
				um.setMailId(rs.getString("mailId"));
				um.setAddress(rs.getString("address"));
				um.setPassword(rs.getString("passwd"));
			}

		} catch (Exception e) {
			/*
			 * log.info (
			 * "Inside DatabaseManager >> getUserDetailsByUserId >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> getUserDetailsByUserId >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return um;
	}

	public static DriverMaster validateDriver(String phone) {
		// log.info("validateDriver");
		PreparedStatement query = null;
		DriverMaster driverMaster = null;

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.authenticate_Driver_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, phone);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				long driverId = new BigDecimal(rs.getDouble("driverId"))
						.longValueExact();
				if (driverId != 0) {
					driverMaster = new DriverMaster();

					driverMaster.setDriverId(new BigDecimal(rs
							.getString("driverId")).longValueExact() + "");
					driverMaster.setAdminId(new BigDecimal(rs
							.getString("adminId")).longValueExact() + "");
					driverMaster.setCompanyId(new BigDecimal(rs
							.getString("companyId")).longValueExact() + "");
					driverMaster.setFirstName(rs.getString("firstName"));
					driverMaster.setLastName(rs.getString("lastName"));
					driverMaster.setPhoneNumber(rs.getString("phoneNumber"));
					driverMaster.setMailId(rs.getString("mailId"));
					driverMaster.setAddress(rs.getString("address"));
					driverMaster
							.setDriverLicense(rs.getString("driverLicense"));
					driverMaster.setPhoneNumber(rs.getString("phoneNumber"));
					driverMaster.setVehicleNo(rs.getString("vehicleNo"));
					driverMaster.setDriverStatus(rs.getString("driverStatus"));
					driverMaster.setCurrAddr(rs.getString("currAddr"));
					driverMaster.setCurrLongt(rs.getString("currLongt"));
					driverMaster.setCurrLat(rs.getString("currLat"));
					driverMaster.setBookingId(rs.getString("bookingId"));
					driverMaster.setDriverCategory(rs.getInt("driverCategory"));

				}

				//log.info("validateDriver >>Searched Values into user master table");
			} else {
				log.info("validateDriver >> Credentials are invalid.");
			}

		} catch (Exception e) {
			/*
			 * log.info
			 * ("Inside DatabaseManager >> validateDriver >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> validateDriver >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return driverMaster;
	}

	public static DriverMaster getDriverDetails(String driverIdStr) {
		// log.info("getDriverDetails");
		PreparedStatement query = null;
		DriverMaster driverMaster = null;

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(
					DBQurries.select_Driver_in_DriverMaster,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, driverIdStr);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				long driverId = new BigDecimal(rs.getDouble("driverId"))
						.longValueExact();
				if (driverId != 0) {
					driverMaster = new DriverMaster();

					driverMaster.setDriverId(new BigDecimal(rs
							.getString("driverId")).longValueExact() + "");
					driverMaster.setAdminId(new BigDecimal(rs
							.getString("adminId")).longValueExact() + "");
					driverMaster.setCompanyId(new BigDecimal(rs
							.getString("companyId")).longValueExact() + "");
					driverMaster.setFirstName(rs.getString("firstName"));
					driverMaster.setLastName(rs.getString("lastName"));
					driverMaster.setPhoneNumber(rs.getString("phoneNumber"));
					driverMaster.setMailId(rs.getString("mailId"));
					driverMaster.setAddress(rs.getString("address"));
					driverMaster
							.setDriverLicense(rs.getString("driverLicense"));
					driverMaster.setPhoneNumber(rs.getString("phoneNumber"));
					driverMaster.setVehicleNo(rs.getString("vehicleNo"));
					driverMaster.setDriverStatus(rs.getString("driverStatus"));
					driverMaster.setCurrAddr(rs.getString("currAddr"));
					driverMaster.setCurrLongt(rs.getString("currLongt"));
					driverMaster.setCurrLat(rs.getString("currLat"));
					driverMaster.setBookingId(rs.getString("bookingId"));
					driverMaster.setDriverCategory(rs.getInt("driverCategory"));
					driverMaster.setMobileOperator(rs
							.getString("mobileOperator"));

				}

				//log.info("getDriverDetails >>Searched Values into user master table");
			} else {
				//log.info("getDriverDetails >> DriverId is invalid.");
			}

		} catch (Exception e) {
			/*
			 * log.info
			 * ("Inside DatabaseManager >> validateDriver >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info (
				 * "Inside DatabaseManager >> validateDriver >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return driverMaster;
	}

	public static UserMaster validateUser(String phone, String password) {
		// log.info("validateUser");
		PreparedStatement query = null;
		UserMaster um = null;

		conn = DatabaseManager.getConnection();

		try {

			query = conn.prepareStatement(DBQurries.validate_user,
					Statement.RETURN_GENERATED_KEYS);

			query.setString(1, phone);
			query.setString(2, password);

			ResultSet rs = query.executeQuery();
			if (rs.next()) {
				long bd = rs.getBigDecimal("userId").longValueExact();
				String firstName = rs.getString("firstName");
				String role = rs.getString("role");

				/*
				 * log.info("Searched Values=" + bd + ", " + firstName + ", " +
				 * role);
				 */

				if (bd != 0 && role.length() > 1) {
					um = new UserMaster();
					um.setFirstName(firstName);
					um.setUserId(bd + "");

					if (role.equalsIgnoreCase("user")) {
						um.setAuthLevel(1);
					} else if (role.equalsIgnoreCase("admin")) {
						um.setAuthLevel(2);
					}

					log.info("Credentials valid.");

				}

			} else {
				log.info("validateUser >> Credentials are invalid.");
			}

		} catch (Exception e) {
			/*
			 * log.info
			 * ("Inside DatabaseManager >> validateUser >> Exception occurred "
			 * + e.getMessage());
			 */
			e.printStackTrace();

		} finally {

			try {

				if (query != null) {
					query.close();
				}

			} catch (Exception e) {
				/*
				 * log.info
				 * ("Inside DatabaseManager >> validateUser >> Exception occurred "
				 * + e.getMessage());
				 */
				e.printStackTrace();

			}
		}
		return um;
	}

	public static void main(String args[]) {

		/*
		 * ArrayList<DriverMaster> dmList = getAllCompanyDriverDetails();
		 * 
		 * for (DriverMaster dm : dmList) {
		 * System.out.println(dm.getDriverId()); }
		 */

		// System.out.println(getBookingsToRescheduleOnStartUp().size());
		/*
		 * Calendar newBookingTime = Calendar.getInstance();
		 * 
		 * newBookingTime.set(Calendar.DATE, 3);
		 * newBookingTime.set(Calendar.MONTH, 0);
		 * newBookingTime.set(Calendar.YEAR, 2014);
		 * newBookingTime.set(Calendar.HOUR_OF_DAY, 1);
		 * newBookingTime.set(Calendar.MINUTE, 44);
		 * 
		 * long existingBooking = getBookingsForaDate("11", newBookingTime);
		 * if(existingBooking != 0){ System.out.println("Driver free status = "
		 * + new Date(existingBooking)); }else{
		 * System.out.println("Driver is Free"); }
		 */

		updateManualBookingActiveStatus("29");

	}
}
