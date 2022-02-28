package iCasa.intervention.manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Map;

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
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for window dependency */
	private DoorWindowSensor window;
	/** Field for switcher dependency */
	private PowerSwitch switcher;
	/** Field for CO dependency */
	private CarbonMonoxydeSensor CO;

	ServerSocket server;
	
	// This method induces the causality relationships modeling the behaviors of the network nodes
	public void induceCausality(String node, int value) {
		switch (node) {
		case "L":
			break;
		case "S":
			// Modeling causality S->H and S->C
			if (value == 1) {
				cooler.setPropertyValue("cooler.powerLevel", (double) 0.0);
				heater.setPropertyValue("heater.powerLevel", (double) 1000.0);
			} else {
				cooler.setPropertyValue("cooler.powerLevel", (double) 1000.0);
				heater.setPropertyValue("heater.powerLevel", (double) 0.0);
			}
			break;
		case "H":
			for (Thermometer device : thermometers) {
				String location = (String) device.getPropertyValue("Location");
				if (location.equals("room"))
					if (value == 1)
						device.setPropertyValue("thermometer.currentTemperature", (double) device.getPropertyValue("thermometer.currentTemperature") * 2);
					else
						device.setPropertyValue("thermometer.currentTemperature", (double) 0.0);
			}
			break;
		case "C":
			for (Thermometer device : thermometers) {
				String location = (String) device.getPropertyValue("Location");
				if (location.equals("room"))
					if (value == 1)
						device.setPropertyValue("thermometer.currentTemperature", (double) device.getPropertyValue("thermometer.currentTemperature") * 2);
					else
						device.setPropertyValue("thermometer.currentTemperature", (double) 0.0);
			}
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
			double currentValue = 0.0;
			for (Thermometer device : thermometers) {
				String location = (String) device.getPropertyValue("Location");
				if (location.equals("room"))
					currentValue = (double) device.getPropertyValue("thermometer.currentTemperature");
					// In questo caso l'obiettivo non è tanto indurre dei valori reali, ma indurre la causalità
					// quindi anche forzando a valori più significativi
					// Probabilmente funzionerebbe anche semplicemente modificare la temperatura in modo casuale
					// quando l'intervention viene fatta su W, senza badare nemmeno se true o false
					
					// Simula un cambio di temperatura repentino con l'apertura o la chiusura della finestra
					if (value == 1)
						device.setPropertyValue("thermometer.currentTemperature", (double) 0.0);
					else
						device.setPropertyValue("thermometer.currentTemperature", (double) 300.0);
			}
			break;
		default:
			break;
		}
	}

	public void intervention(String evidence) throws JSONException {
		// Format of evidence = {'H': 280}
		// The conversion logic of incoming values is left to Python

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
		// TODO: Add your implementation code here
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometers(Thermometer thermometer, Map properties) {
		// TODO: Add your implementation code here
	}

}
