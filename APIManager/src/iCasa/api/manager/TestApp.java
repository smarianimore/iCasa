package iCasa.api.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import org.json.JSONException;

public class TestApp {

	public static void main(String[] args) throws JSONException, IOException {
		APIManagerImpl m = new APIManagerImpl();
		m.movePerson("Mario", "bedroom");

		System.out.println(m.getPersonsList());
		
	}

}
