package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "UrNkFRmweDbYdSjMDauOny8LfHAQfHLs";
	public JSONArray search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // Rick Sun => Rick20%Sun
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 9);
		
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=50", API_KEY, geoHash, keyword);

	    
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			connection.setRequestMethod("GET");
			
			int responseCode = connection.getResponseCode();
			System.out.println("Sending 'GET' request to URL: " + URL);
			System.out.println("Response Code: " + responseCode);
		
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject obj = new JSONObject(response.toString());
			if (!obj.isNull("_embedded")) {
				JSONObject embbeded = obj.getJSONObject("_embedded");
				return embbeded.getJSONArray("events");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();		
              }
	
	private void queryAPI(double lat, double lon) {
		List<Item> itemList = search (lat,lon,null);
		try {
			for(Item item : itemList) {
				JSONObject jsonObject = item.toJSONObject();
				System.out.println(jsonObject);
			}
		} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public static void main(String[] args) {
			TicketMasterAPI tmApi = new TicketMasterAPI();
			// Mountain View, CA
			// tmApi.queryAPI(37.38, -122.08);
			// London, UK
			// tmApi.queryAPI(51.503364, -0.12);
			// Houston, TX
			tmApi.queryAPI(29.682684, -95.295410);


		}
	}

