package com.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class Logger {
	static BufferedWriter br = null;
	public static boolean status = false;
	public static boolean timer = false;
	public static boolean sop = false;
	public static boolean filelog = false;
	public static String fileId = null;

	static {
		status = Boolean.parseBoolean(VODServerConfigManager
				.getPropertyDetails("loggerstatus"));
		filelog = Boolean.parseBoolean(VODServerConfigManager
				.getPropertyDetails("filelog"));
		timer = Boolean.parseBoolean(VODServerConfigManager
				.getPropertyDetails("timer"));
		sop = Boolean.parseBoolean(VODServerConfigManager
				.getPropertyDetails("sop"));
		System.out.println("Logger ststus  ::  " + status);
		System.out.println("File Logger ststus  ::  " + filelog);
		System.out.println("Timer Logger ststus  ::  " + timer);
		System.out.println("sop Logger ststus  ::  " + sop);
		
	}

	public static void log(String message) {
		if (status) {
			
			try {
				if (sop) {
					System.out.println(message);
				}
				if (filelog) {
					//System.out.println("Printing getFileId()  =  " + getFileId());
					
					br = new BufferedWriter(new FileWriter(VODServerConfigManager
							.getPropertyDetails("loggerpath")
							+ "/ugc_" + getFileId() + ".log", true));
					br.write(message.toCharArray());
					br.newLine();
					if (timer) {
						br.write(" Date = "
								+ String.valueOf(Calendar.getInstance().get(
										Calendar.DATE))
								+ " :: Hour = "
								+ String.valueOf(Calendar.getInstance().get(
										Calendar.HOUR_OF_DAY))
								+ " :: Minute = "
								+ String.valueOf(Calendar.getInstance().get(
										Calendar.MINUTE))
								+ " :: Second = "
								+ String.valueOf(Calendar.getInstance().get(
										Calendar.SECOND)));
					}
					br.newLine();
					br.flush();
					// br.close();
				}
			} catch (IOException e) {
				System.out.println("Exception occurred " + e.getMessage());
			} catch (Exception e) {
				System.out.println("Exception occurred " + e.getMessage());

			}
		} // end if if
	}

	private static String getFileId() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return (sdf.format(new Date().getTime()));

	}

	public static void main(String[] args) {

		log("Hi this is test logbcbcvb");

	}

}
