package iCasa.system.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import iCasa.devices.component.configuration.SystemServiceConfiguration;
import iCasa.dataset.manager.services.datasetManagerServices;
import iCasa.api.manager.configuration.APIManagerConfiguration;

public class SystemManagerImpl implements PeriodicRunnable {

	/** Field for devicesConfiguration dependency */
	private SystemServiceConfiguration devicesConfiguration;
	/** Field for datasetManager dependency */
	private datasetManagerServices datasetManager;
	/** Field for apiManager dependency */
	private APIManagerConfiguration apiManager;

	/* MAIN PROGRAM */
	@Override
	public void run() {
		System.out.println("Snapshot del sistema");
		try {
			//TAKE THE SNAPSHOT OF THE SYSTEM
			JSONObject snapshot = new JSONObject();
			snapshot = devicesConfiguration.takeSnapshot();

			System.out.println(snapshot);

			//WRITE THE RECORD ON THE DATASET
			datasetManager.buildAndWrite(snapshot);

			System.out.println("Snapshot recorded");

			//SET THE VARIABLES IN A RANDOM WAY
			this.setVariablesRandom();

		} catch (JSONException | IOException e) {
			System.out.println("Manager exception on run method!");
			e.printStackTrace();
		}

	}

	public synchronized void setVariablesRandom() throws JSONException, MalformedURLException{
		Random r = new Random();

		//INDOOR TEMPERATURE THRESHOLD
		double rangeMinIndoorT = 288.15;
		double rangeMaxIndoorT = 298.15;
		double rndIndoorT = rangeMinIndoorT + (rangeMaxIndoorT - rangeMinIndoorT) * r.nextDouble();
		devicesConfiguration.setIndoorTemperatureThreshold(rndIndoorT);
		
		System.out.println("Indoor random temperature: " + rndIndoorT);

		//OUTDOOR TEMPERATURE THRESHOLD
		double rangeMinOutdoorT = 273.15;
		double rangeMaxOutdoorT = 308.15;
		double rndOutdoorT = rangeMinOutdoorT + (rangeMaxOutdoorT - rangeMinOutdoorT) * r.nextDouble();
		devicesConfiguration.setOutdoorTemperatureThreshold(rndOutdoorT);
		
		System.out.println("Outdoor random temperature: " + rndOutdoorT);

//		//POWER CONSUMPTION THRESHOLD
//		double rangeMinPowerConsumption = 0;
//		double rangeMaxPowerConsumption = 4000;
//		double rndPowerConsumption = rangeMinPowerConsumption
//				+ (rangeMaxPowerConsumption - rangeMinPowerConsumption) * r.nextDouble();	
//		devicesConfiguration.setPowerConsumptionThreshold(rndPowerConsumption);
		
		//HEATER LEVEL
		double rangeMinHeaterLeavel = 0;
		double rangeMaxHeaterLeavel = 1000;
		double rndHeaterLevel = rangeMinHeaterLeavel + (rangeMaxHeaterLeavel - rangeMinHeaterLeavel) * r.nextDouble();
		devicesConfiguration.setHeaterLevel(rndHeaterLevel);
		
		System.out.println("Random Heater Level: " + rndHeaterLevel);
		
		//WINDOW OPENED/CLOSED
		boolean opening = r.nextBoolean();
		devicesConfiguration.setWindowOpened(opening);
		
		System.out.println(opening ? "Finestra aperta" : "Finestra chiusa");
		
		//MOVE PERSON RANDOMLY
		List<String> zones = new ArrayList<String>();
		zones = apiManager.getZonesList();
		List<String> persons = new ArrayList<String>();
		persons = apiManager.getPersonsList();
		//move each person in a random location chosen from the list
		for (String person : persons) {
			String zone = zones.get(r.nextInt(zones.size()));
			apiManager.movePerson(person, zone);
			//System.out.println("Person " + person + " moved to " + zone);
		} 

		//Call the method of the device component in order to set the variables
		devicesConfiguration.setValues();

	}

	@Override
	public long getPeriod() {
		return 1;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.HOURS;
	}

	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void start() {
	}

}
