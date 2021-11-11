package iCasa.alarm.system.manager;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

public class TestApp {
	public static void main(String[] args) throws MalformedURLException, JSONException {
		
		AlarmSystemManagerImpl m = new AlarmSystemManagerImpl();
		m.managingVariables();

	}
}
