package iCasa.devices.component;

import org.json.JSONException;
import org.json.JSONObject;

public class TestApp {

	public static void main(String[] args) throws JSONException {
		devicesComponentImpl snap = new devicesComponentImpl();
		
		JSONObject snapshot = new JSONObject();
		JSONObject JSONpresenceSensors = new JSONObject();
		
		JSONpresenceSensors.append("livingroom", false);
		JSONpresenceSensors.append("kitchen", true);
		
		snapshot.append("presencesSensor", JSONpresenceSensors);
		System.out.println(snapshot);
		

	}

}
