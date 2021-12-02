package iCasa.devices.component;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import iCasa.devices.component.configuration.SystemServiceConfiguration;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.PowerObservable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import fr.liglab.adele.icasa.clockservice.Clock;

public class devicesComponentImpl implements SystemServiceConfiguration, DeviceListener {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for windows dependency */
	private DoorWindowSensor windows;
	/** Field for powerConsumption dependency */
	private PowerObservable powerConsumption;
	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for clockService dependency */
	private Clock clockService;
	/** Injected field for the service property outdoorTemperatureThreshold */
	private Double outdoorTemperatureThreshold;
	/** Injected field for the service property indoorTemperatureThreshold */
	private Double indoorTemperatureThreshold;
	/** Injected field for the service property powerConsumptionThreshold */
	private Double powerConsumptionThreshold;
	/** Injected field for the service property windowOpened */
	private Boolean windowOpened;

	public static final String LOCATION_PROPERTY_NAME = "Location";
	public static final String LOCATION_UNKNOWN = "unknown";
	/** Injected field for the service property heaterLevel */
	private Double heaterLevel;

	/** Component Lifecycle Method */
	public void stop() {
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
	}

	/** Bind Method for presenceSensors dependency */
	public void bindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public void unbindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.removeListener(this);
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
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometers(Thermometer thermometer, Map properties) {
		// TODO: Add your implementation code here
	}

	public double roundDouble(double num) {
		if (num != 0)
			return Math.round(num * 100.0) / 100.0;
		else
			return 0d;
	}

	/** Take the snapshot of the state variables: construct the JSON
	 * @throws JSONException 
	 * 
	 */
	@Override
	public JSONObject takeSnapshot() throws JSONException {
		
		//First construct the snapshot as a LinkedHashMap to preserve the order of injection
		LinkedHashMap<String, Object> snapshot = new LinkedHashMap<String, Object>();

		//TIMESTAMP
		JSONObject JSONtimestamp = new JSONObject();
		long currentMillisTime = clockService.currentTimeMillis();
		Timestamp timestamp = new Timestamp(currentMillisTime);
		JSONtimestamp.put("timestamp", timestamp);
		snapshot.put("timestamp", JSONtimestamp);

		//PRESENCE SENSORS STATE
		JSONObject JSONpresenceSensors = new JSONObject();
		for (PresenceSensor presence : presenceSensors) {
			boolean value = false;
			if (presence.getPropertyValue("fault") == "no" && presence.getPropertyValue("state") == "activated")
				value = (boolean) presence.getPropertyValue("presenceSensor.sensedPresence");
			String location = (String) presence.getPropertyValue("Location");
			JSONpresenceSensors.put(location.toString(), value ? 1 : 0);
		}
		snapshot.put("Pr", JSONpresenceSensors);

		//BINARY LIGHTS STATE
		JSONObject JSONbinaryLights = new JSONObject();
		for (BinaryLight binLight : binaryLights) {
			boolean value = false;
			if (binLight.getPropertyValue("fault") == "no" && binLight.getPropertyValue("state") == "activated")
				value = (boolean) binLight.getPropertyValue("binaryLight.powerStatus");
			String location = (String) binLight.getPropertyValue("Location");
			JSONbinaryLights.put(location.toString(), value ? 1 : 0);
		}
		snapshot.put("L", JSONbinaryLights);

		//INDOOR THERMOMETERS STATE
		JSONObject JSONthermometer = new JSONObject();
		for (Thermometer thermo : thermometers) {
			String location = (String) thermo.getPropertyValue("Location");
			if (!location.equals("outdoor")) {
				double value = -1;
				if (thermo.getPropertyValue("fault") == "no" && thermo.getPropertyValue("state") == "activated")
					value = (double) thermo.getPropertyValue("thermometer.currentTemperature");
				JSONthermometer.put(location.toString(), this.roundDouble(value));

				System.out.println("Temperatura interna rilevata: " + this.roundDouble(value) + " in " + location);
			}

		}
		snapshot.put("T", JSONthermometer);
				
		//OUTDOOR THERMOMETER STATE
		JSONObject JSONotudoor = new JSONObject();
		for (Thermometer thermo : thermometers) {
			String location = (String) thermo.getPropertyValue("Location");
			if (location.equals("outdoor")) {
				double value = -1;
				if (thermo.getPropertyValue("fault") == "no" && thermo.getPropertyValue("state") == "activated")
					value = (double) thermo.getPropertyValue("thermometer.currentTemperature");
				JSONotudoor.put(location.toString(), this.roundDouble(value));

				System.out.println("Temperatura esterna rilevata: " + this.roundDouble(value) + " in " + location);
			}

		}
		snapshot.put("O", JSONotudoor);

		//WINDOW STATE
		JSONObject JSONwindow = new JSONObject();
		boolean valueOpen = false;
		if (windows.getPropertyValue("fault") == "no" && windows.getPropertyValue("state") == "activated")
			valueOpen = (boolean) windows.getPropertyValue("doorWindowSensor.opneningDetection");
		String locationWindow = (String) windows.getPropertyValue("Location");
		JSONwindow.put(locationWindow.toString(), valueOpen ? 1 : 0);
		snapshot.put("W", JSONwindow);

		//HEATER STATE
		JSONObject JSONheater = new JSONObject();
		for (Heater heat : heaters) {
			double value = -1;
			if (heat.getPropertyValue("fault") == "no" && heat.getPropertyValue("state") == "activated")
				value = (double) heat.getPropertyValue("heater.powerLevel");
			String location = (String) heat.getPropertyValue("Location");
			JSONheater.put(location.toString(), this.roundDouble(value));
		}
		snapshot.put("H", JSONheater);

		//POWER CONSUMPTION STATE: since the internal model does not consider the power from the heater, we have to add it
		JSONObject JSONconsumption = new JSONObject();
		double value = (double) powerConsumption.getCurrentConsumption();
		System.out.println("Consumo di potenza prima del contributo degli heaters: " + value);
		for (Heater heating : heaters) {
			value += (double) heating.getPropertyValue("powerObservable.currentConsumption");
		}
		System.out.println("Consumo di potenza con il contributo degli heaters: " + value);
		JSONconsumption.put("Total", this.roundDouble(value));
		snapshot.put("Pow", JSONconsumption);

		//Transform the snapshot to JSON and return
		JSONObject JSONsnapshot = new JSONObject(snapshot);
		
		return JSONsnapshot;
	}

