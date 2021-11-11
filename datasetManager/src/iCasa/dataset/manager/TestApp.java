package iCasa.dataset.manager;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class TestApp {

	public static void main(String[] args) throws JSONException, IOException {

		JSONObject obj = new JSONObject();
		obj.put("livingroom", "true");
		obj.put("kitchen", "true");
		obj.put("bathroom", "false");
		
		JSONObject snapshot = new JSONObject();
		snapshot.put("presenceSensors", obj);
		
		System.out.println(snapshot);
		
		datasetManagerImpl m = new datasetManagerImpl();
		m.buildAndWrite(snapshot);
		
		

	}

}
