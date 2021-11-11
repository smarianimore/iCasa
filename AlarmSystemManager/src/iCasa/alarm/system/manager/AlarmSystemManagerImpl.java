package iCasa.alarm.system.manager;

import iCasa.alarm.system.configuration.AlarmSystemConfiguration;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;

import fr.liglab.adele.icasa.service.location.PersonLocationService;

public class AlarmSystemManagerImpl implements Runnable {

	/** Field for alarmSystem dependency */
	private AlarmSystemConfiguration alarmSystem;
	/** Field for personLocationService dependency */
	private PersonLocationService personLocationService;

	//Se non funziona forse bisogna fornire la PeriodicRunnable come servizio
	@Override
	public void run() {

		while (true) {
			try {
				System.out.println("Managing variables...");
				this.managingVariables();
				Thread.sleep(300);
			} catch (InterruptedException | MalformedURLException | JSONException e) {
				System.out.println("Thread exception!");
				e.printStackTrace();
			}
		}
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Alarm System Manager stopping...");
	}

	/** Component Lifecycle Method 
	 * @throws JSONException 
	 * @throws MalformedURLException */
	public void start() throws MalformedURLException, JSONException {
		System.out.println("Alarm System Manager starting...");

		//Faccio una prima inizializzazione del sistema
		alarmSystem.setUnauthorizedPeopleIn(false);
		alarmSystem.setAlarmTrigger(false);
		alarmSystem.setAuthorizedPeopleIn(false);
		this.managingVariables();

		Thread t = new Thread(this);
		t.start();

	}

	public void managingVariables() throws MalformedURLException, JSONException {

		//Prima di procedere dobbiamo ottenere la lista delle zone tramite GET
		APIcommunications http_req = new APIcommunications();
		List<String> zoneList = new ArrayList<String>();
		zoneList = http_req.getZonesList();

		//Una volta ottenuta la lista delle zone, con il servizio di localizzazione delle persone
		//andiamo a prendere la lista di chi è nell'appartamento
		Set<String> peopleInZone;
		List<String> peopleInFlat = new ArrayList<String>();

		if(!zoneList.isEmpty()) {
			for (String zone : zoneList) {
				peopleInZone = personLocationService.getPersonInZone(zone);
				peopleInFlat.addAll(peopleInZone);
			}
		}

		//Avendo la lista delle persone autorizzate, conto le persone autorizzate e non, presenti nell'appartamento
		List<String> authorizedPeople = new ArrayList<String>();
		authorizedPeople.add("Giorgio");
		authorizedPeople.add("Giulia");
		authorizedPeople.add("Mario");
		int numAuthorizedPeoplePresent = 0;
		int numUnauthorizedPeoplePresent = 0;
		for (String person : peopleInFlat) {
			if (authorizedPeople.contains(person)) {
				numAuthorizedPeoplePresent++;
			} else if (!authorizedPeople.contains(person)) {
				numUnauthorizedPeoplePresent++;
			}
		}

		//Se ci sono persone non autorizzate e nessuno degli autorizzati è presente allora scatta l'allarme
		//Caso 1: non autorizzati: SI & autorizzati: NO	-> ALLARME ATTIVO/TUTTE LE LUCI ACCESE
		if (numUnauthorizedPeoplePresent > 0 && numAuthorizedPeoplePresent == 0) {
			alarmSystem.setUnauthorizedPeopleIn(true);
			alarmSystem.setAlarmTrigger(true);
			alarmSystem.setAuthorizedPeopleIn(false);
		}
		//Caso 2: non autorizzati: SI/NO & autorizzati: SI	-> ALLARME NON ATTIVO/SI ACCENDONO SOLO LE LUCI DELLE STANZE DOVE CI SONO LE PERSONE
		else if (numAuthorizedPeoplePresent > 0) {
			alarmSystem.setUnauthorizedPeopleIn(false);
			alarmSystem.setAlarmTrigger(false);
			alarmSystem.setAuthorizedPeopleIn(true);

		}
		//Caso 3: non autorizzati: NO & autorizzati: NO	-> ALLARME SPENTO/LUCI SPENTE
		else {
			alarmSystem.setUnauthorizedPeopleIn(false);
			alarmSystem.setAlarmTrigger(false);
			alarmSystem.setAuthorizedPeopleIn(false);
		}

		//System.out.println("Variables setted");

		alarmSystem.actualizeValues();

	}

}
