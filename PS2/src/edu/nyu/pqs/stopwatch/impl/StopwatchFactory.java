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
    /* Get view of list to limit time in lock */   
    List<Stopwatch> viewOfList = getStopwatches();
    for (Stopwatch stopwatch: viewOfList) {
      if (idToCheck.equals(stopwatch.getId())) {
        return false;	
      }
    }
    return true;
  }
  
  /**
   * Removes the specified {@code Stopwatch} object reference 
   * from the list of stopwatch objects, if it is present.
   * @param stopwatch the {@code Stopwatch} object to be removed to the list of stopwatches.
   * @return true if the stopwatch is successfully removed; false otherwise.
   * @throws IllegalArgumentException if {@code Stopwatch} is null.
   * @see removeAll
   */
  
  public static boolean remove(Stopwatch stopwatch) {
    if(stopwatch == null){
      throw new IllegalArgumentException("stopwatch cannot be null.");
    }
    /* Get view of list to limit time in lock */   
    List<Stopwatch> viewOfList = getStopwatches();
    int indexOfElement = -1;
    for (int i = 0; i < viewOfList.size(); i++) {
      if (stopwatch.equals(viewOfList.get(i))) {
        indexOfElement = i;
        break;
      }
    }
    /* stopwatch not in list. */
    if (indexOfElement == -1) {
      return false;
    } else {
      synchronized(stopwatchesLock) {
        stopwatches.remove(indexOfElement);
      }
      return true;
    }
  }
  
  /**
   * Returns an unmodifiable view of the list of all created stopwatches
   * @return an unmodifiable view of the List of all created Stopwatch objects.  
   * Returns an empty List if no Stopwatches have been created.
   */
  
  public static List<Stopwatch> getStopwatches() {
    /*Return unmodifiable view */
    synchronized(stopwatchesLock) {
      return Collections.unmodifiableList(stopwatches);
    }
  }
  
  /**
   * The {@code BasicStopwatch} class represents a stopwatch object with a unique id.
   * <p>
   * {@code BasicStopwatch} provides no accessor methods apart from {@code getId()}.
   * <p>
   * {@code BasicStopwatch} includes methods to start, stop, reset, or record a lap
   * and to compare two stopwatch objects using {@code equals} or {@code compareTo}.
   * @see Stopwatch
   * @author Eric
   *
   */
  
  private static class BasicStopwatch implements Stopwatch, Comparable<Stopwatch> {
    private final String id;
    /* Lazily initialized, cached hashcode */
    private volatile int hashCode;
    private List<Long> lapTimes;
    private long lapStartTime; 
    private long previousLapStartTime;
    private enum stopwatchStates { RUNNING, STOPPED }
    private enum Actions { START, STOP, LAP, RESET }
    private stopwatchStates currentState = stopwatchStates.STOPPED;
    private Actions previousAction;
    private Object lapTimesListLock;
    private Object lapTimeLock;
    private Object actionLock;
    private Object stateLock;
    
    private BasicStopwatch(String id) {
      this.id = id;
      this.lapTimes = new ArrayList<Long>();
      this.lapTimesListLock = new Object();
      this.lapTimeLock = new Object();
      this.actionLock = new Object();
      this.stateLock = new Object();
    }
    
    /**
     * Returns the Id of this stopwatch
     * @return the Id of this stopwatch.  Will never be empty or null.
     */
    public String getId() {
      /* String immutable. Don't need Defensive Copy */  
      return id;	
    }

    /**
     * Starts the stopwatch.
     * @throws IllegalStateException when the stopwatch is already running
     */
    public void start() {
      synchronized(stateLock) {
        if (currentState == stopwatchStates.RUNNING) {
          throw new IllegalStateException("stopwatch already running");	
        }
        currentState = stopwatchStates.RUNNING;
      }
      /* Store previous action in order to release lock */
      boolean previousActionWasStop = false;
      synchronized(actionLock) {
        if (previousAction == Actions.STOP) {
          previousActionWasStop = true;
          previousAction = Actions.START;
        }
      }
      if (previousActionWasStop) {
        synchronized(lapTimeLock) {
          lapStartTime = previousLapStartTime;
        }
        synchronized(lapTimesListLock) {
          lapTimes.remove(lapTimes.size() - 1);
        }
      } else {
        synchronized(lapTimeLock) {
          lapStartTime = System.nanoTime();
        }
      }
    }

    /**
     * Stores the time elapsed since the last time lap() was called
     * or since start() was called if this is the first lap.
     * Lap times are calculated in nanoseconds for accuracy and converted to 
     * milliseconds for storage in the lap times list.
     * @throws IllegalStateException when the stopwatch isn't running.
     */
    
    public void lap() {
      synchronized(stateLock) {
        if (currentState == stopwatchStates.STOPPED) {
          throw new IllegalStateException("stopwatch not currently running");	
        }
      }
      long currentNanoTime = System.nanoTime();
      /* Store copy to release lock */
      long lapStartTimeCopy;
      synchronized(lapTimeLock) {
        lapStartTimeCopy = lapStartTime;
        previousLapStartTime = lapStartTime;
        lapStartTime = currentNanoTime;
      }
      /* Calculate lap time. Convert to milliseconds */
      long elapsedTime =  (currentNanoTime - lapStartTimeCopy)/1000000;
      synchronized(lapTimesListLock) {
        /* Autobox long to Long */
        lapTimes.add(elapsedTime);
      }
      synchronized(actionLock) {
        previousAction = Actions.LAP;  
      }
    }

    /**
     * Stops the stopwatch (and records one final lap).
     * @throws IllegalStateException when the stopwatch isn't running.
     */
    
    public void stop() {
      lap();
      synchronized(stateLock) {
        currentState = stopwatchStates.STOPPED;	  
      }
      synchronized(actionLock) {
        previousAction = Actions.STOP;
      }
    }

    /**
     * Resets the stopwatch.  If the stopwatch is running, this method stops the
     * watch and resets it.  This also clears all recorded laps.
     */
    
    public void reset() {
      /* Store state in order to release lock */
      boolean stopwatchIsRunning = false;
      synchronized(stateLock) {
        if (currentState == stopwatchStates.RUNNING) { 
          stopwatchIsRunning = true;
          currentState = stopwatchStates.STOPPED;
        }
      }
      if (stopwatchIsRunning) {
        synchronized(lapTimeLock) {
          previousLapStartTime = 0L;
          lapStartTime = 0L;
        }
        synchronized(lapTimesListLock) {
          lapTimes.clear();
        }
        synchronized(actionLock) {
          previousAction = Actions.RESET;
        }
      }
    }

    /**
     * Returns a copy of the list of lap times (in milliseconds).  This method can be called at
     * any time and will not throw an exception.
     * @return a list of recorded lap times or an empty list.
     */
    
    public List<Long> getLapTimes() {
      synchronized(lapTimesListLock) {
        if (lapTimes.isEmpty()) {
          /*Return same empty list */
          return Collections.emptyList();	
        } else {
          /*Return deep copy of lapTimes */
          return new ArrayList<Long>(lapTimes);
        }
      }
    }
    
    /**
     * Used for internal retrieval of lapTimes list in order to 
     * limit time spent in lock and avoid making a copy.
     * @return unmodifiable view of lap times list.
     */
    
    private List<Long> privateGetLapTimes() {
      synchronized(lapTimesListLock) {
        if (lapTimes.isEmpty()) {
          /*Return same empty list */
          return Collections.emptyList();	
        } else {
          return Collections.unmodifiableList(lapTimes);
        }
      }	
    }
    
    /**
     * Compare the sum of the lap times of this stopwatch with the specified stopwatch.
     * Two stopwatches can only be compared if the size of their lists of lap times is equal. 
     * @param the Stopwatch object to be compared with this one
     * @return  negative integer, zero, or a positive integer as this object is less than, equal to, 
     * or greater than the specified object.
     * @throws IllegalArgumentException if {@code stopwatch} is null.
     */
    
    public int compareTo(Stopwatch stopwatch) {
      if (stopwatch == null) {
        throw new IllegalArgumentException("stopwatch cannot be null.");   	  
      }
      /* getLapTimes is synchronized */
      if (this.privateGetLapTimes().size() != stopwatch.getLapTimes().size()) {
        throw new IllegalArgumentException("List sizes must be equal to compare");
      }
      /* Long because list is of type Long */
      Long sumThis = 0L;
      Long sumToCompare = 0L;
      List<Long> lapTimesView = privateGetLapTimes();
      for (Long lap: lapTimesView ) {
        sumThis += lap; 
      }
      /* Copy of lapTimes. getLapTimes is synchronized */
      for (Long lap: stopwatch.getLapTimes()) {
        sumToCompare += lap; 
      }
      /* True if both lists are empty */
      return sumThis.compareTo(sumToCompare);			
    }
    
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;	
      }
      if (!(o instanceof BasicStopwatch)) {
        return false;
      }
      /* Extra check: If two stopwatches have equal ids they should reference
       * the same stopwatch object.
       */
      BasicStopwatch stopwatch = (BasicStopwatch)o;
      return this.id.equals(stopwatch.getId());
    }
    
    @Override
    public int hashCode() {
      /* hashcode calculated from final field id */
      int result = hashCode;
      if (result == 0) {
        result = 17;
        result = 31 * result + Integer.parseInt(id);
        hashCode = result;
      }
      return result;
    }
    
    @Override
    public String toString() {
      List<Long> lapTimesView = privateGetLapTimes();
      StringBuilder stopwatchBuilder = new StringBuilder();
      int counter = 1;
      for (Long lapTime : lapTimesView) {
        stopwatchBuilder.append("Lap ");
        stopwatchBuilder.append(counter);
        stopwatchBuilder.append(": ");
        stopwatchBuilder.append(lapTime);
        stopwatchBuilder.append("\n");
        counter++;
      }
      return stopwatchBuilder.toString();  
    }
    
    
  }
  
}
