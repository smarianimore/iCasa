package iCasa.intervention.manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Random;

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

public class InterventionManagerImpl {

	/** Field for button dependency */
	private PushButton button;
	/** Field for binaryLight dependency */
	private BinaryLight binaryLight;
	/** Field for cooler dependency */
	private Cooler cooler;
	/** Field for heater dependency */
	private Heater heater;
	/** Field for alarm dependency */
	private Siren alarm;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for window dependency */
	private DoorWindowSensor window;
	/** Field for switcher dependency */
	private PowerSwitch switcher;
	/** Field for presenceSensor dependency */
	private PresenceSensor presenceSensor;
	/** Field for CO dependency */
	private CarbonMonoxydeSensor CO;
	/** Field for CO2 dependency */
	private CarbonDioxydeSensor CO2;

	ServerSocket server;
	
	// The method initialize the devices (could probably increase the sense or real of the scenario)
	public void randomInitialization() {
		Random r = new Random();
		
		//INDOOR TEMPERATURE THRESHOLD
		double rangeMinIndoorT = 273.15;
		double rangeMaxIndoorT = 303.15;
		double rndIndoorT = rangeMinIndoorT + (rangeMaxIndoorT - rangeMinIndoorT) * r.nextDouble();

		//OUTDOOR TEMPERATURE THRESHOLD
		 double rangeMinOutdoorT = 273.15;
		 double rangeMaxOutdoorT = 303.15;
		 double rndOutdoorT = rangeMinOutdoorT + (rangeMaxOutdoorT - rangeMinOutdoorT) * r.nextDouble();
		
		for (Thermometer device : thermometers) {
			String location = (String) device.getPropertyValue("Location");
			if (location.equals("room")) {
				device.setPropertyValue("thermometer.currentTemperature", (double) rndIndoorT);
			}
			if (location.equals("outdoor")) {
				device.setPropertyValue("thermometer.currentTemperature", (double) rndOutdoorT);
			}
		}

		//HEATER LEVEL
		double rangeMinHeaterLeavel = 0;
		double rangeMaxHeaterLeavel = 1000;
		double rndHeaterLevel = rangeMinHeaterLeavel + (rangeMaxHeaterLeavel - rangeMinHeaterLeavel) * r.nextDouble();
		heater.setPropertyValue("heater.powerLevel", (double) rndHeaterLevel); 

		//COOLER LEVEL
		double rangeMinCoolerLeavel = 0;
		double rangeMaxCoolerLeavel = 1000;
		double rndCoolerLevel = rangeMinCoolerLeavel + (rangeMaxCoolerLeavel - rangeMinCoolerLeavel) * r.nextDouble();
		cooler.setPropertyValue("cooler.powerLevel", (double) rndCoolerLevel); 

		//WINDOW OPENED/CLOSED
		boolean opening = r.nextBoolean();
		window.setPropertyValue("doorWindowSensor.opneningDetection", opening);

		//BUTTON
		boolean btn = r.nextBoolean();
		button.setPropertyValue("pushButton.pushAndHold", btn);

		//SWITCHER
		boolean s = r.nextBoolean();
		switcher.setPropertyValue("powerSwitch.currentStatus", s);
	}
	
