package org.example.follow.me.manager;

public enum EnergyGoal {
	LOW(100d), MEDIUM(200d), HIGH(1000d);
	 
    /**
     * The corresponding maximum energy in watt
     */
    private double maximumEnergyInRoom;
 
    /**
     * get the maximum energy consumption in each room
     * 
     * @return the energy in watt
     */
    public double getMaximumEnergyInRoom() {
        return maximumEnergyInRoom;
    }
    
    public static EnergyGoal getCorrespondingEnergyGoal(double energy) {
    	if (100 <= energy && energy < 200)
    		return EnergyGoal.LOW;
    	else if (200 <= energy && energy < 1000)
    		return EnergyGoal.MEDIUM;
    	else if (1000 <= energy)
    		return EnergyGoal.HIGH;
    	else
    		return null;
    }
 
    private EnergyGoal(double powerInWatt) {
        maximumEnergyInRoom = powerInWatt;
    }
}
