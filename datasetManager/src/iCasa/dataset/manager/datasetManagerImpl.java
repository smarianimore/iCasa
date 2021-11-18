package iCasa.dataset.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import iCasa.dataset.manager.services.datasetManagerServices;

public class datasetManagerImpl implements datasetManagerServices {

	@Override
	public void setHeader(JSONObject snapshot, FileWriter csvWriter, BufferedReader br) throws IOException, JSONException {
		
		//The header is inserted only if the file is empty    
		if (br.readLine() == null) {
			
			List<String> row = this.buildHeader(snapshot);
			
			if (!row.isEmpty()) {
				csvWriter.append(String.join(",", row));
			    csvWriter.append("\n");
			}
		}
	}
	
	@Override
	public List<String> buildHeader(JSONObject snapshot) throws JSONException {

		//Read the value of a row from the snapshot and make them in a list
		List<String> row = new ArrayList<String>();

		for (String deviceName : JSONObject.getNames(snapshot)) {
			JSONObject deviceLocationValues = snapshot.getJSONObject(deviceName);
			for (String location : JSONObject.getNames(deviceLocationValues)) {
				//This check makes us able to distinguish between singular values as the timestamp
				//and multiple values as when we have multiple rooms
				if (JSONObject.getNames(deviceLocationValues).length < 2)
					row.add(deviceName);
				else
					row.add(deviceName + "." + location.substring(0, 3));
			}
		}

		return row;
	}
	
	@Override
	public List<String> buildRow(JSONObject snapshot) throws JSONException {
		
		//Read the value of a row from the snapshot and make them in a list
		List<String> row = new ArrayList<String>();
		
		for (String deviceName : JSONObject.getNames(snapshot)) {
			JSONObject deviceLocationValues = snapshot.getJSONObject(deviceName);
			for (String location : JSONObject.getNames(deviceLocationValues)) {
				row.add((String) deviceLocationValues.get(location).toString());
			}
		}
		
		return row;
	}
	
	@Override
	public void writeRow(List<String> row, FileWriter csvWriter) throws IOException {
		
		if (!row.isEmpty()) {
			csvWriter.append(String.join(",", row));
		    csvWriter.append("\n");
		}

	}
	
	@Override
	public void buildAndWrite(JSONObject snapshot) throws JSONException, IOException {
		//Open the file in append mode for writer and for reader
		FileWriter csvWriter = new FileWriter("dataset.csv", true);
		BufferedReader br = new BufferedReader(new FileReader("dataset.csv")); 
		
		//Set the header if not already present
		this.setHeader(snapshot, csvWriter, br);
		
		//Build and write the row on the dataset
		List<String> row = this.buildRow(snapshot);
		this.writeRow(row, csvWriter);
		
		//Close the pointers
		br.close();
		csvWriter.flush();
		csvWriter.close();
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
