package edu.nyu.pqs.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The {@code Graph} class represents an undirected graph with edge labels 
 * of type String.
 * <p>
 * The {@code Graph} class takes a generic parameter V that represents the
 * value of a vertex.
 * <p>
 * Two vertices are connected by an edge that can be labeled with a String value.
 * <p>
 * Graphs can be traversed by iterating over each vertex in the graph in a 
 * Breadth First or Depth First search manner.
 *
 * @author Eric
 * @param <V> a generic parameter V that represents the
 * value of a vertex.
 */
public class Graph<V> {
  private Node root;
  private List<Node> vertices;
  private int vertexCount = 0;

  public Graph() {
    vertices = new ArrayList<Node>();
  }

  public Graph(V value) {
    if (value == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    }
    root = new Node(value);
    vertices = new ArrayList<Node>();
    vertices.add(root);
  }

  /**
   * Creates and returns an Iterator that traverses the graph through
   * a Breadth First Search. 
   * <p>
   * The Iterator provides hasNext() and next() methods. next() returns
   * a GraphVertex<V> object. 
   * The remove() method on Iterator does not perform any operations.
   * <p>
   * It is possible to return an Iterator for an empty graph, but
   * elements added afterwards will not be added to the Iterator.
   * <p>
   * Vertices added or removed from a graph after retrieving an Iterator will
   * only appear in the iteration if they are added or removed to/from a vertex
   * that has not yet been retrieved by a next() call.
   * However, caution is advised when adding or removing vertices during iteration
   * as it might cause unexpected behavior, especially when iterating over the same
   * graph with different iterators in different threads. 
   * A new Iterator should instead be retrieved after modifying the graph.
   * 
   * @return an Iterator object that iterates over GraphVertex<V> objects
   * via Breadth First Search.
   * @see GraphVertex<V>
   */
  public Iterator<GraphVertex<V>> BFSIterator() {
    return new BreadthFirstSearchIterator();
  }

  /**
   * Creates and returns an Iterator that traverses the graph through
   * a Depth First Search. 
   * <p>
   * The Iterator provides hasNext() and next() methods. next() returns
   * a GraphVertex<V> object. 
   * The remove() method on Iterator does not perform any operations.
   * <p>
   * It is possible to return an Iterator for an empty graph, but
   * elements added afterwards will not be added to the Iterator.
   * <p>
   * Vertices added or removed from a graph after retrieving an Iterator will
   * only appear in the iteration if they are added or removed to/from a vertex
   * that has not yet been retrieved by a next() call.
   * However, caution is advised when adding or removing vertices during iteration
   * as it might cause unexpected behavior, especially when iterating over the same
   * graph with different iterators in different threads.
   * A new Iterator should instead be retrieved
   * after modifying the graph.
   * 
   * @return an Iterator object that iterates over GraphVertex<V> objects
   * via Depth First Search.
   * @see GraphVertex<V>
   */
  public Iterator<GraphVertex<V>> DFSIterator() {
    return new DepthFirstSearchIterator();
  }

  private int getVisitedSize() {
    int largestIndex = Integer.MIN_VALUE;
    for (Node vertex : vertices) {
      if (vertex.index > largestIndex) {
        largestIndex = vertex.index;
      }
    }
    return largestIndex;
  }

  /**
   * A convenience method for adding the root vertex to an empty graph.
   * <p>
   * Vertices cannot have a null value.
   * <p>
   * Add root should not be called more than once on a graph. Use a 
   * {@code set} method instead.
   * 
   * @param rootValue the value of the root vertex.
   * @return true if root is added, false otherwise.
   * @throws IllegalArgumentException if {@code rootValue} is null or already
   * set.
   */
  public boolean addRoot(V rootValue) {
    if (root != null) {
      throw new IllegalArgumentException("Root vertex has already been assigned");	
    }
    if (rootValue == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    }
    root = new Node(rootValue);
    return vertices.add(root);
  }

  /**
   * One of two methods for adding a new vertex to the graph. The new vertex
   * will be added to the existing vertex at the provided index.
   * <p>
   * The index of a vertex is set internally and can be retrieved from the
   * {@code GraphVertex<V>} object.
   * 
   * @param index index of the existing graph vertex for the new vertex to be 
   * connected to. 
   * @param edgeLabel label for the edge that connects the two vertices.
   * Can be empty String or null.
   * @param newVertex value given to the new vertex.
   * @return true if the vertex was added to the graph. False otherwise.
   * @throws IllegalArgumentException if the index is negative or the new
   * vertex value is null.
   */
  public boolean addToIndex(int index, String edgeLabel, V newVertex) {
    if (index < 0) {
      throw new IllegalArgumentException("index cannot be negative");
    }
    if (newVertex == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    }
    for (Node vertex : vertices) {
      if (vertex.index == index) {
        Node newNode = new Node(newVertex);
        vertex.adjacentEdges.add(new InternalEdge(edgeLabel, newNode));
        newNode.adjacentEdges.add(new InternalEdge(edgeLabel, vertex));
        vertices.add(newNode);
        return true;
      }
    }
    return false;
  }

