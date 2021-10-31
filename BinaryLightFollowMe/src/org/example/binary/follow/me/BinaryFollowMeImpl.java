package org.example.binary.follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.service.zone.size.calculator.ZoneSizeCalculator;
import fr.liglab.adele.icasa.service.location.PersonLocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.follow.me.configuration.FollowMeConfiguration;

public class BinaryFollowMeImpl implements DeviceListener, FollowMeConfiguration {
	public static final String LOCATION_PROPERTY_NAME = "Location";
	public static final String LOCATION_UNKNOWN = "unknown";

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLigths dependency */
	private BinaryLight[] binaryLigths;
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;

	/** Injected field for the service property maximumNumberOfLightsToTurnOn */
	private Integer maximumNumberOfLightsToTurnOn;
	/** Injected field for the service property MaximumAllowedEnergyInRoom */
	private Double MaximumAllowedEnergyInRoom;
	/** Injected field for the service property targetedIlluminance */
	private Double targetedIlluminance;

	/**
	 * Watt to lumens conversion factor
	 * It has been considered that: 1 Watt=680.0 lumens at 555nm.
	 */
	public final static double ONE_WATT_TO_ONE_LUMEN = 680.0d;

	/** Field for zoneCalculator dependency */
	private ZoneSizeCalculator zoneCalculator;

	/**
	* User preferences for illuminance
	**/
	public static final String USER_PROP_ILLUMINANCE = "illuminance";

	/** Bind Method for binaryLigths dependency */
	public void bindBinaryLigths(BinaryLight binaryLight, Map properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
	}

	/** Unbind Method for binaryLigths dependency */
	public void unbindBinaryLigths(BinaryLight binaryLight, Map properties) {
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
	}

	/** Bind Method for dimmerLights dependency */
	public void bindDimmerLights(DimmerLight dimmerLight, Map properties) {
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
	}

	/** Unbind Method for dimmerLights dependency */
	public void unbindDimmerLights(DimmerLight dimmerLight, Map properties) {
		System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
	}

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeListener(this);
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("Component is stopping...");
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
	}
	
	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component is starting...");
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

			//Calcoliamo l'area della stanza
			float area = zoneCalculator.getSurfaceInMeterSquare(detectorLocation);

			if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
				// get the related binary lights
				List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
				List<DimmerLight> sameLocationDimmerLights = getDimmerLightFromLocation(detectorLocation);

				int numLightsOn = 0;

				//Ricaviamo il numero massimo di luci che possiamo accendere in base al tetto sul consumo di energia fissato
				double maxNumLightsOnTarget = this.targetedIlluminance >= 100.0 ? this.targetedIlluminance / 100.0 : 0.0;

				//Le luci binarie si accendono tenendo conto del numero massimo impostato e del massimo consumo di energia
				for (BinaryLight binaryLight : sameLocationLigths) {
					// and switch them on/off depending on the sensed presence
					if (changingSensor.getSensedPresence() && numLightsOn < maxNumLightsOnTarget) {
						binaryLight.turnOn();
						numLightsOn++;
					} else {
						binaryLight.turnOff();
					}
				}

				//Una volta accese le luci binarie, ricaviamo il quantitativo di energia utilizzabile rimanente
				//come differenza tra il massimo numero di luci che si possono accendere per il consumo di energia e il numero di luci accese
				double remainingPowerAvailable = maxNumLightsOnTarget - numLightsOn; //assumiamo questa quantità positiva dall'if precedente

				//Con la potenza rimanente, accendiamo delle dimmer lights impostando il loro livello adeguatamente
				for (DimmerLight dimmerLight : sameLocationDimmerLights) {
					if (changingSensor.getSensedPresence() && remainingPowerAvailable != 0) {
						if (remainingPowerAvailable <= 1.0) {
							dimmerLight.setPowerLevel(remainingPowerAvailable);
							numLightsOn++;
							remainingPowerAvailable -= remainingPowerAvailable;
						} else {
							dimmerLight.setPowerLevel(1.0);
							numLightsOn++;
							remainingPowerAvailable -= 1.0;
						}
					} else {
						dimmerLight.setPowerLevel(0.0);
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

	/**
	 * Return all DimmerLight from the given location
	 * 
	 * @param location
	 *            : the given location
	 * @return the list of matching DimmerLights
	 */
	private synchronized List<DimmerLight> getDimmerLightFromLocation(String location) {
		//creaiamo un'array list castata a DimmerLight
		List<DimmerLight> DimmerLightsLocation = new ArrayList<DimmerLight>();
		//scorriamo l'array delle dimmerLights presenti
		for (DimmerLight dimLight : dimmerLights) {
			//selezioniamo solo quelle che hanno la stessa location e le aggiungiamo alla lista
			if (dimLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				DimmerLightsLocation.add(dimLight);
			}
		}
		//ritorniamo la lista
		return DimmerLightsLocation;
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
		for (BinaryLight binLight : binaryLigths) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binLight);
			}
		}
		return binaryLightsLocation;
	}

	@Override
	public int getMaximumNumberOfLightsToTurnOn() {
		return maximumNumberOfLightsToTurnOn;
	}

	@Override
	public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
		this.maximumNumberOfLightsToTurnOn = maximumNumberOfLightsToTurnOn;

	}

	@Override
	public double getMaximumAllowedEnergyInRoom() {
		return this.MaximumAllowedEnergyInRoom;
	}

	@Override
	public void setMaximumAllowedEnergyInRoom(double maximumEnergy) {
		this.MaximumAllowedEnergyInRoom = maximumEnergy;

	}

	@Override
	public double getTargetedIlluminance() {
		return this.targetedIlluminance;
	}

	@Override
	public void setTargetedIlluminance(double illuminance) {
		this.targetedIlluminance = illuminance;
	}

}
