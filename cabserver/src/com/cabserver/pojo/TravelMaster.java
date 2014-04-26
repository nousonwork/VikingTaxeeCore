package com.cabserver.pojo;

import java.util.Date;

import org.quartz.Scheduler;
import org.quartz.TriggerKey;

public class TravelMaster implements Cloneable {

	

	String userId;
	String driverId = "0";	
	String totalDistanceTravelled;
	String from;
	String to;
	String fromLongt;
	String toLongt;
	String fromLat;
	String toLat;
	Date bookingDateTime;
	String bookingStatus;
	String feedBack;
	String travellerPhone;
	String travellerName;
	String driverName;
	String driverPhone;
	String vehicleNo;
	String driverCurrAddress;
	String driverStatus;
	boolean dbStatus;
	String bookingId ="0";
	String bookingStatusCode;
	boolean isBefore;	
	Scheduler scheduler;
	TriggerKey triggerKey;
	int noOfPassengers;
	String mobileOperator;
	String 	airline;
	String flightNumber;
	int bookingType = 0;
	int activationStatus = 0;
	String customerMailId;
	String driverMailId;
	
	String fromMailId;
	String toMailId;
	String subject;
	String fare;
	int mailType;
	String mailText;
	
	
	
	
	
	



	public static TravelMaster getInstance(TravelMaster tm){
		
		TravelMaster tmNew = new TravelMaster();
		tmNew.setActivationStatus(tm.getActivationStatus());
		tmNew.setAirline(tm.getAirline());
		tmNew.setBefore(tm.isBefore);
		tmNew.setBookingDateTime(tm.getBookingDateTime());
		tmNew.setBookingId(tm.getBookingId());
		tmNew.setBookingStatus(tm.getBookingStatus());
		tmNew.setBookingType(tm.getBookingType());
		tmNew.setCustomerMailId(tm.getCustomerMailId());
		tmNew.setDateTime(tm.getDateTime());
		tmNew.setDbStatus(tm.isDbStatus());
		tmNew.setDriverCurrAddress(tm.getDriverCurrAddress());
		tmNew.setDriverId(tm.getDriverId());
		tmNew.setDriverMailId(tm.getDriverMailId());
		tmNew.setDriverName(tm.getDriverName());
		tmNew.setDriverPhone(tm.getDriverPhone());
		tmNew.setDriverStatus(tm.getDriverStatus());
		tmNew.setFare(tm.getFare());
		tmNew.setFeedBack(tm.getFeedBack());
		tmNew.setFlightNumber(tm.getFlightNumber());
		tmNew.setFrom(tm.getFrom());
		tmNew.setFromLat(tm.getFromLat());
		tmNew.setFromLongt(tm.getFromLongt());
		tmNew.setFromMailId(tm.getFromMailId());
		tmNew.setMailText(tm.getMailText());
		tmNew.setMailType(tm.getMailType());
		tmNew.setMobileOperator(tm.getMobileOperator());
		tmNew.setNoOfPassengers(tm.getNoOfPassengers());
		tmNew.setScheduler(tm.getScheduler());
		tmNew.setSubject(tm.getSubject());
		tmNew.setTo(tm.getTo());
		tmNew.setToLat(tm.getToLat());
		tmNew.setToLongt(tm.getToLongt());
		tmNew.setToMailId(tm.getToMailId());
		tmNew.setTotalDistanceTravelled(tm.getTotalDistanceTravelled());
		tmNew.setTravellerName(tm.getTravellerName());
		tmNew.setTravellerPhone(tm.getTravellerPhone());
		tmNew.setTriggerKey(tm.getTriggerKey());
		tmNew.setUserId(tm.getUserId());
		tmNew.setVehicleNo(tm.getVehicleNo());
		
		
		return tmNew;
	}
	
	
	
	
	
	public String getMailText() {
		return mailText;
	}

	public void setMailText(String mailText) {
		this.mailText = mailText;
	}

	public int getMailType() {
		return mailType;
	}

