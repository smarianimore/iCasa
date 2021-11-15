package iCasa.system.manager;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import iCasa.devices.component.configuration.SystemServiceConfiguration;
import iCasa.dataset.manager.services.datasetManagerServices;

public class SystemManagerImpl implements PeriodicRunnable {

	/** Field for devicesConfiguration dependency */
	private SystemServiceConfiguration devicesConfiguration;
	/** Field for datasetManager dependency */
	private datasetManagerServices datasetManager;

	@Override
	public void run() {
		System.out.println("Snapshot del sistema");
		try {
			JSONObject snapshot = new JSONObject();
			snapshot = devicesConfiguration.takeSnapshot();

			System.out.println(snapshot);

			datasetManager.buildAndWrite(snapshot);
			
			System.out.println("Scrittura sul dataset eseguita");
			
			//Dopo ogni registrazione, il manager deve settare le 3 variabili di threshold in modo random
			this.setVariablesRandom();

		} catch (JSONException | IOException e) {
			System.out.println("Exception on system snapshot!");
			e.printStackTrace();
		}

	}
	
	public void setVariablesRandom() {
		Random r = new Random();
		
		//INDOOR TEMPERATURE THRESHOLD
		double rangeMinIndoorT = 288.15;
		double rangeMaxIndoorT = 298.15;
		double rndIndoorT = rangeMinIndoorT + (rangeMaxIndoorT - rangeMinIndoorT) * r.nextDouble();
		devicesConfiguration.setIndoorTemperatureThreshold(rndIndoorT);
		
		//OUTDOOR TEMPERATURE THRESHOLD
		double rangeMinOutdoorT = 273.15;
		double rangeMaxOutdoorT = 308.15;
		double rndOutdoorT = rangeMinOutdoorT + (rangeMaxOutdoorT - rangeMinOutdoorT) * r.nextDouble();
		devicesConfiguration.setOutdoorTemperatureThreshold(rndOutdoorT);
		
		//POWER CONSUMPTION THRESHOLD
		double rangeMinPowerConsumption = 1200;
		double rangeMaxPowerConsumption = 4000;
		double rndPowerConsumption = rangeMinPowerConsumption + (rangeMaxPowerConsumption - rangeMinPowerConsumption) * r.nextDouble();
		devicesConfiguration.setPowerConsumptionThreshold(rndPowerConsumption);
		
		//WINDOW OPENED/CLOSED
		devicesConfiguration.setWindowOpened(r.nextBoolean());
		
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
		// TODO: Add your implementation code here
	}

}
