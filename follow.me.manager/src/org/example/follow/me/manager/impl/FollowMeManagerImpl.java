package org.example.follow.me.manager.impl;

import org.example.follow.me.configuration.FollowMeConfiguration;
import java.util.Map;
import java.util.Set;

import org.example.follow.me.manager.EnergyGoal;
import org.example.follow.me.manager.FollowMeAdministration;
import org.example.follow.me.manager.IlluminanceGoal;
import org.example.time.MomentOfTheDay;
import org.example.time.MomentOfTheDayListener;
import org.example.time.MomentOfTheDayService;

import fr.liglab.adele.icasa.service.preferences.Preferences;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.service.location.PersonLocationService;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;

public class FollowMeManagerImpl implements FollowMeAdministration, DeviceListener, MomentOfTheDayListener {
	/** Field for followMe dependency */
	private FollowMeConfiguration followMe;

	/** Field for preferencesService dependency */
	private Preferences preferencesService;
	/** Field for personLocationService dependency */
	private PersonLocationService personLocationService;

	public static final String LOCATION_PROPERTY_NAME = "Location";
	public static final String LOCATION_UNKNOWN = "unknown";

	/**
	* User preferences for illuminance
	**/
	public static final String USER_PROP_ILLUMINANCE = "illuminance";

	/** Field for presenceSensor dependency */
	private PresenceSensor[] presenceSensor;

	/** Field for momentOfTheDayService dependency */
	private MomentOfTheDayService[] momentOfTheDayService;
	
    // There is no need of full illuminance in the morning 
    private double MORNING_ILLUMINANCE_FACTOR = 0.5;
    // In the afternoon the illuminance can be largely limited
    private double AFTERNOON_ILLUMINANCE_FACTOR = 0.2;
    // In the evening, the illuminance should be the best
    private double EVENING_ILLUMINANCE_FACTOR = 1;
    // In the night, there is no need to use the full illuminance
    private double NIGHT_ILLUMINANCE_FACTOR = 0.8;

	/** Bind Method for followMe dependency */
	public void bindFollowMe(FollowMeConfiguration followMeConfiguration, Map properties) {
		System.out.println("FollowMe starting...");
	}

	/** Unbind Method for followMe dependency */
	public void unbindFollowMe(FollowMeConfiguration followMeConfiguration, Map properties) {
		System.out.println("FollowMe stopping...");
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		for (PresenceSensor sensor : presenceSensor) {
			sensor.removeListener(this);
		}
		for (MomentOfTheDayService s : momentOfTheDayService) {
			s.unregister(this);
		}

	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Manager starting...");

		//Il manager qui imposta manualmente la variabile maximumNumberOfLightsToTurnOn e la MaximumAllowedEnergyInRoom
		//Questo viene fatto ogni volta che il componente del manager parte (quindi all'inizio)
		followMe.setMaximumNumberOfLightsToTurnOn(IlluminanceGoal.FULL.getNumberOfLightsToTurnOn());
		followMe.setMaximumAllowedEnergyInRoom(250.0);
		//this.setUserPreference("Paul", USER_PROP_ILLUMINANCE, (float) 100);
		//this.setUserPreference("Jacob", USER_PROP_ILLUMINANCE, (float) 4000);


	}

	@Override
	public void setIlluminancePreference(IlluminanceGoal illuminanceGoal, String user) {
		followMe.setMaximumNumberOfLightsToTurnOn(illuminanceGoal.getNumberOfLightsToTurnOn());
		followMe.setTargetedIlluminance(illuminanceGoal.getIlluminance());
		this.setUserPreference(user, USER_PROP_ILLUMINANCE, (float) illuminanceGoal.getIlluminance());
	}

	@Override
	public IlluminanceGoal getIlluminancePreference() {
		double illuminance = followMe.getTargetedIlluminance();
		return IlluminanceGoal.getCorrespondingIlluminanceFromValue(illuminance);
	}

	@Override
	public void setEnergySavingGoal(EnergyGoal energyGoal) {
		followMe.setMaximumAllowedEnergyInRoom(energyGoal.getMaximumEnergyInRoom());

	}

	@Override
	public EnergyGoal getEnergyGoal() {
		double numValue = followMe.getMaximumAllowedEnergyInRoom();
		return EnergyGoal.getCorrespondingEnergyGoal(numValue);

	}

	public void setUserPreference(String user, String name, float preference) {
		preferencesService.setUserPropertyValue(user, name, preference);

	}

	public Set<String> getUserPreference(String user) {
		return preferencesService.getUserProperties(user);
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
	public synchronized void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue,
			Object newValue) {
		//we assume that we listen only to presence sensor events (otherwise there is a bug)  
		assert device instanceof PresenceSensor : "device must be a presence sensors only";

		//based on that assumption we can cast the generic device without checking via instanceof
		PresenceSensor changingSensor = (PresenceSensor) device;

		// check the change is related to presence sensing
		if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {

			String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);

			if (!detectorLocation.equals(LOCATION_UNKNOWN)) {

				Set<String> peopleInZone = personLocationService.getPersonInZone(detectorLocation);
				if (peopleInZone.size() > 0) {
					//Impostazione della luminosità come media delle preferenze degli utenti
					float illuminanceUserBased = 0;
					for (String person : peopleInZone) {
						//Per ora utilizzo direttamente la funzione di libreria, dopo cambio per passare tramite il metodo
						//di questa classe
						illuminanceUserBased += (float) preferencesService.getUserPropertyValue(person, USER_PROP_ILLUMINANCE);
						System.out.println("Se non c'è nessuna preferenza impostata ottengo: " + illuminanceUserBased);
					}
					if (illuminanceUserBased != 0) {
						illuminanceUserBased = illuminanceUserBased / peopleInZone.size();
						//Impostiamo l'illuminazione target che servirà al binary follow me
						followMe.setTargetedIlluminance(illuminanceUserBased);
					}
				}
			}

		}

	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}

	/** Bind Method for presenceSensor dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensor dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.removeListener(this);
	}

	@Override
	public void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay) {
		System.out.println("Nuovo MomentOfTheDay: " + newMomentOfTheDay);
		String moment = newMomentOfTheDay.toString();
		System.out.println(moment);
		switch (moment) {
			case "MORNING":
				followMe.setTargetedIlluminance(MORNING_ILLUMINANCE_FACTOR * IlluminanceGoal.SOFT.getIlluminance());
			case "AFTERNOON":
				followMe.setTargetedIlluminance(AFTERNOON_ILLUMINANCE_FACTOR * IlluminanceGoal.MEDIUM.getIlluminance());
			case "EVENING":
				followMe.setTargetedIlluminance(EVENING_ILLUMINANCE_FACTOR * IlluminanceGoal.MEDIUM.getIlluminance());
			case "NIGHT":
				followMe.setTargetedIlluminance(NIGHT_ILLUMINANCE_FACTOR * IlluminanceGoal.FULL.getIlluminance());
		}
		
	}

	/** Bind Method for momentOfTheDayService dependency */
	public void bindMomentOfTheDayService(MomentOfTheDayService momentOfTheDayService, Map properties) {
		System.out.println("Bind MomentOfTheDay service");
		for (MomentOfTheDayService s : this.momentOfTheDayService) {
			s.register(this);
		}
	}

	/** Unbind Method for momentOfTheDayService dependency */
	public void unbindMomentOfTheDayService(MomentOfTheDayService momentOfTheDayService, Map properties) {
		System.out.println("Unbind MomentOfTheDay service");
		for (MomentOfTheDayService s : this.momentOfTheDayService) {
			s.unregister(this);
		}
	}
}