	// This method induces the causality relationships modeling the behaviors of the network nodes
	public void induceCausality(String node, int value) {
		switch (node) {
		case "L":
			break;
		case "S":
			// Modeling causality S->H and S->C: each device takes the opposite actual value
			double h = (double) heater.getPropertyValue("heater.powerLevel");
			double c = (double) cooler.getPropertyValue("cooler.powerLevel");
			
			if (h == 0.0)
				heater.setPropertyValue("heater.powerLevel", (double) 1000.0);
			else
				heater.setPropertyValue("heater.powerLevel", (double) 0.0);
			
			if (c == 0.0)
				cooler.setPropertyValue("cooler.powerLevel", (double) 1000.0);
			else
				cooler.setPropertyValue("cooler.powerLevel", (double) 0.0);

			break;
		case "H":
//			for (Thermometer device : thermometers) {
//				String location = (String) device.getPropertyValue("Location");
//				if (location.equals("room"))
//					if (value == 1)
//						device.setPropertyValue("thermometer.currentTemperature", (double) device.getPropertyValue("thermometer.currentTemperature") * 2);
//					else
//						device.setPropertyValue("thermometer.currentTemperature", (double) 0.0);
//			}
			break;
		case "C":
//			for (Thermometer device : thermometers) {
//				String location = (String) device.getPropertyValue("Location");
//				if (location.equals("room"))
//					if (value == 1)
//						device.setPropertyValue("thermometer.currentTemperature", (double) device.getPropertyValue("thermometer.currentTemperature") * 2);
//					else
//						device.setPropertyValue("thermometer.currentTemperature", (double) 0.0);
//			}
			break;
		case "A":
			// Modeling causality A->W
			if (value == 1)
				window.setPropertyValue("doorWindowSensor.opneningDetection", true);
			else
				window.setPropertyValue("doorWindowSensor.opneningDetection", false);
			break;
		case "B":
			// Modeling causality B->W
			if (value == 1)
				window.setPropertyValue("doorWindowSensor.opneningDetection", true);
			else
				window.setPropertyValue("doorWindowSensor.opneningDetection", false);
			break;
		case "W":
			// Modeling causality W->T
			double currentValueInt = 0.0;
			double currentValueExt = 0.0;
			Thermometer in = null;
			for (Thermometer device : thermometers) {
				String location = (String) device.getPropertyValue("Location");
				if (location.equals("room")) {
					currentValueInt = (double) device.getPropertyValue("thermometer.currentTemperature");
					in = device;
				}
				else if (location.equals("outdoor")) {
					currentValueExt = (double) device.getPropertyValue("thermometer.currentTemperature");
				}
			}
			// Simuliamo il cambiamento di temperatura dovuto alla temperatura esterna ed all'apertura/chiusura della finestra
			// Windows is open
			if (value == 1) {
				System.out.println("Current temperatures: " + currentValueExt + " " + currentValueInt);

				// Test: simulate a real change in external temperature
//				Random r = new Random();
//				double rangeMinOutdoorT = 273.15;
//				double rangeMaxOutdoorT = 303.15;
//				double rndOutdoorT = rangeMinOutdoorT + (rangeMaxOutdoorT - rangeMinOutdoorT) * r.nextDouble();
//				currentValueExt = rndOutdoorT;
//				System.out.println("Current temperatures: " + "Internal=" + currentValueInt + "External=" + currentValueExt);
				
				if (currentValueExt < currentValueInt)
					in.setPropertyValue("thermometer.currentTemperature", (double) 273.15); // minimum value
				// This case should be added in a real scenario, where the external temperature changes during the time
				// but in our simulation it remains always constant
				else if (currentValueExt == currentValueInt)
					in.setPropertyValue("thermometer.currentTemperature", (double) currentValueInt); //no action
				else
					in.setPropertyValue("thermometer.currentTemperature", (double) 303.15); // maximum value
			}
			break;
		default:
			break;
		}
	}

	public void intervention(String evidence) throws JSONException {
		// Format of evidence = {'H': 1}

		JSONObject ev = new JSONObject(evidence);
		System.out.println("Evidence " + ev);

		String node = JSONObject.getNames(ev)[0];
		int value = (int) ev.get(node);

		switch (node) {
		case "L":
			binaryLight.setPropertyValue("binaryLight.powerStatus", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			induceCausality(node, value);
			break;
		case "S":
			switcher.setPropertyValue("powerSwitch.currentStatus", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			induceCausality(node, value);
			break;
		case "H":
			heater.setPropertyValue("heater.powerLevel", value == 0 ? 0.0 : 1000.0);
			System.out.println(value);
			induceCausality(node, value);
			break;
		case "C":
			cooler.setPropertyValue("cooler.powerLevel", value == 0 ? 0.0 : 1000.0);
			System.out.println(value);
			induceCausality(node, value);
			break;
		case "A":
			alarm.setPropertyValue("siren.status", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			induceCausality(node, value);
			break;
		case "B":
			button.setPropertyValue("pushButton.pushAndHold", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			induceCausality(node, value);
			break;
		case "W":
			window.setPropertyValue("doorWindowSensor.opneningDetection", value == 0 ? false : true);
			System.out.println(value == 0 ? false : true);
			induceCausality(node, value);
			break;
		default:
			break;
		}

	}

	public void doing() {
		String fromClient;

		try {
			while (true) {
				Socket client = server.accept();
				System.out.println("Client connected");
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

				fromClient = in.readLine();
				System.out.println("received: " + fromClient);
				
				intervention(fromClient);

			}
		} catch (IOException | JSONException e) {
			System.out.println("Intervention process failed!");
		}
	}

	/** Component Lifecycle Method 
	 * @throws IOException */
	public void stop() {
		try {
			server.close();
			System.out.println("Server closed");
		} catch (IOException e) {
			System.out.println("Error on server close");
			e.printStackTrace();
		}
	}

	/** Component Lifecycle Method 
	 * @throws IOException */
	public void start() {
		try {
			server = new ServerSocket(7777);
			System.out.println("Server started");
		} catch (IOException e) {
			System.out.println("Error on server start");
			e.printStackTrace();
		}
		
		doing();
	}

	/** Bind Method for thermometers dependency */
	public void bindThermometers(Thermometer thermometer, Map properties) {
		randomInitialization();
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometers(Thermometer thermometer, Map properties) {
	}

}
