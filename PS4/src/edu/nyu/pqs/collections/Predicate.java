package edu.nyu.pqs.collections;

/**
 * This interface imposes a restriction on the values returned by the
 * {@code next()} method when iterating over a data structure 
 * that implements Iterator.
 * 
 * @author Eric
 * @param <V> The generic parameter that will be tested against a condition.
 */
public interface Predicate<V> {
  boolean accept(V value);
}