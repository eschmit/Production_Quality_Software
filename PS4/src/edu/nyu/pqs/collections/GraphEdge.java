package edu.nyu.pqs.collections;

/**
 * The {@code GraphEdge<V>} class represents the external representation
 * of a {@code Graph} edge. 
 * 
 * The class maintains an edge label, and
 * the vertex value of one of the vertices incident to the edge.
 * <p>
 * The {@code GraphVertex<V>} class maintains an adjacency list of
 * {@code GraphEdge<V>} objects that specify the incident edges 
 * adjacent edges to the vertex.
 * <p>
 * The {@code GraphVertex<V>} class has the following methods to retrieve
 * the vertex properties: getVertex(), getIndex(), and getAdjacentEdges().
 * 
 * @author Eric
 * @param <V> The generic value of the connecting {@code GraphVertex<V>}.
 * This value cannot be null.
 * @see GraphVertex<V>
 */
public class GraphEdge<V> {
  private String label;
  private V connectingVertex;
  
  public GraphEdge(String label, V connectingVertex) {
    this.label = label;
    this.connectingVertex = connectingVertex;
  }
  
  public V getconnectingVertex() {
    return connectingVertex;
  }
  
  public String getLabel() {
    return label;	  
  }
}