  /**
   * One of two methods for adding a new vertex to the graph. The new vertex
   * will be added to the existing vertex with the provided value.
   * 
   * @param existingVertex the value of the existing graph vertex for the new vertex to be 
   * connected to. 
   * @param edgeLabel label for the edge that connects the two vertices.
   * Can be empty String or null.
   * @param newVertex value given to the new vertex.
   * @return true if the vertex was added to the graph. False otherwise.
   * @throws IllegalArgumentException if the existing vertex is null or 
   * the new vertex value is null.
   */
  public boolean add(V existingVertex, String edgeLabel, V newVertex) {
    if (existingVertex == null || newVertex == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    } 
    for (Node vertex : vertices) {
      if (vertex.value.equals(existingVertex)) {
        Node newNode = new Node(newVertex);
        vertex.adjacentEdges.add(new InternalEdge(edgeLabel, newNode));
        newNode.adjacentEdges.add(new InternalEdge(edgeLabel, vertex));
        vertices.add(newNode);
        return true;
      }
    }
    return false;
  }

  /**
   * One of two methods provided for removing a vertex from the graph.
   * <p>
   * The provided vertex will be removed from the graph structure and from
   * the adjacency lists of all the vertices it was connected to.
   * 
   * @param vertex the value of the vertex to be removed.
   * @return true if the vertex was successfully removed. False otherwise.
   * @throws IllegalArgumentException if the vertex value provided is null.
   */
  public boolean remove(V vertex) {
    if (vertex == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    }
    for (Node v : vertices) {
      for (int i = 0; i < v.adjacentEdges.size(); i++) {
        if (vertex.equals(v.adjacentEdges.get(i).connectingVertex.value)) {
          v.adjacentEdges.remove(i);
          break;
        }
      }
    }
    return vertices.remove(vertex);
  }

