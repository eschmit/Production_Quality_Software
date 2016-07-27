package edu.nyu.pqs.collections;

import java.util.List;

/**
 * The {@code GraphVertex<V>} class represents the external representation
 * of a {@code Graph} vertex. The class maintains a vertex value, an 
 * adjacency list of {@code GraphEdge<V>} objects and an index to
 * identify the vertex in the graph. 
 * <p>
 * The {@code GraphVertex<V>} class has the following methods to retrieve
 * the vertex properties: getVertex(), getIndex(), and getAdjacentEdges().
 * 
 * @author Eric
 * @param <V> The generic value the {@code GraphVertex<V>} maintains.
 * This value cannot be null.
 * @see GraphVertex<V>
 * @see GraphEdge<V>
 */
public class GraphVertex<V> {
  private V value;
  private List<GraphEdge<V>> adjacentEdges;
  private int index;

  public GraphVertex(V value, int index, List<GraphEdge<V>> adjacentEdges) {
    if (value == null) {
      throw new NullPointerException();
    }
    this.value = value;
    this.index = index;
    this.adjacentEdges = adjacentEdges; //dont need defensive copy
  }
    
  public V getVertex() {
    return value;
  }
    
  public int getIndex() {
    return index;
  }
  
  public List<GraphEdge<V>> getAdjacentEdges() {
    return adjacentEdges;
  }
 
}
