package edu.nyu.pqs.stopwatch.impl;

import edu.nyu.pqs.stopwatch.api.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The StopwatchFactory is a thread-safe factory class for Stopwatch objects.
 * It maintains references to all created Stopwatch objects and provides a
 * convenient method for getting an unmodifiable view of those list of objects.
 * @see BasicStopwatch
 * @author Eric
 * 
 */
public class StopwatchFactory {
  private static List<Stopwatch> stopwatches = new ArrayList<Stopwatch>();
  private static Object stopwatchesLock = new Object();
  
  /* Prevent Instantiation */	
  private StopwatchFactory() { }

  /**
   * Creates and returns a new Stopwatch object. Adds the newly created Stopwatch 
   * object to a list of all created Stopwatch objects. 
   * @param id The identifier of the new object
   * @return The new Stopwatch object
   * @throws IllegalArgumentException if <code>id</code> is empty, null, or
   *     already taken.
   */
  public static Stopwatch getStopwatch(String id) {
    /* String immutable. Don't need Defensive Copy */
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("id field cannot be null or empty String");
    } else if (!(uniqueId(id))) {
      throw new IllegalArgumentException("Provided id is already taken");
    }
    BasicStopwatch stopwatch = new BasicStopwatch(id);
    synchronized(stopwatchesLock) {
      stopwatches.add(stopwatch);
    }
    return stopwatch;
  }
  
  private static boolean uniqueId(String idToCheck) {  
	boolean isUnique = false;
	synchronized(stopwatchesLock) {
      for (Stopwatch stopwatch : stopwatches) {
        if (idToCheck.equals(stopwatch.getId())) {
          isUnique = false;
          break;
      }
      isUnique = true;
    }
	}
    return isUnique;
  }
  
  /**
   * Removes the specified {@code Stopwatch} object reference 
   * from the list of stopwatch objects, if it is present.
   * @param stopwatch the {@code Stopwatch} object to be removed to the list of stopwatches.
   * @return true if the stopwatch is successfully removed; false otherwise.
   * @throws NullPointerException if {@code Stopwatch} is null.
   */
  public static boolean remove(Stopwatch stopwatch) {
    if (stopwatch == null) {
      throw new NullPointerException("stopwatch cannot be null.");
    }
    boolean removed;
    synchronized(stopwatchesLock) {
      removed = stopwatches.remove(stopwatch);
    }
    return removed;
  }
  
  /**
   * Returns a shallow copy of the list of all created stopwatches
   * @return a shallow copy of the List of all created Stopwatch objects.  
   * Returns an empty List if no Stopwatches have been created.
   */
  public static List<Stopwatch> getStopwatches() {
    /*Return copy */
	List<Stopwatch> stopwatchesCopy;
    synchronized(stopwatchesLock) {
    	stopwatchesCopy =  new ArrayList<Stopwatch>(stopwatches);//shallow copy
    }
    return stopwatchesCopy;
  }

  
}
