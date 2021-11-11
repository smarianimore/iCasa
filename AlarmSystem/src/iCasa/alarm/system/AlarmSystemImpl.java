package iCasa.alarm.system;

import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.security.Siren;
import iCasa.alarm.system.configuration.AlarmSystemConfiguration;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmSystemImpl implements AlarmSystemConfiguration {
	public static final String LOCATION_PROPERTY_NAME = "Location";
	public static final String LOCATION_UNKNOWN = "unknown";
	
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;
	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for sirens dependency */
	private Siren[] sirens;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Injected field for the service property AlarmTrigger */
	private Boolean AlarmTrigger;
	/** Injected field for the service property AuthorizedPeopleIn */
	private Boolean AuthorizedPeopleIn;
	/** Injected field for the service property UnauthorizedPeopleIn */
	private Boolean UnauthorizedPeopleIn;

	/** Bind Method for sirens dependency */
	public void bindSirens(Siren siren, Map properties) {
		System.out.println("bind siren " + siren.getSerialNumber());
	}

	/** Unbind Method for sirens dependency */
	public void unbindSirens(Siren siren, Map properties) {
		System.out.println("unbind siren " + siren.getSerialNumber());
	}

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLights(BinaryLight binaryLight, Map properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLights(BinaryLight binaryLight, Map properties) {
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
	}

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
	}

	/** Bind Method for dimmerLights dependency */
	public void bindDimmerLights(DimmerLight dimmerLight, Map properties) {
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
	}

	/** Unbind Method for dimmerLights dependency */
	public void unbindDimmerLights(DimmerLight dimmerLight, Map properties) {
		System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("Alarm system is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Alarm system is starting...");
		
	}
	
	public void turnOnAllLights() {
		for (BinaryLight binLight : binaryLights) {
			binLight.setPowerStatus(true);
		}
		for (DimmerLight dimLight : dimmerLights) {
			dimLight.setPowerLevel(1.0);
		}
	}
	
	public void turnOffAllLights() {
		for (BinaryLight binLight : binaryLights) {
			binLight.setPowerStatus(false);
		}
		for (DimmerLight dimLight : dimmerLights) {
			dimLight.setPowerLevel(0.0);
		}
	}
	
	public void turnOnLightsWherePeopleIn() {
		for (PresenceSensor sensor : presenceSensors) {
			if (sensor.getSensedPresence()) {
				//dobbiamo prendere la zona in cui ci troviamo, trovare le luci di quella zona ed accenderle
				String detectorLocation = (String) sensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
					// get the related binary lights
					List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
					List<DimmerLight> sameLocationDimmerLights = getDimmerLightFromLocation(detectorLocation);
					
					for (BinaryLight binLight : binaryLights) {
						if (sameLocationLigths.contains(binLight))
							binLight.setPowerStatus(true);
						//else
						//	binLight.setPowerStatus(false);
					}
					for (DimmerLight dimLight : dimmerLights) {
						if (sameLocationDimmerLights.contains(dimLight))
							dimLight.setPowerLevel(1.0);
						//else
						//	dimLight.setPowerLevel(0.0);
					}
				}
			}
		}
	}
	
	public void turnOnSirens() {
		for (Siren s : sirens) {			
			s.setPropertyValue("siren.status", true);
			//bisognerebbe aggiungere qualcosa da far suonare?
		}
	}
	
	//Creiamo un metodo che verrà chiamato ogni volta che il manager imposta le variabili
	//Il metodo deve capire se accendere/attivare/disattivare l'allarme
	@Override
	public void actualizeValues() {
		//Attivo l'allarme (cioè accendo tutte le luci e faccio suonare le sirene) quando sono presenti
		//persone non autorizzate e non c'è nessuno di autorizzato
		if (!AuthorizedPeopleIn && UnauthorizedPeopleIn && AlarmTrigger) {
			//accendo tutte le luci
			this.turnOnAllLights();
			this.turnOnSirens();	
		}else if (AuthorizedPeopleIn) {
			//accendo solo le luci delle stanze in cui ci sono persone
			this.turnOnLightsWherePeopleIn();
		}else {
			//spengo tutte le luci se non c'è nessuno
			this.turnOffAllLights();
		}
	}

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
	public void setAuthorizedPeopleIn(boolean AuthorizedPeopleIn) {
		this.AuthorizedPeopleIn = AuthorizedPeopleIn;
	}

	@Override
	public boolean getAuthorizedPeopleIn() {
		return this.AuthorizedPeopleIn;
	}

	@Override
	public void setUnauthorizedPeopleIn(boolean UnauthorizedPeopleIn) {
		this.UnauthorizedPeopleIn = UnauthorizedPeopleIn;
	}

	@Override
	public boolean getUnauthorizedPeopleIn() {
		return this.UnauthorizedPeopleIn;
	}

	@Override
	public void setAlarmTrigger(boolean AlarmTrigger) {
		this.AlarmTrigger = AlarmTrigger;
	}

	@Override
	public boolean getAlarmTrigger() {
		return this.AlarmTrigger;
	}

}
