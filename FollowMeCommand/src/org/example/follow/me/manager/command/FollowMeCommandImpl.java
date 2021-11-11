package org.example.follow.me.manager.command;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.example.follow.me.manager.EnergyGoal;
import org.example.follow.me.manager.FollowMeAdministration;
import org.example.follow.me.manager.IlluminanceGoal;
import org.example.time.MomentOfTheDayService;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "follow.me.mananger.command")
//Use the handler command and declare the command as a command provider. The
//namespace is used to prevent name collision.
@CommandProvider(namespace = "followme")

public class FollowMeCommandImpl {

	// Declare a dependency to a FollowMeAdministration service
	@Requires
	private FollowMeAdministration m_administrationService;

	//@Requires
	//private MomentOfTheDayService m_momentService;


	/**
	 * Felix shell command implementation to sets the illuminance preference.
	 *
	 * @param goal the new illuminance preference ("SOFT", "MEDIUM", "FULL")
	 */

	// Each command should start with a @Command annotation
	@Command
	public void setIlluminancePreference(String user, String goal) {
		// The targeted goal
		IlluminanceGoal illuminanceGoal;

		if (goal.equals("SOFT") || goal.equals("MEDIUM") || goal.equals("FULL")) {
			illuminanceGoal = IlluminanceGoal.valueOf(goal);

			//call the administration service to configure it :
			m_administrationService.setIlluminancePreference(illuminanceGoal, user);
		}else {
			System.out.println("Input error, insert one of the folowing values: SOFT, MEDIUM, FULL");
		}
	}

	@Command
	public void getIlluminancePreference(){
		System.out.println("The illuminance goal is ");
		System.out.println(m_administrationService.getIlluminancePreference());
	}

	@Command
	public void setEnergyPreference(String goal) {
		EnergyGoal energyGoal;

		if (goal.equals("LOW") || goal.equals("MEDIUM") || goal.equals("HIGH")) {
			energyGoal = EnergyGoal.valueOf(goal);

			//call the administration service to configure it :
			m_administrationService.setEnergySavingGoal(energyGoal);
		}else {
			System.out.println("Input error, insert one of the folowing values: LOW, MEDIUM, HIGH");
		}

	}

	@Command
	public void getEnergyPreference() {
		System.out.println("The energy goal is ");
		System.out.println(m_administrationService.getEnergyGoal());
	}

	//@Command
	//public void getMomentOfTheDay() {
	//	System.out.println(m_momentService.getMomentOfTheDay().name());
	//}

}