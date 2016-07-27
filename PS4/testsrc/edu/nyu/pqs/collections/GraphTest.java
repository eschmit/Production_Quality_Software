package edu.nyu.pqs.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class GraphTest {
  Graph<String> graph;
  Predicate<String> predicate1;
  Predicate<String> predicate2;

  @Before
  public void setUp() {
    graph = new Graph<String>();
    predicate1 = new Predicate<String>() {
      @Override
      public boolean accept(String s) {
        return s.length() > 6;
      }
    };
    predicate2 = new Predicate<String>() {
      @Override
      public boolean accept(String s) {
        return s.length() < 8;
      }
    };
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGraph_nullParameter() {
    Graph<String> graph2 = new Graph<String>(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRoot_nullParameter() {
    graph.addRoot(null);
  }

  @Test
  public void testAddRoot_emptyString() {
    assertTrue(graph.addRoot(""));
  }

  @Test
  public void testAddRoot_emptyGraph() {
    assertTrue(graph.addRoot("New York"));
  }

  @Test
  public void testRemove_emptyGraph() {
    assertFalse(graph.remove("New York"));  
  }

  @Test
  public void testRemoveAtIndex_emptyGraph() {
    assertFalse(graph.removeAtIndex(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRoot_rootPreviouslySet() {
    assertTrue(graph.addRoot("New York"));
    graph.addRoot("Boston");  
  }

  @Test
  public void testRemove_nonExistentVertex() {
    assertTrue(graph.addRoot("New York"));
    assertFalse(graph.remove("Boston"));
  }

  @Test
  public void testRemoveAtIndex_nonExistentVertex() {
    assertTrue(graph.addRoot("New York"));
    assertFalse(graph.removeAtIndex(10));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveAtIndex_negativeIndex() {
    assertTrue(graph.addRoot("New York"));
    graph.removeAtIndex(-1);
  }

  @Test
  public void testAddToIndex_nonExistentIndex() {
    assertTrue(graph.addRoot("New York"));
    assertFalse(graph.addToIndex(10, "", ""));
  }

  @Test
  public void testAdd_nonExistentVertex() {
    assertTrue(graph.addRoot("New York"));
    assertFalse(graph.add("Boston", "", ""));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddToIndex_negativeIndex() {
    assertTrue(graph.addRoot("New York"));
    graph.addToIndex(-1, "", "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAdd_nullVertex() {
    assertTrue(graph.addRoot("New York"));
    assertFalse(graph.add("New York", "", null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAdd_toNullVertex() {
    assertTrue(graph.addRoot("New York"));
    assertFalse(graph.add(null, "", ""));
  }

  @Test
  public void testAdd_nullAndEmptyLabel() {
    assertTrue(graph.addRoot("New York"));
    assertTrue(graph.add("New York", null, ""));
    assertTrue(graph.add("New York", "", ""));
  }

  @Test
  public void testAdd_emptyString() {
    assertTrue(graph.addRoot(""));
  }

  @Test
  public void testAdd_longString() {
    assertTrue(graph.addRoot(
        "Llanfair­pwllgwyn­gyllgo­gerychwyrn­drobwll­llanty­silio­gogogoch"));
  }

  @Test
  public void testSetVertex() {
    createMiniGraph();
    assertTrue(graph.setVertex("New York", "testCity"));
    testVertexSettersHelper();
  }

  @Test
  public void testSetVertexAtIndex() {
    createMiniGraph();
    assertTrue(graph.setVertexAtIndex(0, "testCity"));
    testVertexSettersHelper();
  }

  private void createMiniGraph() {
    assertTrue(graph.addRoot("New York"));
    List<String> labels = createLabelsOrValues("50", "75");
    List<String> cities = createLabelsOrValues("Philadelphia", "Boston");
    addAdjacents("New York", labels, cities);
  }

  private void testVertexSettersHelper() {
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    assertTrue(iterator.hasNext());
    GraphVertex<String> testCity = iterator.next();
    assertEquals("testCity", testCity.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> philadelphia = iterator.next();
    List<GraphEdge<String>> edges = philadelphia.getAdjacentEdges();
    assertEquals("testCity", edges.get(0).getconnectingVertex());
    GraphVertex<String> boston = iterator.next();
    List<GraphEdge<String>> edgesB = boston.getAdjacentEdges();
    assertEquals("testCity", edgesB.get(0).getconnectingVertex());
  }

  @Test
  public void testSetEdge() {
    createMiniGraph();
    assertTrue(graph.setEdge("New York", "Philadelphia", "60"));
    testEdgeSettersHelper();
  }

  @Test
  public void testSetEdgeAtIndex() {
    createMiniGraph();
    assertTrue(graph.setEdgeAtIndex(0, 1, "60"));
    testEdgeSettersHelper();
  }

  private void testEdgeSettersHelper() {
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    assertTrue(iterator.hasNext());
    GraphVertex<String> newYork = iterator.next();
    assertEquals("New York", newYork.getVertex());
    List<GraphEdge<String>> edges = newYork.getAdjacentEdges();
    assertEquals("60", edges.get(0).getLabel());
    assertTrue(iterator.hasNext());
    GraphVertex<String> philadelphia = iterator.next();
    assertEquals("Philadelphia", philadelphia.getVertex());
    List<GraphEdge<String>> edgesPhila = philadelphia.getAdjacentEdges();
    assertEquals("60", edgesPhila.get(0).getLabel());
  }

  @Test
  public void testGraph_IntegerType() {
    Graph<Integer> graph2 = new Graph<Integer>();
    graph2.addRoot(10);
  }

  @Test
  public void testGraph_withDuplicateVertex() {
    assertTrue(graph.addRoot("Brad"));
    List<String> labels = createLabelsOrValues("sibling", "friend");
    List<String> names = createLabelsOrValues("Eric", "Chet");
    addAdjacents("Brad", labels, names);
    names = createLabelsOrValues("Eric", "Josh");
    labels = createLabelsOrValues("friend", "friend");
    addAdjacents("Chet", labels, names);
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    assertTrue(iterator.hasNext());
    GraphVertex<String> brad = iterator.next();
    assertEquals("Brad", brad.getVertex());
    List<GraphEdge<String>> edges = brad.getAdjacentEdges();
    assertEquals("sibling", edges.get(0).getLabel());
    assertEquals("Eric", edges.get(0).getconnectingVertex());
    assertTrue(iterator.hasNext());
    iterator.next();
    assertTrue(iterator.hasNext());
    GraphVertex<String> chet = iterator.next();
    assertEquals("Chet", chet.getVertex());
    List<GraphEdge<String>> edgesChet = chet.getAdjacentEdges();
    assertEquals("friend", edgesChet.get(1).getLabel());
    assertEquals("Eric", edgesChet.get(1).getconnectingVertex());
  }

  @Test
  public void testBFSIterator_emptyGraphHasNext() {
    assertFalse(graph.BFSIterator().hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void testBFSIterator_emptyGraphNext() {
    graph.BFSIterator().next();
  }

  @Test
  public void testDFSIterator_emptyGraphHasNext() {
    assertFalse(graph.DFSIterator().hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void testDFSIterator_emptyGraphNext() {
    graph.DFSIterator().next();
  }

  private void addAdjacents(String vertex, List<String> labels, 
      List<String> values) {
    if (labels.size() == values.size()) {
      for (int i = 0; i < labels.size(); i++) {
        graph.add(vertex, labels.get(i), values.get(i));
      }
    }
  }

  private List<String> createLabelsOrValues(String...labels) {
    List<String> l = new ArrayList<String>();
    for (String label : labels) {
      l.add(label);
    }
    return l;
  }

  @Test
  public void testGraph_adjacentEdges() {
    assertTrue(graph.addRoot("New York"));
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    assertTrue(iterator.hasNext());
    List<String> labels = createLabelsOrValues("50");
    List<String> cities = createLabelsOrValues("Philadelphia");
    addAdjacents("New York", labels, cities);
    GraphVertex<String> newYork = iterator.next();
    assertEquals("New York", newYork.getVertex());
    List<GraphEdge<String>> edges = newYork.getAdjacentEdges();
    assertEquals("50", edges.get(0).getLabel());
    assertEquals("Philadelphia", edges.get(0).getconnectingVertex());
  }

  private void createGraph() {
    assertTrue(graph.addRoot("New York"));
    List<String> labels = createLabelsOrValues("50", "75");
    List<String> cities = createLabelsOrValues("Philadelphia", "Boston");
    addAdjacents("New York", labels, cities);
    cities = createLabelsOrValues("DC", "Miami");
    addAdjacents("Philadelphia", labels, cities);
    labels = createLabelsOrValues("100");
    cities = createLabelsOrValues("Chicago");
    addAdjacents("Boston", labels, cities);
  }

  @Test
  public void testBFSIterator() {
    createGraph();
    BFSIteratorHelper();
  }

  private void BFSIteratorHelper() {
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    assertTrue(iterator.hasNext());
    GraphVertex<String> newYork = iterator.next();
    assertEquals("New York", newYork.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> philadelphia = iterator.next();
    assertEquals("Philadelphia", philadelphia.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> boston = iterator.next();
    assertEquals("Boston", boston.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> dc = iterator.next();
    assertEquals("DC", dc.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> miami = iterator.next();
    assertEquals("Miami", miami.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> chicago = iterator.next();
    assertEquals("Chicago", chicago.getVertex());
  }

  @Test
  public void testDFSIterator() {
    createGraph();
    DFSIteratorHelper();
  }

  @Test
  public void testDFSIterator_concurrent() {
    createGraph();
    new Thread(new Runnable() {
      @Override
      public void run() {
        DFSIteratorHelper();
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        DFSIteratorHelper();
      }
    }).start();
  }

  private void DFSIteratorHelper() {
    Iterator<GraphVertex<String>> iterator = graph.DFSIterator();
    GraphVertex<String> newYork = iterator.next();
    assertEquals("New York", newYork.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> boston = iterator.next();
    assertEquals("Boston", boston.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> chicago = iterator.next();
    assertEquals("Chicago", chicago.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> philadelphia = iterator.next();
    assertEquals("Philadelphia", philadelphia.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> miami = iterator.next();
    assertEquals("Miami", miami.getVertex());
    assertTrue(iterator.hasNext());
    GraphVertex<String> dc = iterator.next();
    assertEquals("DC", dc.getVertex());  
  }

  @Test
  public void testBFSIterator_concurrent() {
    createGraph();
    new Thread(new Runnable() {
      @Override
      public void run() {
        BFSIteratorHelper();
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        BFSIteratorHelper();
      }
    }).start(); 
  }

  @Test
  public void testDFSIterator_hasNext() {
    Iterator<GraphVertex<String>> iterator = graph.DFSIterator();
    assertFalse(iterator.hasNext());
    assertTrue(graph.addRoot("New York"));
    assertFalse(iterator.hasNext());
    iterator = graph.DFSIterator();
    assertTrue(iterator.hasNext());
    iterator.next();
    List<String> labels = createLabelsOrValues("50", "75");
    List<String> cities = createLabelsOrValues("Philadelphia", "Boston");
    addAdjacents("New York", labels, cities);
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testBFSIterator_hasNext() {
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    assertFalse(iterator.hasNext());
    assertTrue(graph.addRoot("New York"));
    assertFalse(iterator.hasNext());
    iterator = graph.BFSIterator();
    assertTrue(iterator.hasNext());
    iterator.next();
    List<String> labels = createLabelsOrValues("50", "75");
    List<String> cities = createLabelsOrValues("Philadelphia", "Boston");
    addAdjacents("New York", labels, cities);
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testFilterIterator_hasNextFalse() {
    assertTrue(graph.addRoot("DC"));
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    Iterator<GraphVertex<String>> filterIterator =
        new FilterIterator<String>(iterator, predicate1);
    assertFalse(filterIterator.hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void testFilterIterator_noNext() {
    assertTrue(graph.addRoot("DC"));
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    Iterator<GraphVertex<String>> filterIterator =
        new FilterIterator<String>(iterator, predicate1);
    filterIterator.next();
  }

  @Test
  public void testFilterIterator() {
    createGraph();
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    Iterator<GraphVertex<String>> filterIterator =
        new FilterIterator<String>(iterator, predicate1);
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> newYork = filterIterator.next();
    assertEquals("New York", newYork.getVertex());
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> philadelphia = filterIterator.next();
    assertEquals("Philadelphia", philadelphia.getVertex());
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> chicago = filterIterator.next();
    assertEquals("Chicago", chicago.getVertex());
  }

  @Test
  public void testFilterIterator_notPredicate() {
    createGraph();
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    Iterator<GraphVertex<String>> filterIterator =
        new FilterIterator<String>(iterator, 
        new NotPredicate<String>(predicate1));
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> boston = filterIterator.next();
    assertEquals("Boston", boston.getVertex());
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> dc = filterIterator.next();
    assertEquals("DC", dc.getVertex());
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> miami = filterIterator.next();
    assertEquals("Miami", miami.getVertex());
  }

  @Test
  public void testFilterIterator_andPredicate() {
    createGraph();
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    Iterator<GraphVertex<String>> filterIterator = new FilterIterator<String>(
        iterator, new AndPredicate<String>(predicate1, predicate2));
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> chicago = filterIterator.next();
    assertEquals("Chicago", chicago.getVertex());
  }

  @Test
  public void testFilterIterator_orPredicate() { 
    createGraph();
    Iterator<GraphVertex<String>> iterator = graph.BFSIterator();
    Iterator<GraphVertex<String>> filterIterator =
        new FilterIterator<String>(iterator,
        new OrPredicate<String>(predicate1, new Predicate<String>() {
          @Override
          public boolean accept(String s) {
            return s.length() < 3;
          }
        }));
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> newYork = filterIterator.next();
    assertEquals("New York", newYork.getVertex());
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> philadelphia = filterIterator.next();
    assertEquals("Philadelphia", philadelphia.getVertex());
    assertTrue(filterIterator.hasNext());
    GraphVertex<String> dc = filterIterator.next();
    assertEquals("DC", dc.getVertex());
  }

}
