package edu.nyu.pqs.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code FilterIterator<V>} class is a decorator for the Iterator
 * interface that allows traversal of a {@code Graph} 
 * and only returns elements that satisfy a provided condition
 * via a {@code Predicate<V>}.
 * 
 * @author Eric
 * @param <V> The generic parameter contained within {@code GraphVertex}
 * elements.
 * @see Iterator
 * @see GraphVertex<V>
 * @see Graph
 */
public class FilterIterator<V> implements Iterator<GraphVertex<V>> {
  private Iterator<GraphVertex<V>> iterator;
  private Predicate<V> predicate;
  private GraphVertex<V> nextItem;

  public FilterIterator(Iterator<GraphVertex<V>> iterator, Predicate<V> predicate) {
    this.iterator = iterator;
    this.predicate = predicate;
  }

  @Override
  public boolean hasNext() {
    if (nextItem != null) {
      return true;
    }
    while (iterator.hasNext()) {
      nextItem = iterator.next();
      V nextValue = nextItem.getVertex();
      if (predicate.accept(nextValue)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public GraphVertex<V> next() {
    if (nextItem != null) {
      GraphVertex<V> vertex = nextItem;
      nextItem = null;
      return vertex;
    }
    if (hasNext()) {
      return next();
    }
    throw new NoSuchElementException();
  }

  @Override
  public void remove() {
    return;
  }
}