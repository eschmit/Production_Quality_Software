package edu.nyu.pqs.collections;

/**
 * This class is a concrete implementation of the Predicate<V> interface.
 * It takes a single Predicates<V> as a parameter and tests that a value
 * does not satisfy the predicate condition.
 * 
 * @author Eric
 * @param <V> The generic parameter to be tested against the predicate.
 */
public class NotPredicate<V> implements Predicate<V> {
  Predicate<V> predicate;

  public NotPredicate(Predicate<V> predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean accept(V value) {
    return !predicate.accept(value);
  }
}