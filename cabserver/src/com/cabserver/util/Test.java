package com.cabserver.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Test {

	public static void main(String[] args) {

		System.out.println(getCalanderFromDateStr(getDateFormattedString()).getTime());

	}

	public static String getDateFormattedString() {

		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
		formatter.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

		return formatter.format(new Date());
	}

	public static Calendar getCalanderFromDateStr(String formattedTimeStr) {

		Calendar cal = Calendar.getInstance();
		System.out.println("FormattedTimeStr = " + formattedTimeStr);

		String timeArry[] = formattedTimeStr.split(" ");

		// System.out.println("date = "+ timeArry[0]);

		String dateArry[] = timeArry[0].split("/");
		cal.set(cal.YEAR, Integer.parseInt(dateArry[0]));
		cal.set(cal.MONTH, Integer.parseInt(dateArry[1]) - 1);
		cal.set(cal.DATE, Integer.parseInt(dateArry[2]));

		String timeValueArry[] = timeArry[1].split(":");
		cal.set(cal.HOUR, Integer.parseInt(timeValueArry[0]));
		cal.set(cal.MINUTE, Integer.parseInt(timeValueArry[1]));
		cal.set(cal.SECOND, Integer.parseInt(timeValueArry[2]));

		if (timeArry[2].equalsIgnoreCase("AM")) {
			cal.set(cal.AM_PM, cal.AM);
		} else {
			cal.set(cal.AM_PM, cal.PM);
		}

		return cal;
	}

	public static void main_2(String[] args) {

		/*
		 * JSONObject obj1 = new JSONObject(); JSONObject obj2 = new
		 * JSONObject(); JSONArray arr = new JSONArray();
		 * 
		 * obj1.put("code", "200"); obj1.put("msg", "Login Succesful.");
		 * 
		 * obj2.put("code", "200"); obj2.put("msg", "Login Succesful.");
		 * 
		 * arr.add(obj1); arr.add(obj2);
		 * 
		 * System.out.println("Formed JSON = " + arr.toJSONString());
		 */

		// Random randomGenerator = new Random();
		// for (int idx = 1; idx <= ; ++idx){
		int randomInt = new Random().nextInt(100000);
		System.out.println("Generated : " + randomInt);
		// }

	}

	public static void main_1(String[] args) {
		String gpsData = "";
		try {
			FileInputStream fis = new FileInputStream("E:/CabGuru/gpsdata.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			ArrayList<String> longtList = new ArrayList<String>();
			ArrayList<String> latList = new ArrayList<String>();

			String str = null;
			while ((str = br.readLine()) != null) {

				String tmpArry[] = str.split(",");
				longtList.add(tmpArry[1]);
				latList.add(tmpArry[2]);
			}

			// System.out.println(longtList.toString());
			// System.out.println(latList.toString());

			gpsData = "[\n";

			for (int i = 0; i < latList.size(); i++) {

				if (i == (latList.size() - 1)) {
					gpsData += "['Taxi ID " + (i + 1) + "'," + latList.get(i)
							+ "," + longtList.get(i) + "," + (i + 1) + "]\n]";
				} else {
					gpsData += "['Taxi ID " + (i + 1) + "'," + latList.get(i)
							+ "," + longtList.get(i) + "," + (i + 1) + "],\n";
				}

			}

			System.out.println(gpsData);
			// out.println(gpsData);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return gpsData;

	}

	public static String getGPSData() {
		String gpsData = "";
		try {
			FileInputStream fis = new FileInputStream("E:/CabGuru/gpsdata.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			ArrayList<String> longtList = new ArrayList<String>();
			ArrayList<String> latList = new ArrayList<String>();

			String str = null;
			while ((str = br.readLine()) != null) {

				String tmpArry[] = str.split(",");
				longtList.add(tmpArry[1]);
				latList.add(tmpArry[2]);
			}

			// System.out.println(longtList.toString());
			// System.out.println(latList.toString());

			gpsData = "[\n";

			for (int i = 0; i < latList.size(); i++) {

				if (i == (latList.size() - 1)) {
					gpsData += "['Taxi ID " + (i + 1) + "'," + latList.get(i)
							+ "," + longtList.get(i) + "," + (i + 1) + "]\n]";
				} else {
					gpsData += "['Taxi ID " + (i + 1) + "'," + latList.get(i)
							+ "," + longtList.get(i) + "," + (i + 1) + "],\n";
				}

			}

			System.out.println("This is >> Test >> getGPSData");
			System.out.println(gpsData);
			// out.println(gpsData);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gpsData;
	}

}
