package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.cabserver.util.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonParser3 {

	public static void main(String args[]) {

		try {
			String address = "40.714224,-73.961452";

			Gson gson = new Gson();
			GoogleGeoCodeResponse result = gson.fromJson(
					jsonCoord(URLEncoder.encode(address, "UTF-8")),
					GoogleGeoCodeResponse.class);

			double lat = Double
					.parseDouble(result.results[0].geometry.location.lat);

			double lng = Double
					.parseDouble(result.results[0].geometry.location.lng);

			System.out.println("formatted address = "
					+ result.results[0].formatted_address);

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void recordGPSLocation(String address) {

		try {
			Gson gson = new Gson();
			GoogleGeoCodeResponse result = gson.fromJson(
					jsonCoord(URLEncoder.encode(address, "UTF-8")),
					GoogleGeoCodeResponse.class);

			double lat = Double
					.parseDouble(result.results[0].geometry.location.lat);

			double lng = Double
					.parseDouble(result.results[0].geometry.location.lng);
			
			String formatted_address = result.results[0].formatted_address;

			System.out.println("formatted address = "
					+ formatted_address);
			
			GPSData gd = new GPSData();
			
			gd.setLongt(lng);
			gd.setLat(lat);
			gd.setAddress(formatted_address);
			
			//DatabaseManager.insertGPSData(gd);
			

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String jsonCoord(String address) throws IOException {

		System.getProperties().put("http.proxyHost", "rtecproxy.ril.com");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "shankar.mohanty");
		System.getProperties().put("http.proxyPassword", "cu141#123");
		URL url = new URL(
				"http://"+Constants.GOOGLE_MAPS_API+"/maps/api/geocode/json?latlng="
						+ address + "&sensor=false");
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		String jsonResult = "";
		while ((inputLine = in.readLine()) != null) {
			jsonResult += inputLine;
		}
		in.close();
		return jsonResult;
	}

}
