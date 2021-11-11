package iCasa.system.manager.dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import iCasa.system.manager.SystemManagerImpl;

public class TestApp {

	public static void main(String[] args) throws JSONException, IOException {
//		JSONObject snapshot = new JSONObject();
//		JSONObject device = new JSONObject();
//		device.put("livingroom", "true");
//		device.put("kitchen", "false");
//		snapshot.put("presenceSensors", device);

		FileWriter csvWriter = new FileWriter("new.csv");

		List<List<String>> rows = Arrays.asList(
			    Arrays.asList("Jean", "author", "Java"),
			    Arrays.asList("David", "editor", "Python"),
			    Arrays.asList("Scott", "editor", "Node.js"));
		
		for (List<String> rowData : rows) {
		    csvWriter.append(String.join(",", rowData));
		    csvWriter.append("\n");
		}

	    csvWriter.flush();
		csvWriter.close();

		
	}

}
