package iCasa.alarm.system.configuration;

public interface AlarmSystemConfiguration {
	public void setAuthorizedPeopleIn(boolean AuthorizedPeopleIn);
	public boolean getAuthorizedPeopleIn();
	
	public void setUnauthorizedPeopleIn(boolean UnauthorizedPeopleIn);
	public boolean getUnauthorizedPeopleIn();
	
	public void setAlarmTrigger(boolean AlarmTrigger);
	public boolean getAlarmTrigger();
	
	public void actualizeValues();
}
