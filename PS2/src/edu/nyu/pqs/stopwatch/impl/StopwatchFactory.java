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
    for (Stopwatch stopwatch : viewOfList) {
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
   * @throws NullPointerException if {@code Stopwatch} is null.
   */
  public static boolean remove(Stopwatch stopwatch) {
    if (stopwatch == null) {
      throw new NullPointerException("stopwatch cannot be null.");
    }
    synchronized(stopwatchesLock) {
      return stopwatches.remove(stopwatch);
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
   * and a toString implementation that provides a string representation of the stopwatches
   * lap times in milliseconds.
   * @see Stopwatch
   * @author Eric
   *
   */
  private static class BasicStopwatch implements Stopwatch {
    private final String id;
    private List<Long> lapTimes;
    private long lapStartTime;
    private enum stopwatchStates { RUNNING, STOPPED }
    private enum Actions { START, STOP, LAP, RESET }
    private stopwatchStates currentState = stopwatchStates.STOPPED;
    private Actions previousAction;
    private boolean previousActionsStopStart = false;
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
      synchronized(actionLock) {
        if (previousAction == Actions.STOP) {
          previousActionsStopStart = true;
          previousAction = Actions.START;
        }
      }
      synchronized(lapTimeLock) {
        lapStartTime = System.nanoTime();
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
        lapStartTime = currentNanoTime;
      }
      boolean previousActionsStopStartCopy;
      synchronized(actionLock) {
        previousActionsStopStartCopy = previousActionsStopStart;    
        previousAction = Actions.LAP;
      }
      /* Calculate lap time. Convert to milliseconds */
      long elapsedTime =  (currentNanoTime - lapStartTimeCopy)/1000000;
      if (previousActionsStopStartCopy) {
        synchronized(lapTimesListLock) {
          long totalTime = lapTimes.get(lapTimes.size() - 1 ) + elapsedTime;
          lapTimes.set(lapTimes.size() - 1, totalTime);
        }
        synchronized(actionLock) {
          previousActionsStopStart = false;	
        }
      } else {
        synchronized(lapTimesListLock) {
          /* Autobox long to Long */
          lapTimes.add(elapsedTime);
        }
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
        stopwatchBuilder.append("ms");
        stopwatchBuilder.append("\n");
        counter++;
      }
      return stopwatchBuilder.toString();  
    }
    
  }
  
}
