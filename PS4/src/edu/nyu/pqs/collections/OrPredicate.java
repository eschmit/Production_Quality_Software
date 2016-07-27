package edu.nyu.pqs.collections;

/**
 * This class is a concrete implementation of the Predicate<V> interface.
 * It takes two Predicates<V> as parameters and test that a value
 * satisfies either condition.
 * 
 * @author Eric
 * @param <V> The generic parameter to be tested against the two provided
 * predicates.
 */
public class OrPredicate<V> implements Predicate<V> {
  Predicate<V> predicate1;
  Predicate<V> predicate2;

  public OrPredicate(Predicate<V> predicate1, Predicate<V> predicate2) {
    this.predicate1 = predicate1;
    this.predicate2 = predicate2;
  }

  @Override
  public boolean accept(V value) {
    return predicate1.accept(value) || predicate2.accept(value);
  }
}