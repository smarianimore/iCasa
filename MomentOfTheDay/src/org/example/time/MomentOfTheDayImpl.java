package org.example.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.LocalTime;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;

public class MomentOfTheDayImpl implements MomentOfTheDayService, PeriodicRunnable {

	MomentOfTheDay currentMomentOfTheDay = MomentOfTheDay.NIGHT;
	List<MomentOfTheDayListener> momentOfTheDayListener = new ArrayList<MomentOfTheDayListener>(); //lista dei listener di tipo MomentOfTheDay

	@Override
	public synchronized void run() {
		System.out.println("Run called!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(momentOfTheDayListener);

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
		return TimeUnit.SECONDS;
	}

	@Override
	public MomentOfTheDay getMomentOfTheDay() {
		int hour = LocalTime.now().getHour();
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
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("MomentOfTheDay starting...");
	}

}
