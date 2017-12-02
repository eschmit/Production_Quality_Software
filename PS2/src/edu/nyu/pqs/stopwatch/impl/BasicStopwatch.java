package edu.nyu.pqs.stopwatch.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nyu.pqs.stopwatch.api.Stopwatch;


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
public class BasicStopwatch implements Stopwatch {
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
  
  BasicStopwatch(String id) {
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
	List<Long> lapTimesCopy;
    synchronized(lapTimesListLock) {
      if (lapTimes.isEmpty()) {
        /*Return same empty list */
        lapTimesCopy = Collections.emptyList();
      } else {
        /*Return deep copy of lapTimes */
        //return new ArrayList<Long>(lapTimes);
    	lapTimesCopy = new ArrayList<Long>(lapTimes.size());
      	for (Long value : lapTimes) {
      	  lapTimesCopy.add(value.longValue());
      	}
      }
    }
    return lapTimesCopy;
  }
      
  @Override
  public String toString() {
    List<Long> lapTimesView = getLapTimes();
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
