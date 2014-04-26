package com.test;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonParser2 {

	public static void main(String args[]){
		
		JsonFactory f = new JsonFactory();
		JsonParser jp;
		try {
			jp = f.createJsonParser(new File("D:\\addr.json"));
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		    mapper.configure(DeserializationConfig.Feature.AUTO_DETECT_CREATORS, false);                 
		    JsonNode rootNode = mapper.readTree(jp);
		    JsonNode firstResult = rootNode.get("results").get(0);
		    JsonNode location = firstResult.get("locations").get(0);
		    JsonNode latLng = location.get("latLng");
		    String lat = latLng.get("lat").asText();
		    String lng = latLng.get("lng").asText();
		    String output = lat + "," + lng;
		    System.out.println("Found Coordinates: " + output);
			
			
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
