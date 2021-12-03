package iCasa.api.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import iCasa.api.manager.configuration.APIManagerConfiguration;

public class APIManagerImpl implements APIManagerConfiguration {
	
	public static final String ROOT = "http://localhost:9000";
	public static final String PERSON_LIST_URL = ROOT + "/icasa/persons/persons";
	public static final String PERSON_UPDATE_URL = ROOT + "/icasa/persons/person/";
	public static final String ZONE_LIST_URL = ROOT + "/icasa/zones/zones";
	
	private HttpURLConnection getConnection(URL url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		return con;
	}
	
	private HttpURLConnection closeConnection(HttpURLConnection con) {
		con.disconnect();
		return con;
	}
	
	private void setMethod(HttpURLConnection con, String method) throws ProtocolException {
		con.setRequestMethod(method);
	}
	
	@Override
	public void movePerson(String personID, String roomID) throws MalformedURLException{
		URL url = new URL((PERSON_UPDATE_URL + personID).toString());
		String method = "PUT";
		
		String payload = "{\"location\": \"" + roomID + "\"}";
		
		//Open the connection and make the request
		HttpURLConnection con;
		try {
			con = this.getConnection(url);

			//Set the method to be used
			this.setMethod(con, method);
			
			//We need to initialize the header in order to make a put with JSON
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Type", "application/json");

			byte[] out = payload.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = con.getOutputStream();
			stream.write(out);

			System.out.println(con.getResponseCode() + " " + con.getResponseMessage());
			con.disconnect();

		} catch (IOException e1) {
			System.out.println("Error on reading the list of persons");
			e1.printStackTrace();
		}
		
	}
	
	@Override
	public List<String> getPersonsList() throws JSONException, MalformedURLException{
		URL url = new URL(PERSON_LIST_URL);
		String method = "GET";
		
		//Open the connection and make the request
		HttpURLConnection con;
		BufferedReader in;
		StringBuilder content = new StringBuilder();
		try {
			con = this.getConnection(url);

			//Set the method to be used
			this.setMethod(con, method);

			if (method == "GET") {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}

				in.close();

				this.closeConnection(con);
			}

		} catch (IOException e1) {
			System.out.println("Error on getting the list of persons");
			e1.printStackTrace();
		}

		//Obtain the list of persons from the response
		List<String> persons = new ArrayList<String>();
		persons = this.formatListOutput(content.toString());
		
		return persons;
		
	}
	
	@Override
	public List<String> getZonesList() throws MalformedURLException, JSONException {
		//URL
		URL url = new URL(ZONE_LIST_URL);
		String method = "GET";
		
		
		//Open the connection and read
		HttpURLConnection con;
		BufferedReader in;
		StringBuilder content = new StringBuilder();
		try {
			con = this.getConnection(url);
			this.setMethod(con, method);
			
			if (method == "GET") {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				
				in.close();
				
				this.closeConnection(con);
			}
				 
			} catch (IOException e1) {
			System.out.println("Error on getting the list of zones");
			e1.printStackTrace();
			}

		//Estraiamo dal contenuto la lista delle zone
		List<String> zones = new ArrayList<String>();
		zones = this.formatListOutput(content.toString());
		return zones;
		
		
	}
	
	/* Format the output */
	private List<String> formatListOutput(String content) throws JSONException{
		List<String> items = new ArrayList<String>();
		
		JSONArray array = new JSONArray(content);
		for(int i=0; i < array.length(); i++){  
			JSONObject object = array.getJSONObject(i);
			items.add(object.getString("id"));
		}
		
		return items;
	}
	
	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
	}
}
