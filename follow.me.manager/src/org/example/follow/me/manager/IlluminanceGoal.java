package org.example.follow.me.manager;

public enum IlluminanceGoal {
    /** The goal associated with soft illuminance. */
    SOFT(1, 100d),
    /** The goal associated with medium illuminance. */
    MEDIUM(2, 2750d),
    /** The goal associated with full illuminance. */
    FULL(3, 4000d);
 
    /** The number of lights to turn on. */
    private int numberOfLightsToTurnOn;
    
    private double illuminance;
 
    /**
     * Gets the number of lights to turn On.
     * 
     * @return the number of lights to turn On.
     */
    public int getNumberOfLightsToTurnOn() {
        return numberOfLightsToTurnOn;
    }
    
    public double getIlluminance() {
    	return illuminance;
    }
    
    public static IlluminanceGoal getCorrespondingIlluminanceFromNumLights(int numLights) {
    	if (numLights == 1)
    		return IlluminanceGoal.SOFT;
    	else if (numLights == 2)
    		return IlluminanceGoal.MEDIUM;
    	else if (numLights == 3)
    		return IlluminanceGoal.FULL;
    	else
    		return null;
    }
    
    public static IlluminanceGoal getCorrespondingIlluminanceFromValue(double illuminance){
    	if (100d <= illuminance && illuminance < 2750d)
    		return IlluminanceGoal.SOFT;
    	else if (2750d <= illuminance && illuminance < 4000d)
    		return IlluminanceGoal.MEDIUM;
    	else if (4000d <= illuminance)
    		return IlluminanceGoal.FULL;
    	else
    		return null;
    }

   
    /**
     * Instantiates a new illuminance goal.
     * 
     * @param numberOfLightsToTurnOn
     *            the number of lights to turn on.
     */
    private IlluminanceGoal(int numberOfLightsToTurnOn, double illuminance) {
        this.numberOfLightsToTurnOn = numberOfLightsToTurnOn;
        this.illuminance = illuminance;
    }
}
