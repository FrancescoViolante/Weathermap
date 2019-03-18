package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import util.PropertyUtil;


@RestController
@SpringBootApplication
@RequestMapping("/Weathermap")
public class ExampleController {
	
	public static String appid= PropertyUtil.getPropertyValue("APPID");
	
	@RequestMapping("/getVal/{id}")
	public String firstMessage(@PathVariable("cityName") String cityName) {
		System.out.println(cityName);
		System.out.println(PropertyUtil.getPropertyValue("prova"));
		return "Greetings from Spring Boot!";
	}

	@GetMapping(value = "/getWheather", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getWheather(@RequestBody List<Long> idUsedRb) {
		List<Long> idList= find();		
		
		JSONObject json = null;
		
		//Sets BerlinId for the first run
		long randomId= Long.parseLong(PropertyUtil.getPropertyValue("BerlinId"));
		
		if(idUsedRb.size()!=0) {
			idList.removeAll(idUsedRb);
			randomId= getRandomId(idList);
		}
		
		
		try {
			URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?id="+randomId+"&APPID="+appid);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String message = IOUtils.toString(br);

			JSONParser parser = new JSONParser();
			json = (JSONObject) parser.parse(message);
//			System.out.println(json);
			conn.disconnect();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public static List<Long> find() {
		
		List<Long> idList=new ArrayList<>(); 
	   
	    try {     

	        String cityListString = new String(Files.readAllBytes(Paths.get("C:\\Users\\fr_vi\\eclipse-workspace-try\\city.list.json")), "UTF-8");    
	        JSONParser parser = new JSONParser();
			JSONArray jsonArray =  (JSONArray) parser.parse(cityListString);			
			
			for(int i = 0; i< jsonArray.size(); i++)
			{			     
				JSONObject jsonObject= (JSONObject) jsonArray.get(i);
				idList.add((long) jsonObject.get("id"));
			}			
	
	    }
	    catch(IOException | ParseException e) {
	        e.printStackTrace();      
	    }

	    return idList;
	}
	
	public Long getRandomId(List<Long> idList) {

	    int index = ThreadLocalRandom.current().nextInt(idList.size());		
	    System.out.println("\nIndex :" + index );
	    return idList.get(index);
	    
	}
	
	public static void main(String args[]) {
		SpringApplication.run(ExampleController.class, args);
	}

}
