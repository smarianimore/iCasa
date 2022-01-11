package iCasa.devices.component.configuration;

import org.json.JSONException;
import org.json.JSONObject;

public interface SystemServiceConfiguration {
	/*
	 * Takes the snapshot of the system
	 * */
	public JSONObject takeSnapshot() throws JSONException;
	
	/*
	 * Method that actualizes the values of the variables
	 * */
	public void setValues();
	
	/*
	 * setters and getters for the threshold variables
	 * */
	public Double getOutdoorTemperatureThreshold();
	public void setOutdoorTemperatureThreshold(double outdoorTemperatureThreshold);
	public Double getIndoorTemperatureThreshold();
	public void setIndoorTemperatureThreshold(double indoorTemperatureThreshold);
	public Double getPowerConsumptionThreshold();
	public void setPowerConsumptionThreshold(double powerConsumptionThreshold);
	public Boolean getWindowOpened();
	public void setWindowOpened(boolean windowOpened);
	public Double getHeaterLevel();
	public void setHeaterLevel(double heaterLevel);
	public Double getCoolerLevel();
	public void setCoolerLevel(double coolerLevel);
	public Double getCOlevel();
	public void setCOlevel(double COlevel);
	public Double getCO2level();
	public void setCO2level(double CO2level);
	public Boolean getBtnStatus();
	public void setBtnStatus(boolean btnStatus);
	public Boolean getSwitcherStatus();
	public void setSwitcherStatus(boolean switcherStatus);
	
}
