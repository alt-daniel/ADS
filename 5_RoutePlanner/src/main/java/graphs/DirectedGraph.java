package graphs;

import route_planner.Junction;
import route_planner.Road;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectedGraph<V extends DGVertex<E>, E extends DGEdge<V>> {

    private Map<String, V> vertices = new HashMap<>();

    /**
     * representation invariants:
     * 1.  all vertices in the graph are unique by their implementation of the getId() method
     * 2.  all edges in the graph reference vertices from and to which are true members of the vertices map
     * (i.e. by true object instance equality == and not just by identity equality from the getId() method)
     * 3.  all edges of a vertex are outgoing edges, i.e. FOR ALL e in v.edges: e.from == v
     **/

    public DirectedGraph() {
    }

    public Collection<V> getVertices() {
        return this.vertices.values();
    }

    /**
     * finds the vertex in the graph identified by the given id
     *
     * @param id
     * @return the vertex that matches the given id
     * return null if none of the vertices matches the id
     */
    public V getVertexById(String id) {
        return this.vertices.get(id);
    }


    /**
     * Adds newVertex to the graph, if not yet present and in a way that maintains the representation invariants.
     * If (a duplicate of) newVertex (with the same id) already exists in the graph,
     * nothing will be added, and the existing duplicate will be kept and returned.
     *
     * @param newVertex
     * @return the duplicate of newVertex with the same id that already existed in the graph,
     * or newVertex itself if it has been added.
     */
    public V addOrGetVertex(V newVertex) {
        if (!this.vertices.containsKey(newVertex.getId())) {
            this.vertices.put(newVertex.getId(), newVertex);
            return newVertex;
        } else return getVertexById(newVertex.getId());
        // a proper vertex shall be returned at all times
    }

    /**
     * Adds all newVertices to the graph, which are not present yet and and in a way that maintains the representation invariants.
     *
     * @param newVertices an array of vertices to be added, provided as variable length argument list
     * @return the number of vertices that actually have been added.
     */
    public int addVertices(V... newVertices) {
        int count = 0;
        for (V v : newVertices) {
            if (v == this.addOrGetVertex(v)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Adds newEdge to the graph, if not yet present and in a way that maintains the representation invariants:
     * If any of the newEdge.from or newEdge.to vertices does not yet exist in the graph, it is added now.
     * If newEdge does not exist yet in the edges list of the newEdge.from vertex, it is added now,
     * otherwise no change is made to that list.
     *
     * @param newEdge the new edge to be added in the edges list of newEdge.from
     * @return the duplicate of newEdge that already existed in the graph
     * or newEdge itselves if it just has been added.
     * @throws IllegalArgumentException if newEdge.from or newEdge.to are duplicate vertices that have not
     *                                  been added to the graph yet have the same id as another vertex in the graph
     */
    public E addOrGetEdge(E newEdge) {
        // Step 1

        this.vertices.putIfAbsent(newEdge.getFrom().getId(), newEdge.getFrom());
        this.vertices.putIfAbsent(newEdge.getTo().getId(), newEdge.getTo());

        // Step 2
        if (!newEdge.getFrom().getEdges().contains(newEdge)) {
            newEdge.getFrom().getEdges().add(newEdge);
        }

        // Step 3
        if (getVertexById(newEdge.getFrom().getId()) == newEdge.getFrom()) {
            // Return existing edge
            return getVertexById(newEdge.getFrom().getId()).getEdges().stream()
                    .filter(e -> e.getTo() == newEdge.getTo())
                    .findFirst()
                    .orElse(newEdge);
        } else if (getVertexById(newEdge.getFrom().getId()) != newEdge.getFrom() &&
                getVertexById(newEdge.getTo().getId()) != newEdge.getTo()) {
            throw new IllegalArgumentException();
        } else return newEdge;
    }

    /**
     * Adds all newEdges to the graph, which are not present yet and in a way that maintains the representation invariants.
     *
     * @param newEdges an array of vertices to be added, provides as variable length argument list
     * @return the number of edges that actually have been added.
     */
    public int addEdges(E... newEdges) {
        int count = 0;
        for (E e : newEdges) {
            if (e == this.addOrGetEdge(e)) {
                count++;
            }
        }

        return count;
    }

    /**
     * @return the total number of vertices in the graph
     */
    public int getNumVertices() {
        return this.vertices.size();
    }

    /**
     * @return the total number of edges in the graph
     */
    public int getNumEdges() {
        return this.getVertices().stream().mapToInt(e -> e.getEdges().size()).sum();
    }

    /**
     * Clean-up unconnected vertices in the graph
     */
    public void removeUnconnectedVertices() {
        Set<V> unconnected = new HashSet<>();

        this.getVertices().stream().filter(v -> v.getEdges().size() == 0).forEach(unconnected::add);
        this.getVertices().stream().flatMap(v -> v.getEdges().stream().map(E::getTo)).forEach(unconnected::remove);
        unconnected.stream().map(V::getId).forEach(this.vertices::remove);
    }

    /**
     * represents a path of connected vertices and edges in the graph
     */
    public class DGPath {
        private V start = null;
        private LinkedList<E> edges = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. The edges are connected by vertices, i.e. FOR ALL i: 0 < i < edges.length: edges[i].from == edges[i-1].to
         * 2. The path begins at vertex == start
         * 3. if edges is empty, the path also ends at vertex == start
         * otherwise edges[0].from == start and the path continues along edges[i].to for all 0 <= i < edges.length
         **/

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%f Length=%d Visited=%d (",
                            this.totalWeight, 1 + this.edges.size(), this.visited.size()));
            sb.append(start.getId());
            for (E e : edges) {
                sb.append(", " + e.getTo().getId());
            }
            sb.append(")");
            return sb.toString();
        }

        public V getStart() {
            return start;
        }

        public LinkedList<E> getEdges() {
            return edges;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public Set<V> getVisited() {
            return visited;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * The path.totalWeight should indicate the number of edges in the result path
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath depthFirstSearch(String startId, String targetId) {

        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;

        if (dfsRecursive(start, path, target)) {
            return path;
        } else return null;

        // no path found, graph was not connected ???
    }

    boolean dfsRecursive(V current, DGPath path, V target) {
//        System.out.println("\n\ncurrent = " + current.getId() + ", \npath = " + path + ", \ntarget = " + target.getId());
        path.visited.add(current);
        if (current.equals(target)) {
            return true;
        }

        for (E destination : current.getEdges()) {
            if (!path.visited.contains(destination.getTo())) {
                path.edges.add(destination);
                if (dfsRecursive(destination.getTo(), path, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Uses a breadth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * The path.totalWeight should indicate the number of edges in the result path
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath breadthFirstSearch(String startId, String targetId) {

        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;

        breadthFirstSearch(start, target, path);
        // no path found, graph was not connected ???
        if (path.visited.contains(target)) {
            return path;
        } else {
            return null;
        }
    }


    public void breadthFirstSearch(V start, V target, DGPath path) {
        if (start == target) return;

        Queue<V> fifoQueue = new LinkedList<>();
        Map<V,E> visitedEdgeTo = new HashMap<>();

        fifoQueue.add(start);
        visitedEdgeTo.put(start, null);

        while (fifoQueue.size() > 0) {
            V next = fifoQueue.poll();
            for (E e : next.getEdges()) {
                V neighbour = e.getTo();
                path.visited.add(neighbour);
                if (neighbour == target) {
                    E previous = e;
                    while (previous != null) {
                        path.edges.addFirst(previous);
                        previous = visitedEdgeTo.get(previous.getFrom());
                    }
                    return;
                } else if (!visitedEdgeTo.containsKey(neighbour)) {
                    visitedEdgeTo.put(neighbour,e);
                    fifoQueue.offer(neighbour);
                }
            }
        }

    }

    // helper class to register the state of a vertex in dijkstra shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private class DSPNode implements Comparable<DSPNode> {
        public V vertex;                // the graph vertex that is concerned with this DSPNode
        public E fromEdge = null;        // the edge from the predecessor's vertex to this node's vertex
        public boolean marked = false;  // indicates DSP processing has been marked complete
        public double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path to this node's vertex

        public DSPNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(DSPNode dspv) {
            return Double.compare(this.weightSumTo, dspv.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     * Uses a minimum distance heuristic from any vertex to the target
     * in order to reduce the number of visited vertices during the search
     *
     * @param startId
     * @param targetId
     * @param weightMapper provides a function, by which the weight of an edge can be retrieved or calculated
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath dijkstraShortestPath(String startId, String targetId,
                                       Function<E, Double> weightMapper) {

        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        if (start == null || target == null) return null;

        // initialise the result path of the search
        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;

        // keep track of the DSP status of all visited nodes
        // you may choose a different approach of tracking progress of the algorith, if you wish
        Map<V, DSPNode> progressData = new HashMap<>();

        // initialise the progress of the start node
        DSPNode nextDspNode = new DSPNode(start);
        nextDspNode.weightSumTo = 0.0;
        progressData.put(start, nextDspNode);

        while (nextDspNode != null && nextDspNode.vertex != target) {
            // Explore current node
            Set<E> currentEdges = nextDspNode.vertex.getEdges();
            // Check each vertex connected to current node
            for (E currentEdge: currentEdges) {
                V targetVertex = currentEdge.getTo();
                path.visited.add(targetVertex);
                // If vertex is not in progressData
                if (!progressData.containsKey(targetVertex)) {
                    DSPNode newNodeTo = new DSPNode(targetVertex);
                    newNodeTo.weightSumTo = weightMapper.apply(currentEdge) + nextDspNode.weightSumTo;
                    newNodeTo.fromEdge = currentEdge;
                    progressData.put(targetVertex, newNodeTo);
                // If vertex is in progressData, update values if it's a path with a lower weight
                } else {
                    DSPNode existingNode = progressData.get(targetVertex);
                    double existingNodeWeight = existingNode.weightSumTo;
                    double newNodeWeight = weightMapper.apply(currentEdge) +  nextDspNode.weightSumTo;
                    if (existingNodeWeight > newNodeWeight) {
                        existingNode.weightSumTo = newNodeWeight;
                        existingNode.fromEdge = currentEdge;
                    }
                }
            }
            // Mark processed node as visited
            nextDspNode.marked = true;
            // Find next node to visit
            nextDspNode = progressData.values().stream()
                    .filter(e -> !e.marked)
                    .min(DSPNode::compareTo)
                    .orElse(null);
        }

        // Fill path with results
        Deque<E> dq = new ArrayDeque<>();

        if (progressData.containsKey(target)) {
            DSPNode currentNode = progressData.get(target);
            while (currentNode != null && currentNode != progressData.get(start)) {
                dq.addFirst(currentNode.fromEdge);
                V previousVertex = currentNode.fromEdge.getFrom();
                currentNode = progressData.get(previousVertex);
            }
        }

        if (path.visited.contains(target)) {
            path.edges.addAll(dq);
            path.totalWeight = progressData.get(target).weightSumTo;
            return path;
        }
        return null;
    }


    // helper class to register the state of a vertex in A* shortest path algorithm
    private class ASNode extends DSPNode{

        // Minimum weight + value from minimumweightestimator
        public double estimatedSum;

        private ASNode(V vertex) {
            super(vertex);
        }

        public int compareTo(ASNode asn) {
            return Double.compare(estimatedSum, asn.estimatedSum);
        }
    }


    /**
     * Calculates the edge-weighted shortest path from start to target
     * Uses a minimum distance heuristic from any vertex to the target
     * in order to reduce the number of visited vertices during the search
     *
     * @param startId
     * @param targetId
     * @param weightMapper           provides a function, by which the weight of an edge can be retrieved or calculated
     * @param minimumWeightEstimator provides a function, by which a lower bound of the cumulative weight
     *                               between two vertices can be calculated.
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath aStarShortestPath(String startId, String targetId,
                                    Function<E, Double> weightMapper,
                                    BiFunction<V, V, Double> minimumWeightEstimator) {

        V start = this.getVertexById(startId);
        V target = this.getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();
        path.start = start;
        path.visited.add(start);

        // easy target
        if (start == target) return path;

        // TODO apply the A* algorithm to find shortest path from start to target.
        //  take dijkstra's solution as the starting point and enhance with heuristic functionality
        //  register all visited vertices while going, for statistical purposes

        Map<V, ASNode> progressData = new HashMap<>();

        // initialise the progress of the start node
        ASNode nextAsNode = new ASNode(start);
        nextAsNode.weightSumTo = 0.0;
        nextAsNode.estimatedSum = nextAsNode.weightSumTo + minimumWeightEstimator.apply(nextAsNode.vertex, target);
        progressData.put(start, nextAsNode);

        while (nextAsNode != null && nextAsNode.vertex != target) {
            Set<E> currentEdges = nextAsNode.vertex.getEdges();
            for (E currentEdge: currentEdges) {
                V targetVertex = currentEdge.getTo();
                if (!progressData.containsKey(targetVertex)) {
                    path.visited.add(targetVertex);
                    ASNode candidateNode = new ASNode(targetVertex);
                    candidateNode.fromEdge = currentEdge;
                    candidateNode.weightSumTo = weightMapper.apply(currentEdge) + nextAsNode.weightSumTo;
                    candidateNode.estimatedSum = minimumWeightEstimator.apply(candidateNode.vertex, target) + candidateNode.weightSumTo;
                    progressData.put(targetVertex, candidateNode);
                } else {
                    ASNode candidateNode = progressData.get(targetVertex);
                    double oldEstimatedSum = candidateNode.estimatedSum;
                    double newEstimatedSum = minimumWeightEstimator.apply(targetVertex, target) + nextAsNode.weightSumTo;
                    if (newEstimatedSum < oldEstimatedSum) {
                        candidateNode.weightSumTo = weightMapper.apply(currentEdge) + nextAsNode.weightSumTo;
                        candidateNode.estimatedSum = newEstimatedSum;
                        candidateNode.fromEdge = currentEdge;
                    }
                }
            }
            nextAsNode.marked = true;

            nextAsNode = progressData.values().stream()
                    .filter(e-> !e.marked)
                    .min(ASNode::compareTo)
                    .orElse(null);

        }

        Deque<E> dq = new ArrayDeque<>();

        if (progressData.containsKey(target)) {
            ASNode currentNode = progressData.get(target);
            while (currentNode != null && currentNode != progressData.get(start)) {
                dq.addFirst(currentNode.fromEdge);
                V previousVertex = currentNode.fromEdge.getFrom();
                currentNode = progressData.get(previousVertex);
            }
        }

        if (path.visited.contains(target)) {
            path.edges.addAll(dq);
            path.totalWeight = progressData.get(target).weightSumTo;
            return path;
        }
        // no path found, graph was not connected ???
        return null;
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     *
     * @param startId
     * @param targetId
     * @param weightMapper provides a function by which the weight of an edge can be retrieved or calculated
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath dijkstraShortestPathByAStar(String startId, String targetId,
                                              Function<E, Double> weightMapper) {

        return aStarShortestPath(startId, targetId,
                weightMapper,
                new BiFunction<V, V, Double>() {
                    @Override
                    public Double apply(V v1, V v2) {
                        return 0.0;
                    }
                }
        );
    }

    @Override
    public String toString() {
        return this.getVertices().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n  ", "{ ", "\n}"));
    }
}
