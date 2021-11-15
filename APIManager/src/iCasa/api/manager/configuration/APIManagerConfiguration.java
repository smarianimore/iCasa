package iCasa.api.manager.configuration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.json.JSONException;

public interface APIManagerConfiguration {
	/*
	 * Move the person to a room
	 * */
	public void movePerson(String personID, String roomID) throws MalformedURLException;
	
	/*
	 * Get the list of persons present in the flat
	 * */
	public List<String> getPersonsList() throws MalformedURLException, JSONException;
	
	/*
	 * Get the list of zones present in the simulation
	 * */
	public List<String> getZonesList() throws MalformedURLException, JSONException;
}
