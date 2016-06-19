package edu.nyu.pqs.stopwatch.impl;

import edu.nyu.pqs.stopwatch.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nyu.pqs.stopwatch.api.Stopwatch;

/**
 * The StopwatchFactory is a thread-safe factory class for Stopwatch objects.
 * It maintains references to all created Stopwatch objects and provides a
 * convenient method for getting a list of those objects.
 *
 */
public class StopwatchFactory {
  private static List<Stopwatch> stopwatches = new ArrayList<Stopwatch>();
  private static Object stopwatchesLock;
  /* Prevent Instantiation */	
  private StopwatchFactory() { } 

  /**
   * Creates and returns a new Stopwatch object
   * @param id The identifier of the new object
   * @return The new Stopwatch object
   * @throws IllegalArgumentException if <code>id</code> is empty, null, or
   *     already taken.
   */
  public static Stopwatch getStopwatch(String id) {
    /* String immutable. Don't need Defensive Copy */
    BasicStopwatch stopwatch = new BasicStopwatch(id);
    synchronized(stopwatchesLock) {
      stopwatches.add(stopwatch);
    }
    return stopwatch;
  }

  /**
   * Returns a list of all created stopwatches
   * @return a List of al creates Stopwatch objects.  Returns an empty
   * list if no Stopwatches have been created.
   */
  public static List<Stopwatch> getStopwatches() {
    /*Return unmodifiable view */
	synchronized(stopwatchesLock) {
      return Collections.unmodifiableList(stopwatches); //BasicStopwatch.stopwatches
    }
  }
  
  private static class BasicStopwatch implements Stopwatch {
	//private static List<Stopwatch> stopwatches = new ArrayList<Stopwatch>();
    private final String id;	
	private List<Long> lapTimes;
	private long lapStartTime; 
	private long previousLapStartTime;
    private enum stopwatchStates { RUNNING, STOPPED }
    private enum Actions { START, STOP, LAP, RESET }
    private stopwatchStates currentState = stopwatchStates.STOPPED;
    private Actions previousAction;
    //private Object startStopLock;
    private Object lapTimesListLock;
    private Object lapTimeLock;
    private Object actionLock;
    private Object stateLock;
    //separate locks for each variable
    	
    private BasicStopwatch(String id) {
      if (id == null || id.isEmpty() || !(uniqueId(id))) {
        throw new IllegalArgumentException("id field cannot be null or empty String");
      }
      //make sure the id doesnt already exist
      //loop over stopwatches do id.equals
	  this.id = id;
	  this.lapTimes = new ArrayList<Long>();
	  this.lapTimesListLock = new Object();
	  this.lapTimeLock = new Object();
	  this.actionLock = new Object();
	  this.stateLock = new Object();
    }
    
    private boolean uniqueId(String idToCheck) {
      synchronized(stopwatchesLock) {
        for (Stopwatch stopwatch: stopwatches) {
          if (idToCheck.equals(stopwatch.getId())) {
            return false;	
          }
        }
      }
      return true;
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
     * @throws IllegalStateException thrown when the stopwatch is already running
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
     * @throws IllegalStateException thrown when the stopwatch isn't running
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
     * @throws IllegalStateException thrown when the stopwatch isn't running
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
        long currentTime = System.nanoTime();
        synchronized(lapTimeLock) {
          previousLapStartTime = currentTime;
    	  lapStartTime = currentTime;
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
     * Returns a list of lap times (in milliseconds).  This method can be called at
     * any time and will not throw an exception.
     * @return a list of recorded lap times or an empty list.
     */
    public List<Long> getLapTimes() {
    	synchronized(lapTimesListLock) {
    	if (lapTimes.isEmpty()) {
          /*Return same empty list */
    	  return Collections.emptyList();	
    	} else {
    	  /*Return copy of lapTimes */
    	  return new ArrayList<Long>(lapTimes);
    	}
    	}
    }
    
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;	
      }
      if (!(o instanceof BasicStopwatch)) {
        return false;
      }
      BasicStopwatch stopwatch = (BasicStopwatch)o;
      synchronized(lapTimesListLock) {
      if(lapTimes.size() != stopwatch.lapTimes.size()) {
        return false;	  
      }
      for (int i = 0; i < lapTimes.size(); ++i) {
          if (!(lapTimes.get(i).equals(stopwatch.lapTimes.get(i)))) {
            return false;	  
          }
      }
      return true;
      }
    }
    
    @Override
    public int hashCode() {
      int result = 17;
      synchronized(lapTimesListLock) {
      for (Long lapTime : lapTimes) {
        long lapTimeUnboxed = lapTime.longValue();
        result = 31 * result + (int) (lapTimeUnboxed ^ (lapTimeUnboxed >>> 32));	  
      }
      return result;
      }
    }
    
    @Override
    public String toString() {
      synchronized(lapTimesListLock) {
      StringBuilder stopwatchBuilder = new StringBuilder();
      int counter = 1;
      for (Long lapTime : lapTimes) {
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
}
