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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import util.LogUtil;
import util.PropertyUtil;

@RestController
@SpringBootApplication
@RequestMapping("/Weathermap")
public class ExampleController {

	private static final Logger logger = LogManager.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	public static String appid = PropertyUtil.getPropertyValue("APPID");

	private static List<Long> extractIdList = new ArrayList<>();

	@GetMapping("/prova")
	public String firstMessage() {

		return "Greetings from Spring Boot!";
	}

	@GetMapping(value = "/getWheather", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getWheather() {

		LogUtil.logMethodStart();
		long randomId;
		if (!extractIdList.isEmpty()) {

			List<Long> idList = find();
			idList.removeAll(extractIdList);

			// If the List to retrieve data is empty, i invert that whit the extractIdList
			if (idList.size() == 0) {

				List<Long> tempList = new ArrayList<Long>(extractIdList);
				extractIdList.clear();
				idList.addAll(tempList);
				
				// Sets BerlinId for the first run
				randomId = Long.parseLong(PropertyUtil.getPropertyValue("BerlinId"));
				extractIdList.add(randomId);
			} else {
				randomId = getRandomId(idList);
			}

		} else {
			// Sets BerlinId for the first run
			randomId = Long.parseLong(PropertyUtil.getPropertyValue("BerlinId"));
			extractIdList.add(randomId);
		}

		

		JSONObject json = null;

		try {
			URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?id=" + randomId + "&APPID=" + appid);

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
			conn.disconnect();
		} catch (IOException | ParseException e) {
			logger.error("Error :" + e);
		}
		LogUtil.logMethodEnd();
		return json;
	}

	public static List<Long> find() {
		LogUtil.logMethodStart();
		List<Long> idList = new ArrayList<>();

		try {

			String cityListString = new String(
					Files.readAllBytes(Paths.get(".\\src\\main\\resources\\city.list.json")), "UTF-8");
			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(cityListString);

			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				idList.add((long) jsonObject.get("id"));
			}

		} catch (IOException | ParseException e) {
			logger.error("Error :" + e);
		}
		LogUtil.logMethodEnd();
		return idList;
	}

	public Long getRandomId(List<Long> idList) {
		LogUtil.logMethodStart();

		int index = ThreadLocalRandom.current().nextInt(idList.size());
		logger.info("\nIndex :" + index);
		LogUtil.logMethodEnd();

		return idList.get(index);

	}

	public static void main(String args[]) {
		SpringApplication.run(ExampleController.class, args);
	}

}
