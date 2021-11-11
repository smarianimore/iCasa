package iCasa.alarm.system.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APIcommunications {
	
	
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
	
	public List<String> getZonesList() throws MalformedURLException, JSONException {
		//URL
		URL url = new URL("http://127.0.0.1:9000/icasa/zones/zones");
		String method = "GET";
		
		
		//Apriamo una connessione e leggiamo
		HttpURLConnection con;
		BufferedReader in;
		//StringBuffer content = new StringBuffer();
		StringBuilder content = new StringBuilder();
		try {
			con = this.getConnection(url);
			
			//Impostiamo il metodo da usare
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}

		//Estraiamo dal contenuto la lista delle zone
		List<String> zones = new ArrayList<String>();
		zones = this.formatZoneOutput(content.toString());
		return zones;
		
		
	}
	
	private List<String> formatZoneOutput(String content) throws JSONException{
		List<String> zones = new ArrayList<String>();
		
		JSONArray array = new JSONArray(content);
		for(int i=0; i < array.length(); i++){  
			JSONObject object = array.getJSONObject(i);
			zones.add(object.getString("id"));
		}
		
		return zones;
	}
}
