package org.example.time;

public enum MomentOfTheDay {
    MORNING(6), AFTERNOON(12), EVENING(18), NIGHT(24);
	 
    /**
     * Gets the moment of the day corresponding to the hour.
     * 
     * @param hour
     *            the given hour
     * @return the corresponding moment of the day
     */
    static MomentOfTheDay getCorrespondingMoment(int hour) {
    	if (0 <= hour && hour < 6)
    		return MomentOfTheDay.NIGHT;
    	else if (6 <= hour && hour < 12)
        	return MomentOfTheDay.MORNING;
        else if (12 <= hour && hour < 18)
        	return MomentOfTheDay.AFTERNOON;
        else if (18 <= hour && hour < 24)
        	return MomentOfTheDay.EVENING;
        else if (24 <= hour)
        	return MomentOfTheDay.NIGHT;
        else
        	return null;
    }
 
    /**
     * The hour when the moment start.
     */
    private final int startHour;
 
    /**
     * Build a new moment of the day :
     * 
     * @param startHour
     *            when the moment start.
     */
    MomentOfTheDay(int startHour) {
        assert ((0 <= startHour) && (startHour <= 24));
        this.startHour = startHour;
    }
}