	public void setMailType(int mailType) {
		this.mailType = mailType;
	}

	public String getFromMailId() {
		return fromMailId;
	}

	public void setFromMailId(String fromMailId) {
		this.fromMailId = fromMailId;
	}

	public String getToMailId() {
		return toMailId;
	}

	public void setToMailId(String toMailId) {
		this.toMailId = toMailId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFare() {
		return fare;
	}

	public void setFare(String fare) {
		this.fare = fare;
	}	
	
	public String getCustomerMailId() {
		return customerMailId;
	}

	public void setCustomerMailId(String customerMailId) {
		this.customerMailId = customerMailId;
	}

	public String getDriverMailId() {
		return driverMailId;
	}

	public void setDriverMailId(String driverMailId) {
		this.driverMailId = driverMailId;
	}
	
	public int getBookingType() {
		return bookingType;
	}

	public void setBookingType(int bookingType) {
		this.bookingType = bookingType;
	}

	public int getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(int activationStatus) {
		this.activationStatus = activationStatus;
	}
	
	public int getNoOfPassengers() {
		return noOfPassengers;
	}

	public void setNoOfPassengers(int noOfPassengers) {
		this.noOfPassengers = noOfPassengers;
	}

	public String getMobileOperator() {
		return mobileOperator;
	}

	public void setMobileOperator(String mobileOperator) {
		this.mobileOperator = mobileOperator;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public TriggerKey getTriggerKey() {
		return triggerKey;
	}

	public void setTriggerKey(TriggerKey triggerKey) {
		this.triggerKey = triggerKey;
	}

	
	public boolean isBefore() {
		return isBefore;
	}

	public void setBefore(boolean isBefore) {
		this.isBefore = isBefore;
	}

	public String getBookingStatusCode() {
		return bookingStatusCode;
	}

	public void setBookingStatusCode(String bookingStatusCode) {
		this.bookingStatusCode = bookingStatusCode;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public boolean isDbStatus() {
		return dbStatus;
	}

	public void setDbStatus(boolean dbStatus) {
		this.dbStatus = dbStatus;
	}

	public Date getBookingDateTime() {
		return bookingDateTime;
	}

	public void setBookingDateTime(Date bookingDateTime) {
		this.bookingDateTime = bookingDateTime;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public String getTravellerPhone() {
		return travellerPhone;
	}

	public void setTravellerPhone(String travellerPhone) {
		this.travellerPhone = travellerPhone;
	}
	
	public String getDriverStatus() {
		return driverStatus;
	}

	public void setDriverStatus(String driverStatus) {
		this.driverStatus = driverStatus;
	}

	public String getTravellerName() {
		return travellerName;
	}

	public void setTravellerName(String travellerName) {
		this.travellerName = travellerName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getDriverCurrAddress() {
		return driverCurrAddress;
	}

	public void setDriverCurrAddress(String driverCurrAddress) {
		this.driverCurrAddress = driverCurrAddress;
	}

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getTotalDistanceTravelled() {
		return totalDistanceTravelled;
	}

	public void setTotalDistanceTravelled(String totalDistanceTravelled) {
		this.totalDistanceTravelled = totalDistanceTravelled;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFromLongt() {
		return fromLongt;
	}

	public void setFromLongt(String fromLongt) {
		this.fromLongt = fromLongt;
	}

	public String getToLongt() {
		return toLongt;
	}

	public void setToLongt(String toLongt) {
		this.toLongt = toLongt;
	}

	public String getFromLat() {
		return fromLat;
	}

	public void setFromLat(String fromLat) {
		this.fromLat = fromLat;
	}

	public String getToLat() {
		return toLat;
	}

	public void setToLat(String toLat) {
		this.toLat = toLat;
	}

	public Date getDateTime() {
		return bookingDateTime;
	}

	public void setDateTime(Date bookingDateTime) {
		this.bookingDateTime = bookingDateTime;
	}	

	public String getFeedBack() {
		return feedBack;
	}

	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

}
