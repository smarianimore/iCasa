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
import java.util.List;
import java.util.Map;

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
		// TODO: Add your implementation code here
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

	/** Take the snapshot of the state variables
	 * @throws JSONException 
	 * 
	 */
	@Override
	public synchronized JSONObject takeSnapshot() throws JSONException {

		//The snapshot variables will have the values of the variables of the system
		JSONObject snapshot = new JSONObject();

		//TIMESTAMP (used as id)
		JSONObject JSONtimestamp = new JSONObject();
		long currentMillisTime = clockService.currentTimeMillis();
		Timestamp timestamp = new Timestamp(currentMillisTime);
		JSONtimestamp.put("timestamp", timestamp);
		snapshot.put("timestamp", JSONtimestamp);

		//PRESENCE SENSORS STATE
		JSONObject JSONpresenceSensors = new JSONObject();
		for (PresenceSensor presence : presenceSensors) {
			boolean value = false;
			if(presence.getPropertyValue("fault") == "no" && presence.getPropertyValue("state") == "activated")
				value = (boolean) presence.getPropertyValue("presenceSensor.sensedPresence");
			String location = (String) presence.getPropertyValue("Location");
			JSONpresenceSensors.put(location.toString(), value ? 1 : 0);
		}
		snapshot.put("Pr", JSONpresenceSensors);

		//BINARY LIGHTS STATE
		JSONObject JSONbinaryLights = new JSONObject();
		for (BinaryLight binLight : binaryLights) {
			boolean value = false;
			if(binLight.getPropertyValue("fault") == "no" && binLight.getPropertyValue("state") == "activated")
				value = (boolean) binLight.getPropertyValue("binaryLight.powerStatus");
			String location = (String) binLight.getPropertyValue("Location");
			JSONbinaryLights.put(location.toString(), value ? 1 : 0);
		}
		snapshot.put("L", JSONbinaryLights);

		//THERMOMETERS STATE
		JSONObject JSONthermometer = new JSONObject();
		for (Thermometer thermo : thermometers) {
			double value = -1;
			if(thermo.getPropertyValue("fault") == "no" && thermo.getPropertyValue("state") == "activated")
				value = (double) thermo.getPropertyValue("thermometer.currentTemperature");
			String location = (String) thermo.getPropertyValue("Location");
			JSONthermometer.put(location.toString(), this.roundDouble(value));
			
			System.out.println("Temperatura rilevata: " + this.roundDouble(value) + " in " + location);
			
		}
		snapshot.put("T", JSONthermometer);

		//WINDOW STATE
		JSONObject JSONwindow = new JSONObject();
		boolean valueOpen = false;
		if(windows.getPropertyValue("fault") == "no" && windows.getPropertyValue("state") == "activated")
			valueOpen = (boolean) windows.getPropertyValue("doorWindowSensor.opneningDetection");
		String locationWindow = (String) windows.getPropertyValue("Location");
		JSONwindow.put(locationWindow.toString(), valueOpen ? 1 : 0);
		snapshot.put("W", JSONwindow);

		//HEATER STATE
		JSONObject JSONheater = new JSONObject();
		for (Heater heat : heaters) {
			double value = -1;
			if(heat.getPropertyValue("fault") == "no" && heat.getPropertyValue("state") == "activated")
				value = (double) heat.getPropertyValue("heater.powerLevel");
			String location = (String) heat.getPropertyValue("Location");
			JSONheater.put(location.toString(), this.roundDouble(value));
		}
		snapshot.put("H", JSONheater);

		//POWER CONSUMPTION STATE
		JSONObject JSONconsumption = new JSONObject();
		double value = (double) powerConsumption.getCurrentConsumption();
		JSONconsumption.put("Total", this.roundDouble(value));
		snapshot.put("Pow", JSONconsumption);

		return snapshot;
	}

	//Bisogna creare un metodo che in base al valore dei threshold vadi ad impostare il valore dei dispositivi
	@Override
	public synchronized void setValues() {

		//POWER CONSUMPTION: in base alla power consumption accendiamo o meno i riscaldamenti, tenendo conto dell'energia
		//consumata già dalle luci che sono accese
		double maxPower = this.powerConsumptionThreshold;
		double powerOfLights = 0;
		for (BinaryLight binLight : binaryLights) {
			powerOfLights += (double) binLight.getPropertyValue("powerObservable.currentConsumption");
		}
		maxPower -= powerOfLights;
		double powerForOneHeater = this.roundDouble(maxPower / 4);
		for (Heater heat : heaters) {
			heat.setPropertyValue("heater.powerLevel", powerForOneHeater);
		}

		//Migliorare il modello di influenza della temperatura esterna: quando la finestra è aperta, la temperatura interna
		//tende verso la temperatura esterna
		//If the window is opened the outdoor temperature influences the indoor temperature
		if (this.windowOpened) {
			double temperature = (this.indoorTemperatureThreshold + this.outdoorTemperatureThreshold) / 2;
			for (Thermometer therm : thermometers) {
				String location = (String) therm.getPropertyValue("Location");
				//Let's assign the mean temperature only to the indoor thermometers
				if (location != "outdoor")
					therm.setPropertyValue("thermometer.currentTemperature", temperature);
			}
		//If the window is closed the outdoor temperature does not influences the indoor one
		}else {
			for (Thermometer therm : thermometers) {
				String location = (String) therm.getPropertyValue("Location");
				if (location != "outdoor")
					therm.setPropertyValue("thermometer.currentTemperature", this.indoorTemperatureThreshold);
				else
					therm.setPropertyValue("thermometer.currentTemperature", this.outdoorTemperatureThreshold);
			}
		}
		
		//Bisogna capire se la temperatura assegnata viene mantenuta o se a prevalere è la misurazione effettuata dai termometri
		//ad ogni iterazione

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
	public synchronized void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		//we assume that we listen only to presence sensor events (otherwise there is a bug)  
		assert device instanceof PresenceSensor : "device must be a presence sensors only";

		//based on that assumption we can cast the generic device without checking via instanceof
		PresenceSensor changingSensor = (PresenceSensor) device;

		// check the change is related to presence sensing
		if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {

			// get the location of the changing sensor:
			String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);

			System.out.println("The device with the serial number" + changingSensor.getSerialNumber() + " has changed");
			System.out.println("This sensor is in the room :" + detectorLocation);

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
	private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {
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
