package iCasa.system.manager;

import java.io.IOException;
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

	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
	}

	@Override
	public void run() {
		System.out.println("Snapshot del sistema");
		try {
			JSONObject snapshot = new JSONObject();
			snapshot = devicesConfiguration.takeSnapshot();

			System.out.println(snapshot);

			datasetManager.buildAndWrite(snapshot);

			System.out.println("Scrittura sul dataset eseguita");

		} catch (JSONException | IOException e) {
			System.out.println("Exception on system snapshot!");
			e.printStackTrace();
		}

	}

	@Override
	public long getPeriod() {
		return 1;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.HOURS;
	}

}
