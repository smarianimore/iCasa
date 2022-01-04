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
import fr.liglab.adele.icasa.device.button.PushButton;
import fr.liglab.adele.icasa.device.security.Siren;
import fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor;
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.gasSensor.CarbonMonoxydeSensor;

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
	/** Field for coolers dependency */
	private Cooler[] coolers;
	/** Field for CO2 dependency */
	private CarbonDioxydeSensor[] CO2;
	/** Field for CO dependency */
	private CarbonMonoxydeSensor[] CO;
	/** Field for alarms dependency */
	private Siren alarms;
	/** Field for buttons dependency */
	private PushButton buttons;

	/** Injected field for the service property outdoorTemperatureThreshold */
	private Double outdoorTemperatureThreshold;
	/** Injected field for the service property indoorTemperatureThreshold */
	private Double indoorTemperatureThreshold;
	/** Injected field for the service property powerConsumptionThreshold */
	private Double powerConsumptionThreshold;
	/** Injected field for the service property windowOpened */
	private Boolean windowOpened;
	/** Injected field for the service property coolerLevel */
	private Double coolerLevel;
	/** Injected field for the service property heaterLevel */
	private Double heaterLevel;
	/** Injected field for the service property CO2level */
	private Double CO2level;
	/** Injected field for the service property COlevel */
	private Double COlevel;
	/** Injected field for the service property btnStatus */
	private Boolean btnStatus;
	
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

	/**
	 * Test if a device is working
	 * */
	public boolean isWorking(GenericDevice device) {
		if (device.getPropertyValue("fault").equals("no") && device.getPropertyValue("state").equals("activated"))
			return true;
		else
			return false;
	}

	/** Take the snapshot of the state variables: construct the JSON
	 * @throws JSONException 
	 * 
	 */
	@Override
	public JSONObject takeSnapshot() throws JSONException {

		//Initially construct the snapshot as a LinkedHashMap to preserve the order of injection
		LinkedHashMap<String, Object> snapshot = new LinkedHashMap<String, Object>();

		//TIMESTAMP
		JSONObject JSONtimestamp = new JSONObject();
		long currentMillisTime = clockService.currentTimeMillis();
		Timestamp timestamp = new Timestamp(currentMillisTime);
		JSONtimestamp.put("timestamp", timestamp);
		snapshot.put("timestamp", JSONtimestamp);

		//PRESENCE SENSORS STATE
		JSONObject JSONpresenceSensors = new JSONObject();
		for (PresenceSensor device : presenceSensors) {
			boolean value = false;
			if (this.isWorking(device))
				value = (boolean) device.getPropertyValue("presenceSensor.sensedPresence");
			String location = (String) device.getPropertyValue("Location");
			JSONpresenceSensors.put(location.toString(), value ? 1 : 0);
		}
		snapshot.put("Pr", JSONpresenceSensors);

		//BINARY LIGHTS STATE
		JSONObject JSONbinaryLights = new JSONObject();
		for (BinaryLight device : binaryLights) {
			boolean value = false;
			if (this.isWorking(device))
				value = (boolean) device.getPropertyValue("binaryLight.powerStatus");
			String location = (String) device.getPropertyValue("Location");
			JSONbinaryLights.put(location.toString(), value ? 1 : 0);
		}
		snapshot.put("L", JSONbinaryLights);

		//INDOOR THERMOMETERS STATE
		JSONObject JSONthermometer = new JSONObject();
		for (Thermometer device : thermometers) {
			String location = (String) device.getPropertyValue("Location");
			if (!location.equals("outdoor")) {
				double value = -1;
				if (this.isWorking(device))
					value = (double) device.getPropertyValue("thermometer.currentTemperature");
				JSONthermometer.put(location.toString(), this.roundDouble(value));

				//System.out.println("Temperatura interna rilevata: " + this.roundDouble(value) + " in " + location);
			}

		}
		snapshot.put("T", JSONthermometer);

		//OUTDOOR THERMOMETER STATE
		JSONObject JSONotudoor = new JSONObject();
		for (Thermometer device : thermometers) {
			String location = (String) device.getPropertyValue("Location");
			if (location.equals("outdoor")) {
				double value = -1;
				if (this.isWorking(device))
					value = (double) device.getPropertyValue("thermometer.currentTemperature");
				JSONotudoor.put(location.toString(), this.roundDouble(value));

				//System.out.println("Temperatura esterna rilevata: " + this.roundDouble(value) + " in " + location);
			}

		}
		snapshot.put("O", JSONotudoor);

		//WINDOW STATE
		JSONObject JSONwindow = new JSONObject();
		boolean valueOpen = false;
		if (this.isWorking(windows))
			valueOpen = (boolean) windows.getPropertyValue("doorWindowSensor.opneningDetection");
		String locationWindow = (String) windows.getPropertyValue("Location");
		JSONwindow.put(locationWindow.toString(), valueOpen ? 1 : 0);
		snapshot.put("W", JSONwindow);

		//HEATER STATE
		JSONObject JSONheater = new JSONObject();
		for (Heater device : heaters) {
			double value = -1;
			if (this.isWorking(device))
				value = (double) device.getPropertyValue("heater.powerLevel");
			String location = (String) device.getPropertyValue("Location");
			JSONheater.put(location.toString(), this.roundDouble(value));
		}
		snapshot.put("H", JSONheater);

		//COOLER STATE
		JSONObject JSONcooler = new JSONObject();
		for (Cooler device : coolers) {
			double value = -1;
			if (this.isWorking(device))
				value = (double) device.getPropertyValue("cooler.powerLevel");
			String location = (String) device.getPropertyValue("Location");
			JSONcooler.put(location.toString(), this.roundDouble(value));
		}
		snapshot.put("C", JSONcooler);

		//POWER CONSUMPTION STATE: heaters consumption + coolers consumptions
		JSONObject JSONconsumption = new JSONObject();
		double consumptionValue = (double) powerConsumption.getCurrentConsumption();
		//System.out.println("Consumo di potenza prima del contributo degli heaters: " + this.roundDouble(consumptionValue));
		for (Heater device : heaters) {
			consumptionValue += (double) device.getPropertyValue("powerObservable.currentConsumption");
		}
		//System.out.println("Consumo di potenza con il contributo degli heaters: " + this.roundDouble(consumptionValue));
		for (Cooler device : coolers) {
			consumptionValue += (double) device.getPropertyValue("powerObservable.currentConsumption");
		}
		JSONconsumption.put("Total", this.roundDouble(consumptionValue));
		snapshot.put("Pow", JSONconsumption);

		//CARBON MONOXYDE
		JSONObject JSONco = new JSONObject();
		for (CarbonMonoxydeSensor device : CO) {
			double value = -1;
			if (this.isWorking(device))
				value = (double) device.getPropertyValue("carbonMonoxydeSensor.currentConcentration");
			String location = (String) device.getPropertyValue("Location");
			JSONco.put(location.toString(), this.roundDouble(value));
		}
		snapshot.put("CO", JSONco);

		//CARBON DIOXIDE
		JSONObject JSONco2 = new JSONObject();
		for (CarbonDioxydeSensor device : CO2) {
			double value = -1;
			if (this.isWorking(device))
				value = (double) device.getPropertyValue("carbonDioxydeSensor.currentConcentration");
			String location = (String) device.getPropertyValue("Location");
			JSONco2.put(location.toString(), this.roundDouble(value));
		}
		snapshot.put("CO2", JSONco2);

		//ALARM
		JSONObject JSONalarm = new JSONObject();
		boolean valueAlarm = false;
		if (this.isWorking(alarms))
			valueAlarm = (boolean) alarms.getPropertyValue("siren.status");
		String locationAlarm = (String) alarms.getPropertyValue("Location");
		JSONalarm.put(locationAlarm.toString(), valueAlarm ? 1 : 0);
		snapshot.put("A", JSONalarm);

		//BUTTON
		JSONObject JSONbutton = new JSONObject();
		boolean valueButton = false;
		if (this.isWorking(buttons))
			valueButton = (boolean) buttons.getPropertyValue("pushButton.pushAndHold");
		String locationButton = (String) buttons.getPropertyValue("Location");
		JSONbutton.put(locationButton.toString(), valueButton ? 1 : 0);
		snapshot.put("B", JSONbutton);

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
	
	public boolean getCoolerStatus() {
		double coolerValue = this.coolerLevel;
		if (coolerValue > 500)
			return true;
		else
			return false;
	}

	public double setTemperature() {
		double Text = this.outdoorTemperatureThreshold;
		double Tint = this.indoorTemperatureThreshold;
		boolean H = this.getHeaterStatus();
		boolean C = this.getCoolerStatus();

		//Window opened
		if (this.windowOpened) {
			if (Text > Tint) {
				if (H && !C) //with the heaters on, the indoor temperature increases
					Tint = Tint + ((Text - Tint) * 0.8);
				else if (!H && C) //with the coolers on, the indoor temperature decreases
					Tint = Tint - ((Text - Tint) * 0.8);
				else //otherwise the temperature increases but less
					Tint = Tint + ((Text - Tint) * 0.6);
			} else if (Text == Tint) {
				if (H && !C)
					Tint = Text + (Tint * 0.2);
				else if (!H && C)
					Tint = Text - (Tint * 0.2);
				else
					Tint = Text;
			} else if (Text < Tint) {
				if (H && !C)
					Tint = Tint + ((Tint - Text) * 0.6);
				else if (!H && C)
					Tint = Tint - ((Tint - Text) * 0.6);
				else
					Tint = Tint - ((Tint - Text) * 0.8);
			}
		} //Window closed
		else if (!this.windowOpened) {
			double alfa = 0.05; //fattore di crescita da regolare in base al massimo della temperatura interna che si può raggiungere
			if (H && !C) //with the heaters on the indoor temperature increases
				Tint = Tint + Tint * alfa;
			else if (!H && C) //with the coolers on the indoor temperature decreases
				Tint = Tint - Tint * alfa;
			else //if heaters and coolers are contemporary on or off, the indoor temperature remains constant
				Tint = Tint;
		}

		return Tint;
	}

	/*
	 * The method sets the values of the devices, based on the values randomly given by the manager:
	 * 	- window open/close
	 * 	- indoor and outdoor temperatures
	 *  - heaters level
	 * */
	@Override
	public void setValues() {

		System.out.println("----------------------MANAGER TIME START-----------------------------");

		//Queste assegnazioni potrebbero farsi nei metodi setCOlevel e setCO2level
		for (CarbonMonoxydeSensor device : CO) {
			device.setPropertyValue("carbonMonoxydeSensor.currentConcentration", this.COlevel);
		}
		for (CarbonDioxydeSensor device : CO2) {
			device.setPropertyValue("carbonDioxydeSensor.currentConcentration", this.CO2level);
		}
		
		//Activate alarm if CO and CO2 levels high (migliorabile)
		if (this.COlevel >= 35 || this.CO2level >= 2000) {
			if (this.isWorking(alarms))
				alarms.setPropertyValue("siren.status", true);
		}else if (this.COlevel >= 15 && this.CO2level >= 1400) {
			if (this.isWorking(alarms))
				alarms.setPropertyValue("siren.status", true);
		}

		//Window: set the window with the random value
		//windows.setPropertyValue("doorWindowSensor.opneningDetection", this.windowOpened);

		//Window: open the window if alarm on or button pushed (pay attention: this setting makes the this.windowOpened useless)
		if (this.isWorking(alarms))
			if ((boolean) alarms.getPropertyValue("siren.status")) {
				windows.setPropertyValue("doorWindowSensor.opneningDetection", true); //device
				this.setWindowOpened(true);	//local variable
			}else {
				this.setWindowOpened(false);
			}
				
		if (this.btnStatus) {
			buttons.setPropertyValue("pushButton.pushAndHold", true);
			windows.setPropertyValue("doorWindowSensor.opneningDetection", true);
			this.setWindowOpened(true);
		}else {
			buttons.setPropertyValue("pushButton.pushAndHold", false);
			this.setWindowOpened(false);
		}

		//Temperature <-- Attention: it needs this.windowsOpened
		double Tint = this.setTemperature();
		for (Thermometer device : thermometers) {
			String location = (String) device.getPropertyValue("Location");
			if (location.equals("outdoor"))
				device.setPropertyValue("thermometer.currentTemperature", this.outdoorTemperatureThreshold);
			else
				device.setPropertyValue("thermometer.currentTemperature", Tint);
		}
		
		//Heater
		for (Heater heater : heaters) {
			heater.setPropertyValue("heater.powerLevel", this.heaterLevel);
		}

		//Cooler
		for (Cooler cooler : coolers) {
			cooler.setPropertyValue("cooler.powerLevel", this.coolerLevel);
		}

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
		this.windowOpened = windowOpened;
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

	/** Bind Method for CO2 dependency */
	public void bindCO2(CarbonDioxydeSensor carbonDioxydeSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for CO2 dependency */
	public void unbindCO2(CarbonDioxydeSensor carbonDioxydeSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Bind Method for CO dependency */
	public void bindCO(CarbonMonoxydeSensor carbonMonoxydeSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for CO dependency */
	public void unbindCO(CarbonMonoxydeSensor carbonMonoxydeSensor, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Bind Method for coolers dependency */
	public void bindCoolers(Cooler cooler, Map properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for coolers dependency */
	public void unbindCoolers(Cooler cooler, Map properties) {
		// TODO: Add your implementation code here
	}

	@Override
	public Double getCoolerLevel() {
		return this.coolerLevel;
	}

	@Override
	public void setCoolerLevel(double coolerLevel) {
		this.coolerLevel = coolerLevel;
	}

	@Override
	public Double getCOlevel() {
		return this.COlevel;
	}

	@Override
	public void setCOlevel(double COlevel) {
		this.COlevel = COlevel;

	}

	@Override
	public Double getCO2level() {
		return this.CO2level;
	}

	@Override
	public void setCO2level(double CO2level) {
		this.CO2level = CO2level;

	}

	@Override
	public Boolean getBtnStatus() {
		return this.btnStatus;
	}

	@Override
	public void setBtnStatus(boolean btnStatus) {
		this.btnStatus = btnStatus;
	}

}
