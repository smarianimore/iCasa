package iCasa.devices.component;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import iCasa.devices.component.configuration.SystemServiceConfiguration;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;
import fr.liglab.adele.icasa.device.PowerObservable;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class devicesComponentImpl implements SystemServiceConfiguration {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for windows dependency */
	private DoorWindowSensor[] windows;
	/** Field for powerConsumption dependency */
	private PowerObservable powerConsumption;
	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;

	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
	}

	/** Bind Method for presenceSensors dependency */
	public void bindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for presenceSensors dependency */
	public void unbindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Bind Method for windows dependency */
	public void bindWindows(DoorWindowSensor doorWindowSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for windows dependency */
	public void unbindWindows(DoorWindowSensor doorWindowSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLights(BinaryLight binaryLight, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLights(BinaryLight binaryLight, Map properties) {
		// TODO: Add your implementation code here
	}
	
	/** Bind Method for heaters dependency */
	public void bindHeaters(Heater heater, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for heaters dependency */
	public void unbindHeaters(Heater heater, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Bind Method for thermometers dependency */
	public void bindThermometers(Thermometer thermometer, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometers(Thermometer thermometer, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Take the snapshot of the state variables
	 * @throws JSONException 
	 * 
	 */
	@Override
	public JSONObject takeSnapshot() throws JSONException {
		//Aggiungere un controllo che permette di fare lo snapshot solo se lo stato del dispositivo non è fault?

		//Fare lo snapshot significa salvare lo stato delle variabili di interesse
		JSONObject snapshot = new JSONObject();

		//PRESENCE SENSORS STATE
		JSONObject JSONpresenceSensors = new JSONObject();
		for (PresenceSensor presence : presenceSensors) {
			boolean value = (boolean) presence.getPropertyValue("presenceSensor.sensedPresence");
			String location = (String) presence.getPropertyValue("Location");
			JSONpresenceSensors.put(location.toString(), value);
		}
		snapshot.put("presenceSensors", JSONpresenceSensors);

//		//BINARY LIGHTS STATE
//		JSONObject JSONbinaryLights = new JSONObject();
//		for (BinaryLight binLight : binaryLights) {
//			boolean value = (boolean) binLight.getPropertyValue("binaryLight.powerStatus");
//			String location = (String) binLight.getPropertyValue("Location");
//			JSONbinaryLights.put(location.toString(), value);
//		}
//		snapshot.put("binaryLights", JSONbinaryLights);
//
//		//THERMOMETERS STATE
//		JSONObject JSONthermometer = new JSONObject();
//		for (Thermometer thermo : thermometers) {
//			double value = (double) thermo.getPropertyValue("thermometer.currentTemperature");
//			String location = (String) thermo.getPropertyValue("Location");
//			JSONthermometer.put(location.toString(), value);
//		}
//		snapshot.put("thermometers", JSONthermometer);
//
//		//WINDOWS STATE
//		JSONObject JSONwindow = new JSONObject();
//		for (DoorWindowSensor window : windows) {
//			boolean value = (boolean) window.getPropertyValue("doorWindowSensor.opneningDetection");
//			String location = (String) window.getPropertyValue("Location");
//			JSONwindow.put(location.toString(), value);
//		}
//		snapshot.put("windows", JSONwindow);
//
//		//HEATER STATE
//		JSONObject JSONheater = new JSONObject();
//		for (Heater heat : heaters) {
//			double value = (double) heat.getPropertyValue("heater.powerLevel");
//			String location = (String) heat.getPropertyValue("Location");
//			JSONheater.put(location.toString(), value);
//		}
//		snapshot.put("heaters", JSONheater);
//
//		//POWER CONSUMPTION STATE
//		JSONObject JSONconsumption = new JSONObject();
//		double value = (double) powerConsumption.getCurrentConsumption();
//		JSONconsumption.put("Total", value);
//		snapshot.put("powerConsumption", JSONconsumption);

		return snapshot;
	}



}
