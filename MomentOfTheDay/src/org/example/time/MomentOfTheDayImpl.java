package org.example.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.clockservice.Clock;

public class MomentOfTheDayImpl implements MomentOfTheDayService, PeriodicRunnable {

	MomentOfTheDay currentMomentOfTheDay;
	List<MomentOfTheDayListener> momentOfTheDayListener = new ArrayList<MomentOfTheDayListener>(); //lista dei listener di tipo MomentOfTheDay
	
	/** Field for clockService dependency */
	private Clock clockService;

	@Override
	public synchronized void run() {
		// TODO : do something to check the current time of the day and see if
		// it has changed
		//Bisogna fare il getMomentOfTheDay e compararlo al currentMomentOfTheDay
		//Se il momento cambia, chiamiamo la momentOfTheDayHasChanged la quale lo userà per fare qualcosa
		MomentOfTheDay newMomentOfTheDay = this.getMomentOfTheDay();
		if (newMomentOfTheDay != currentMomentOfTheDay) {
			System.out.println("Moment changed to: " + newMomentOfTheDay);
			for (MomentOfTheDayListener moment : momentOfTheDayListener) {
				moment.momentOfTheDayHasChanged(newMomentOfTheDay);
			}
			currentMomentOfTheDay = newMomentOfTheDay;
		}

	}

	//getPeriod e getUnit vengono utilizzati dal sistema per chiamare con un certo intervallo il run() e quindi verificare
	//se il momento del giorno è cambiato rispetto al precedente (IPOTESI: se il periodo è cambiato, si fa qualcosa sfruttando
	//uno o più listener
	@Override
	public long getPeriod() {
		return 1;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.HOURS;
	}
	
	@Override
	public MomentOfTheDay getMomentOfTheDay() {
		//Get the current time in ms from the clock service via the clock service dependency
		long currentMillisTime = clockService.currentTimeMillis();

		//Instantiate a Calendar object and get the hours of the day
		//Calendar.HOUR -> 0-12 hours format
		//Calendar.HOUR_OF_DAY -> 0-24 hours format
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentMillisTime);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		System.out.println("Current hour: " + hour);
		
		return MomentOfTheDay.getCorrespondingMoment(hour);
	}

	@Override
	public synchronized void register(MomentOfTheDayListener listener) {
		momentOfTheDayListener.add(listener);

	}

	@Override
	public synchronized void unregister(MomentOfTheDayListener listener) {
		momentOfTheDayListener.remove(listener);

	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("MomentOfTheDay stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("MomentOfTheDay starting...");
	}

}
