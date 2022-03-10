package it.unimore.icasa.rest;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.remote.wisdom.util.IcasaJSONUtil;

import org.json.JSONObject;

import org.wisdom.api.DefaultController;

import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.Route;

import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.MimeTypes;
import org.wisdom.api.http.Result;

import java.util.Random;

/**
 *
 *
 */
@Path("/icasa/devices")
public class DevicePropertyREST extends DefaultController {

	private ContextManager contextMgr;
	
	long seed = 12;
	Random r = new Random(seed);

	@Route(method = HttpMethod.PUT, uri = "/device/{deviceId}/{propertyId}")
	public Result updatesDevice(@Parameter("deviceId") String deviceId, @Parameter("propertyId") String propertyId) {

		if (deviceId == null || deviceId.length() < 1) {
			return notFound();
		}

		int value;
		try {
			value = Integer.valueOf(IcasaJSONUtil.getContent(context().reader()));
			System.out.println("Received value: " + value);
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError();
		}

		GenericDevice device = contextMgr.getGenericDevice(deviceId);
		if (device == null) {
			return notFound();
		}

		//Ricevo sempre 1 o 0 e converto prima di attualizzare il valore in base al dispositivo che mi ritrovo
		if (propertyId != null && device.constainsProperty(propertyId)) {
			actualizeValue(propertyId, value, deviceId, device);
		}

		JSONObject deviceJSON = IcasaJSONUtil.getDeviceJSON(contextMgr.getDevice(deviceId), contextMgr);

		return ok(deviceJSON.toString()).as(MimeTypes.JSON);
	}

	public void actualizeValue(String propertyId, int value, String deviceId, GenericDevice device) {

		switch (deviceId) {
		case "BinaryLight-5022136575":
			device.setPropertyValue(propertyId, value == 0 ? false : true);
			break;
		case "ToggleSwitch-d9704c5a0f":
			device.setPropertyValue(propertyId, value == 0 ? false : true);
			// Modeling causality S->H and S->C: each device takes the opposite actual value
			GenericDevice heater = contextMgr.getGenericDevice("Heater-1bd6fdc99a");
			GenericDevice cooler = contextMgr.getGenericDevice("Cooler-ce2209b064");

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
		case "Heater-1bd6fdc99a":
			device.setPropertyValue(propertyId, value == 0 ? 0.0 : 1000.0);
			break;
		case "Cooler-ce2209b064":
			device.setPropertyValue(propertyId, value == 0 ? 0.0 : 1000.0);
			break;
		case "Siren-d6c226252a":
			device.setPropertyValue(propertyId, value == 0 ? false : true);
			// Modeling causality A->W
			GenericDevice windowA = contextMgr.getGenericDevice("DoorWindowSensor-b041e06747");
			if (value == 1)
				windowA.setPropertyValue("doorWindowSensor.opneningDetection", true);
			else
				windowA.setPropertyValue("doorWindowSensor.opneningDetection", false);
			break;
		case "PushButton-5ec148b252":
			device.setPropertyValue(propertyId, value == 0 ? false : true);
			// Modeling causality B->W
			GenericDevice windowB = contextMgr.getGenericDevice("DoorWindowSensor-b041e06747");
			if (value == 1)
				windowB.setPropertyValue("doorWindowSensor.opneningDetection", true);
			else
				windowB.setPropertyValue("doorWindowSensor.opneningDetection", false);
			break;
		case "DoorWindowSensor-b041e06747":
			device.setPropertyValue(propertyId, value == 0 ? false : true);
			// Modeling causality W->T
			randomInitialization();
			double currentValueInt = 0.0;
			double currentValueExt = 0.0;
			GenericDevice in = contextMgr.getGenericDevice("Thermometer-829cc07927");
			GenericDevice ext = contextMgr.getGenericDevice("Thermometer-b1f74267ed");
			currentValueInt = (double) in.getPropertyValue("thermometer.currentTemperature");
			currentValueExt = (double) ext.getPropertyValue("thermometer.currentTemperature");
			if (value == 1) {
				if (currentValueExt < currentValueInt)
					in.setPropertyValue("thermometer.currentTemperature", (double) 273.15); // minimum value
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

	public void randomInitialization() {
//		//INDOOR TEMPERATURE THRESHOLD
//		double rangeMinIndoorT = 273.15;
//		double rangeMaxIndoorT = 303.15;
//		double rndIndoorT = rangeMinIndoorT + (rangeMaxIndoorT - rangeMinIndoorT) * r.nextDouble();
//		GenericDevice in = contextMgr.getGenericDevice("Thermometer-829cc07927");
//		in.setPropertyValue("thermometer.currentTemperature", (double) rndIndoorT);

		//OUTDOOR TEMPERATURE THRESHOLD
		double rangeMinOutdoorT = 273.15;
		double rangeMaxOutdoorT = 303.15;
		double rndOutdoorT = rangeMinOutdoorT + (rangeMaxOutdoorT - rangeMinOutdoorT) * r.nextDouble();
		GenericDevice ext = contextMgr.getGenericDevice("Thermometer-b1f74267ed");
		ext.setPropertyValue("thermometer.currentTemperature", (double) rndOutdoorT);

//		//HEATER LEVEL
//		double rangeMinHeaterLeavel = 0;
//		double rangeMaxHeaterLeavel = 1000;
//		double rndHeaterLevel = rangeMinHeaterLeavel + (rangeMaxHeaterLeavel - rangeMinHeaterLeavel) * r.nextDouble();
//		GenericDevice heater = contextMgr.getGenericDevice("Heater-1bd6fdc99a");
//		heater.setPropertyValue("heater.powerLevel", (double) rndHeaterLevel);
//
//		//COOLER LEVEL
//		double rangeMinCoolerLeavel = 0;
//		double rangeMaxCoolerLeavel = 1000;
//		double rndCoolerLevel = rangeMinCoolerLeavel + (rangeMaxCoolerLeavel - rangeMinCoolerLeavel) * r.nextDouble();
//		GenericDevice cooler = contextMgr.getGenericDevice("Cooler-ce2209b064");
//		cooler.setPropertyValue("cooler.powerLevel", (double) rndCoolerLevel);
//
//		//WINDOW OPENED/CLOSED
//		boolean opening = r.nextBoolean();
//		GenericDevice window = contextMgr.getGenericDevice("DoorWindowSensor-b041e06747");
//		window.setPropertyValue("doorWindowSensor.opneningDetection", opening);
//
//		//BUTTON
//		boolean btn = r.nextBoolean();
//		GenericDevice button = contextMgr.getGenericDevice("PushButton-5ec148b252");
//		button.setPropertyValue("pushButton.pushAndHold", btn);
//
//		//SWITCHER
//		boolean s = r.nextBoolean();
//		GenericDevice switcher = contextMgr.getGenericDevice("ToggleSwitch-d9704c5a0f");
//		switcher.setPropertyValue("powerSwitch.currentStatus", s);
	}

}
