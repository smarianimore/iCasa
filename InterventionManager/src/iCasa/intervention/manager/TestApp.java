package iCasa.intervention.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestApp {
	
	public static void main (String[] args) throws JSONException {
		
		String evidence = "{'H': 0}";
		
		JSONObject ev = new JSONObject(evidence);
		System.out.println("Evidence " + ev);
		
		String node = JSONObject.getNames(ev)[0];
		int value = (int) ev.get(node);
		
//		for (String deviceName : JSONObject.getNames(ev)) {
//			String node = deviceName;
//			System.out.println(node);
//			
//			int value = (int) ev.get(deviceName);
//			System.out.println(value);
//		}
		
		//JSONArray ev = new JSONArray(evidence);
		//System.out.println("Evidence " + ev);
		//String node = (String) ev.get(0);
		//System.out.println("Node " + node);
		//int value = (int) ev.get(1);
		//System.out.println("Value " + value);
		
	}
}