	public boolean getHeaterStatus() {
		double heaterValue = this.heaterLevel; //Questo valore sarà random
		if (heaterValue > 500)
			return true;
		else
			return false;

		/*Si potrebbe pensare di impostare almeno 3 livelli per H e renderlo una percentuale da moltiplicare
		 * quando si va ad impostare la temperatura*/

	}

	public double setTemperature() {
		double Text = this.outdoorTemperatureThreshold;
		double Tint = this.indoorTemperatureThreshold;
		boolean H = this.getHeaterStatus(); //da implementare i livelli di heater

		//Window opened
		if (this.windowOpened) {
			if (Text > Tint) {
				if (H)
					Tint = Tint + ((Text - Tint) * 0.8);
				else
					Tint = Tint + ((Text - Tint) * 0.6);
			} else if (Text == Tint) {
				if (H)
					Tint = Text + (Tint * 0.2);
				else
					Tint = Text;
			} else if (Text < Tint) {
				if (H)
					Tint = Tint - ((Tint - Text) * 0.6);
				else
					Tint = Tint - ((Tint - Text) * 0.8);
			}
		}//Window closed
		else if (!this.windowOpened) {
			double alfa = 0.05;	//fattore di crescita da regolare in base al massimo della temperatura interna che si può raggiungere
			if (H)
				Tint = Tint + Tint * alfa;
			else
				Tint = Tint;
		}
		
		return Tint;
	}


	/*
	 * The method sets the values of the devices, based on the values randomly given by the manager
	 * */
	@Override
	public void setValues() {

		System.out.println("----------------------MANAGER TIME START-----------------------------");
		
		double Tint = this.setTemperature();
		for (Thermometer therm : thermometers) {
			String location = (String) therm.getPropertyValue("Location");
			if (location.equals("outdoor"))
				therm.setPropertyValue("thermometer.currentTemperature", this.outdoorTemperatureThreshold);
			else
				therm.setPropertyValue("thermometer.currentTemperature", Tint);
		}
		System.out.println("Temperatura interna dopo calcolo: " + Tint);
		
		for (Heater heater : heaters) {
			heater.setPropertyValue("heater.powerLevel", this.heaterLevel);
		}

		//Bisogna confermare che la temperatura assegnata viene mantenuta o se a prevalere è la misurazione effettuata dai termometri
		//ad ogni iterazione

		System.out.println("----------------------MANAGER TIME END-----------------------------");
	}

	@Override
	public Double getOutdoorTemperatureThreshold() {
		return this.outdoorTemperatureThreshold;
	}

	@Override
	public void setOutdoorTemperatureThreshold(double outdoorTemperatureThreshold) {
		this.outdoorTemperatureThreshold = outdoorTemperatureThreshold;
	}

	@Override
	public Double getIndoorTemperatureThreshold() {
		return this.indoorTemperatureThreshold;
	}

	@Override
	public void setIndoorTemperatureThreshold(double indoorTemperatureThreshold) {
		this.indoorTemperatureThreshold = indoorTemperatureThreshold;
	}

	@Override
	public Double getPowerConsumptionThreshold() {
		return this.powerConsumptionThreshold;
	}

	@Override
	public void setPowerConsumptionThreshold(double powerConsumptionThreshold) {
		this.powerConsumptionThreshold = powerConsumptionThreshold;
	}

	@Override
	public Boolean getWindowOpened() {
		return this.windowOpened;
	}

	@Override
	public void setWindowOpened(boolean windowOpened) {
		this.windowOpened = windowOpened; //inutile?
		windows.setPropertyValue("doorWindowSensor.opneningDetection", windowOpened);
	}
	

	@Override
	public Double getHeaterLevel() {
		return this.heaterLevel;
	}

	@Override
	public void setHeaterLevel(double heaterLevel) {
		this.heaterLevel = heaterLevel;
		
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		//we assume that we listen only to presence sensor events (otherwise there is a bug)  
		assert device instanceof PresenceSensor : "device must be a presence sensors only";

		//based on that assumption we can cast the generic device without checking via instanceof
		PresenceSensor changingSensor = (PresenceSensor) device;

		// check the change is related to presence sensing
		if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {

			// get the location of the changing sensor:
			String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);

			//System.out.println("The device with the serial number" + changingSensor.getSerialNumber() + " has changed");
			//System.out.println("This sensor is in the room :" + detectorLocation);

			if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
				// get the related binary lights
				List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);

				for (BinaryLight binaryLight : sameLocationLigths) {
					if (changingSensor.getSensedPresence()) {
						binaryLight.turnOn();
					} else {
						binaryLight.turnOff();
					}
				}
			}
		}

	}

	/**
	 * Return all BinaryLight from the given location
	 * 
	 * @param location
	 *            : the given location
	 * @return the list of matching BinaryLights
	 */
	private List<BinaryLight> getBinaryLightFromLocation(String location) {
		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binLight : binaryLights) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binLight);
			}
		}
		return binaryLightsLocation;
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}


}
