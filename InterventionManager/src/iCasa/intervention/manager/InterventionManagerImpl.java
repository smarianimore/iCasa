package iCasa.intervention.manager;

import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.device.button.PushButton;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.security.Siren;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.gasSensor.CarbonMonoxydeSensor;

public class InterventionManagerImpl implements PeriodicRunnable {

	/** Field for button dependency */
	private PushButton button;
	/** Field for binaryLight dependency */
	private BinaryLight binaryLight;
	/** Field for presenceSensor dependency */
	private PresenceSensor presenceSensor;
	/** Field for cooler dependency */
	private Cooler cooler;
	/** Field for CO2 dependency */
	private CarbonDioxydeSensor CO2;
	/** Field for heater dependency */
	private Heater heater;
	/** Field for alarm dependency */
	private Siren alarm;
	/** Field for thermometer dependency */
	private Thermometer thermometer;
	/** Field for window dependency */
	private DoorWindowSensor window;
	/** Field for switcher dependency */
	private PowerSwitch switcher;
	/** Field for CO dependency */
	private CarbonMonoxydeSensor CO;
	
	ServerSocket server;

	
	public void intervention(String evidence) throws JSONException {
		// evidence = {'H': 0}
		JSONObject ev = new JSONObject(evidence);
		System.out.println("Evidence " + ev);
		
		String node = JSONObject.getNames(ev)[0];
		int value = (int) ev.get(node);
		
		switch(node) {
		case "L":
			binaryLight.setPropertyValue("binaryLight.powerStatus", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			break;
		case "S":
			switcher.setPropertyValue("powerSwitch.currentStatus", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			break;
		case "H":
			heater.setPropertyValue("heater.powerLevel", (double) value == 0 ? 250.0 : 750.0);
			System.out.println(value == 0 ? 250 : 750);
			break;
		case "C":
			cooler.setPropertyValue("cooler.powerLevel", (double) value == 0 ? 250.0 : 750.0);
			System.out.println(value == 0 ? 250 : 750);
			break;
		case "A":
			alarm.setPropertyValue("siren.status", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			break;
		case "B":
			button.setPropertyValue("pushButton.pushAndHold", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			break;
		case "W":
			window.setPropertyValue("doorWindowSensor.opneningDetection", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			break;
		}
		
		
	}
	
	public void doing() {
		String fromClient;
		
		try {
			boolean run = true;
	        while(run) {
	        	Socket client = server.accept();
		        System.out.println("got connection on port 8080");
		        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        
		        fromClient = in.readLine();
		        System.out.println("received: " + fromClient);
		        
		        intervention(fromClient);
		        
	        }
		} catch (IOException | JSONException e) {
			
		}
	}
	
	// Da provare non nel metodo run
	@Override
	public void run() {
//		String fromClient;
//        
//        // ServerSocket server;
//		try {
//			// server = new ServerSocket(8080);
//			// System.out.println("Socket opened on port 8080");
//			
//			boolean run = true;
//	        while(run) {
//	        	Socket client = server.accept();
//		        System.out.println("got connection on port 8080");
//		        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//		        
//		        fromClient = in.readLine();
//		        System.out.println("received: " + fromClient);
//		        
//		        intervention(fromClient);
//		        
//		        //if (!fromClient.equals(null)) {
//		        // 	intervention(fromClient);
//		        //}
//		        
//		        run = false;
//		        client.close();
//	            System.out.println("socket closed");
//	        }
//		} catch (IOException | JSONException e) {
//			
//		}
        
	}

	@Override
	public long getPeriod() {
		return 1;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.HOURS;
	}

	/** Component Lifecycle Method 
	 * @throws IOException */
	public void stop() throws IOException {
		server.close();
	}

	/** Component Lifecycle Method 
	 * @throws IOException */
	public void start() throws IOException {
		server = new ServerSocket(8080);
		// Not call doing if using runnable interface
		doing();
	}

}
