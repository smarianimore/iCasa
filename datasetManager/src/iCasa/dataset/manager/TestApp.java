package iCasa.dataset.manager;

import java.io.IOException;
import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

public class TestApp {

	public static void main(String[] args) throws JSONException, IOException {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println(timestamp);
		

	}

}
