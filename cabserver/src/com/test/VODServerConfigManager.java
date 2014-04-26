package com.test;

import java.util.ResourceBundle;
public class VODServerConfigManager {

	private static ResourceBundle vodServerProperties = null;
	static {
		if(System.getProperty("os.name").indexOf("Windows") > -1){
			System.out.println("Inside VODServerConfigManager >> Properties file for Windows is picked");
		vodServerProperties = ResourceBundle
				.getBundle("com.resources.VODServerConf_wind");
		}else{
			vodServerProperties = ResourceBundle
			.getBundle("com.resources.VODServerConf");
			System.out.println("Inside VODServerConfigManager >> Properties file for LINUX is picked");
		}
	}

	public static String getPropertyDetails(String propertyName) {
		String value = vodServerProperties.getString(propertyName).trim();		
		return value;
	}

	public static void main(String args[]) {
		System.out.println(getPropertyDetails("vodserverip"));
		System.out.println(getPropertyDetails("vodserverport"));
	}

}
