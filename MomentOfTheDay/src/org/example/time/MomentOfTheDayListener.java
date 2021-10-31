package org.example.time;

/**
 * The listener interface for receiving momentOfTheDay events.
 * The class that is interested in processing a momentOfTheDay
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * MomentOfTheDayService <code>register<code> method. When
 * the momentOfTheDay event occurs, that object's appropriate
 * method (<code>momentOfTheDayHasChanged</code>) is invoked.
 * 
 * When the listener is leaving, it must unregister.
 * 
 */
public interface MomentOfTheDayListener {
 
    /**
     * Notify the listener that moment of the day has changed.
     * 
     * @param newMomentOfTheDay
     *            the new moment of the day
     */
    void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay);
}
