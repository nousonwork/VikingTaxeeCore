package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class JsonParser1 {
	public static void main(String[] args) {

		try {

			JsonFactory f = new JsonFactory();
			JsonParser jp = f.createJsonParser(new File("D:\\addr.json"));
			// User user = new User();
			jp.nextToken(); // will return JsonToken.START_OBJECT (verify?)
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String fieldname = jp.getCurrentName();
				jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY
				System.out.println("fieldname" + fieldname);
				while (jp.nextToken() != JsonToken.START_ARRAY) {
					String fieldname1 = jp.getCurrentName();
					jp.nextToken();
					
					
				}
				
				
				if ("address_components".equals(fieldname)) { // contains an
																// object
					// Name name = new Name();
					while (jp.nextToken() != JsonToken.END_OBJECT) {
						String namefield = jp.getCurrentName();
						jp.nextToken(); // move to value

						System.out.println("namefield" + namefield);

					}
					// user.setName(name);
				} else if ("formatted_address".equals(fieldname)) {
					System.out.println("formatted_address" + fieldname);
				} else {
					throw new IllegalStateException("Unrecognized field '"
							+ fieldname + "'!");
				}
			}
			jp.close(); // ensure resources get cleaned up timely and properly

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}