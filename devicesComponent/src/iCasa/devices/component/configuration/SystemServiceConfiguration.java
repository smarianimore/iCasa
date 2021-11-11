package iCasa.devices.component.configuration;

import org.json.JSONException;
import org.json.JSONObject;

public interface SystemServiceConfiguration {
	/*
	 * Takes the snapshot of the system
	 * */
	public JSONObject takeSnapshot() throws JSONException;
}