  /**
   * One of two methods provided for removing a vertex from the graph.
   * <p>
   * The index of a vertex is set internally and can be retrieved from the
   * {@code GraphVertex<V>} object.
   * <p>
   * The vertex with the provided index will be removed from the graph 
   * structure and from the adjacency lists of all the vertices it 
   * was connected to.
   * 
   * @param index the index value of the vertex to be removed.
   * @return true if the vertex was successfully removed. False otherwise.
   * @throws IllegalArgumentException if the index value is negative.
   */
  public boolean removeAtIndex(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index cannot be negative");
    }
    V vertexToRemove;
    for (Node v : vertices) {
      if (v.index == index) {
        vertexToRemove = v.value;
        return remove(vertexToRemove);
      }
    }
    return false;
  }

  /**
   * One of two setter methods provided for modifying the value of a vertex.
   * 
   * @param existingVertex the current value of the vertex to be modified.
   * @param newVertex the new value of the vertex.
   * @return true if the vertex value is successfully modified. False otherwise.
   * @throws IllegalArgumentException if existingVertex or newVertex are null.
   */
  public boolean setVertex(V existingVertex, V newVertex) {
    if (existingVertex == null || newVertex == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    }
    for (Node vertex : vertices) {
      if (vertex.value.equals(existingVertex)) {
        vertex.value = newVertex;
        return true;
      }
    }
    return false;
  }

  /**
   * One of two setter methods provided for modifying the value of a vertex.
   * <p>
   * The index of a vertex is set internally and can be retrieved from the
   * {@code GraphVertex<V>} object.
   * 
   * @param index index value of vertex to be modified.
   * @param newVertex the new value of the vertex.
   * @return true if the vertex value is successfully modified. False otherwise.
   * @throws IllegalArgumentException if index is negative or newVertex is null.
   */
  public boolean setVertexAtIndex(int index, V newVertex) {
    if (index < 0) {
      throw new IllegalArgumentException("index cannot be negative");
    }
    if (newVertex == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    } 
    for (Node v : vertices) {
      if (v.index == index) {
        v.value = newVertex;
        return true;
      }
    }
    return false;  
  }

  /**
   * One of two setter methods provided for modifying the label of an edge.
   * <p>
   * It does not matter which vertex is passed to vertexA and which to vertexB
   * 
   * @param vertexA the value of one of the two adjacent edges.
   * @param vertexB the value of the other adjacent edge.
   * @param newLabel the new edge label
   * @return true if the edge label is successfully modified. False otherwise.
   * @throws IllegalArgumentException if vertexA or vertexB is null.
   */
  public boolean setEdge(V vertexA, V vertexB, String newLabel) {
    if (vertexA == null || vertexB == null) {
      throw new IllegalArgumentException("Graph vertices cannot have a null value");
    }
    boolean setA = false;
    boolean setB = false;
    for (Node vertex : vertices) {
      if (vertex.value.equals(vertexA)) {
        for (int i = 0; i < vertex.adjacentEdges.size(); i++) {
          if (vertexB.equals(vertex.adjacentEdges.get(i).connectingVertex.value)) {
            vertex.adjacentEdges.get(i).label = newLabel;
            setA = true;
            if (setA && setB) {
              return true;
            }
          }
        }
      }
      if (vertex.value.equals(vertexB)) {
        for (int i = 0; i < vertex.adjacentEdges.size(); i++) {
          if (vertexA.equals(vertex.adjacentEdges.get(i).connectingVertex.value)) {
            vertex.adjacentEdges.get(i).label = newLabel;
            setB = true;
            if (setA && setB) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * One of two setter methods provided for modifying the label of an edge.
   * <p>
   * The index of a vertex is set internally and can be retrieved from the
   * {@code GraphVertex<V>} object.
   * <p>
   * It does not matter which index is passed to indexA and which to indexB
   * 
   * @param indexA the index value of one of the two adjacent vertices.
   * @param indexB the index value of the other adjacent vertex.
   * @param newLabel the new edge label.
   * @return true if the edge label is successfully modified. False otherwise.
   * @throws IllegalArgumentException if either index is negative.
   */
  public boolean setEdgeAtIndex(int indexA, int indexB, String newLabel) {
    if (indexA < 0 || indexB < 0) {
      throw new IllegalArgumentException("index cannot be negative");
    }
    V vertexA = null;
    V vertexB = null;
    for (Node vertex : vertices) {
      if (vertex.index == indexA) {
        vertexA = vertex.value;
      }
      if (vertex.index == indexB) {
        vertexB = vertex.value;
      }
    }
    if (vertexA != null && vertexB != null) {
      return setEdge(vertexA, vertexB, newLabel);
    }
    return false;
  }

  /**
   * Internal method used for converting {@code Node} objects into 
   * {@code GraphVertex<V>} objects for external use.
   * 
   * @param vertex the Node to be converted.
   * @return {@code GraphVertex<V>} object for external use.
   */
  private GraphVertex<V> createVertex(Node vertex) {
    List<GraphEdge<V>> adjacentEdges = new ArrayList<GraphEdge<V>>();
    for (InternalEdge edge : vertex.adjacentEdges) {
      adjacentEdges.add(new GraphEdge<V>(edge.label, edge.connectingVertex.value));
    }
    return new GraphVertex<V>(vertex.value, vertex.index, adjacentEdges);
  }

  /**
   * The {@code Node} class is used internally for representing graph vertices.
   * It maintains a value, a list of its adjacent edges, and an index to identify
   * it in the graph.
   * <p>
   * Node cannot take a value of null.
   * 
   * @author Eric
   *
   */
  private class Node {
    private V value;
    private List<InternalEdge> adjacentEdges;
    private int index;

    private Node(V value) {
      if (value == null) {
        throw new NullPointerException();
      }
      this.value = value;
      adjacentEdges = new ArrayList<InternalEdge>();
      index = vertexCount++;
    }

    List<Node> getAdjacentVertices() {
      List<Node> adjacentVertices = new ArrayList<Node>();
      for (InternalEdge edge : adjacentEdges) {
        adjacentVertices.add(edge.connectingVertex);
      }
      return adjacentVertices;
    }
  }

  /**
   * The {@code InternalEdge} class represents an edge object that connects (is incident to)
   * two vertices in the graph.
   * The edge maintains a label value and the other edge that it connects to.
   * <p>
   * Each node maintains a list of InternalEdges that specify the incident edges and
   * adjacent edges to the node.
   * 
   * @author Eric
   * @see Node
   */
  private class InternalEdge {
    private String label;
    private Node connectingVertex;

   private InternalEdge(String label, Node connectingVertex) {
     this.label = label;
     this.connectingVertex = connectingVertex;
   }
  }

  /**
   * The {@code DepthFirstSearchIterator} implements the Iterator Interface to 
   * traverse a graph through a Depth First Search.
   * <p>
   * The class provides hasNext() and next() methods. next() returns
   * a GraphVertex<V> object. 
   * The remove() method does not perform any operations.
   * <p>
   * It is possible to create a {@code DepthFirstSearchIterator} for 
   * an empty graph, but elements added afterwards will not be added 
   * to the Iterator.
   * <p>
   * Vertices added or removed from a graph after retrieving an 
   * {@code DepthFirstSearchIterator} will only appear in the iteration 
   * if they are added or removed to/from a vertex
   * that has not yet been retrieved by a next() call.
   * However, caution is advised when adding or removing vertices during iteration
   * as it might cause unexpected behavior, particularly in a multi-threaded program
   * in which each thread has a separate iterator operating on the same graph. 
   * A new Iterator should instead be retrieved after modifying the graph.
   *
   * @author Eric
   * @see GraphVertex<V>
   * @see Iterator
   */
  private class DepthFirstSearchIterator implements Iterator<GraphVertex<V>> {
    private List<Node> stack;
    private List<Boolean> visited;

    private DepthFirstSearchIterator() {
      stack = new ArrayList<Node>();
      if (root != null) {
        stack.add(root);
        int size = getVisitedSize();
        visited = new ArrayList<Boolean>(size);
        setVisited();
      }
    }

    private void setVisited() {
      if (visited != null) {
        for (int i = 0; i < visited.size(); i++) {
          visited.set(i, false);
        }
      }
    }

    @Override
    public boolean hasNext() {
      return (!stack.isEmpty());
    }

    @Override
    public GraphVertex<V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Node top;
      while (!stack.isEmpty()){
        top = stack.remove(stack.size() - 1);
        if (top.index > visited.size() - 1) {
          for (int i = visited.size(); i < top.index + 1; i++) {
            visited.add(false);
          }
        }
        if(top != null && !visited.get(top.index)) {
          visited.set(top.index, true);
          stack.addAll(top.getAdjacentVertices());
          return createVertex(top);
        }
      }
      return null;
    }

    @Override
	public void remove() {
      return;
    }
  }

  /**
   * The {@code BreadthFirstSearchIterator} implements the Iterator Interface to 
   * traverse a graph through a Breadth First Search.
   * <p>
   * The class provides hasNext() and next() methods. next() returns
   * a GraphVertex<V> object. 
   * The remove() method does not perform any operations.
   * <p>
   * It is possible to create a {@code BreadthFirstSearchIterator} for 
   * an empty graph, but elements added afterwards will not be added 
   * to the Iterator.
   * <p>
   * Vertices added or removed from a graph after retrieving an 
   * {@code BreadthFirstSearchIterator} will only appear in the iteration 
   * if they are added or removed to/from a vertex
   * that has not yet been retrieved by a next() call.
   * However, caution is advised when adding or removing vertices during iteration
   * as it might cause unexpected behavior, particularly in a multi-threaded program
   * in which each thread has a separate iterator operating on the same graph. 
   * A new Iterator should instead be retrieved after modifying the graph.
   *
   * @author Eric
   * @see GraphVertex<V>
   * @see Iterator
   */
  private class BreadthFirstSearchIterator implements Iterator<GraphVertex<V>> {
    private LinkedList<Node> queue;
    private List<Boolean> visited;

    private BreadthFirstSearchIterator() {
      queue = new LinkedList<Node>();
      if (root != null) {
        queue.add(root);
        int size = getVisitedSize();
        visited = new ArrayList<Boolean>(size);
        setVisited();
      }
    }

    private void setVisited() {
      if (visited != null) {
        for (int i = 0; i < visited.size(); i++) {
          visited.set(i, false);
        }
      }
    }

    @Override
    public boolean hasNext() {
      return (!queue.isEmpty());
    }

    @Override
    public GraphVertex<V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Node u;
      while (!queue.isEmpty()){
    	u = queue.removeFirst();
        if (u.index > visited.size() - 1) {
          for (int i = visited.size(); i < u.index + 1; i++) {
            visited.add(false);
          }
        }
        if(u != null &&  !visited.get(u.index)) {
          visited.set(u.index, true);
          queue.addAll(u.getAdjacentVertices());
          return createVertex(u);
        }
      }
      return null;
    }

    @Override
    public void remove() {
      return;
    }
  }
}