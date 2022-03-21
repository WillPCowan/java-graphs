import org.json.JSONObject;

import java.util.*;

public class Graph {
    private HashMap<String, Node> nodes;
    private HashMap<Integer, Edge> edges;  // Use hash based on Node ids
    private HashMap<Node, HashMap<Node, Edge>> adjList; // Secondary edge datastructure to accelerate algos

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.adjList = new HashMap<>();
    }

    public boolean addNode(Node n) {
        if (nodes.containsKey(n.id)) return false;
        nodes.put(n.id, n);
        adjList.put(n, new HashMap<>());
        return true;
    }

    public void setNodes(HashMap<String, Node> nodes) {
        this.nodes = nodes;
    }

    public boolean addEdge(Node tail, Node head, JSONObject data) {
        // Abort if edge exists
        if (adjList.containsKey(tail) && adjList.get(tail).containsKey(head)) return false;

        // Create edge
        Edge e = new Edge(tail, head, data);

        // Save nodes if new
        if (!nodes.containsKey(e.tail.id)) addNode(e.tail);
        if (!nodes.containsKey(e.head.id)) addNode(e.head);

        // Update edge datastructures
        edges.put(e.hashCode(), e); // Add edge
//        if (!adjList.containsKey(e.tail)) adjList.put(e.tail, new HashMap<>()); // Add adjList entry if not present
        adjList.get(e.tail).put(e.head, e); // Add adjacent node (and ref the shared edge)
//        if (!adjList.containsKey(e.head)) adjList.put(e.head, new HashMap<>()); // Add adjList entry if not present

        return true;
    }

    public boolean addEdge(String tailId, String headId, JSONObject data) {
        Node tail = this.getNode(tailId);
        Node head = this.getNode(headId);
        return addEdge(tail, head, data);
    }

    // Accessors
    public Node getNode(String id) { return this.nodes.get(id); }
    public Edge getEdge(String tailId, String headId) { return this.edges.get(Objects.hash(tailId, headId)); }
    public HashMap<Node, HashMap<Node, Edge>> getAdjList() { return this.adjList; }


    // GRAPH ALGORITHMS ==============================================================================================

    /**
     *
     * @param src
     * @param dst
     * @return
     */
    public ArrayList<Node> getShortestPath(Node src, Node dst, String xLabel, String yLabel) {

        // Set-up datastructures for tracking
        HashMap<Node, Integer> gScores = new HashMap<>(); // Node distance from src (total hops or weighted hops)
        HashMap<Node, Double> fScores = new HashMap<>();  // Node distance from src + heuristic (e.g. euc distance)
        HashMap<Node, Node> previous = new HashMap<>();   // Node ancestor that first searched it

        // Create a priority queue based on fScores of unvisited nodes
        class NodeFScoreComparator implements Comparator<Node> {
            @Override
            public int compare(Node n1, Node n2) {
                return Double.compare(fScores.get(n1), fScores.get(n2));
            }
        }
        NodeFScoreComparator comparator = new NodeFScoreComparator();
        PriorityQueue<Node> fScorePQ = new PriorityQueue<>(comparator);

        // Initialise structures with src node
        gScores.put(src, 0);
        fScores.put(src, gScores.get(src) + euclidianDistance(src, dst, xLabel, yLabel));
        previous.put(src, null);
        fScorePQ.add(src);

        // Start loop to search nodes based on f-score heuristic, until nodes exhausted or target found
        Node current = null;
        while (!fScorePQ.isEmpty()) {
            current = fScorePQ.remove();
            if (current.equals(dst)) break;

            // For each node that is unvisited, calculated scores and add to PQ
            for (Node adjNode : this.adjList.get(current).keySet()) {
                if (gScores.containsKey(adjNode)) continue; // If node seen, skip
                previous.put(adjNode, current);
                gScores.put(adjNode, gScores.get(current) + 1); // ! if want weighted, then multiply by edge weight
                fScores.put(adjNode, gScores.get(adjNode) + euclidianDistance(adjNode, dst, xLabel, yLabel));
                fScorePQ.add(adjNode);
            }
        }

        // If destination node reached, backtrace and return nodes in shortest path
        ArrayList<Node> path = null;
        if (current != null && current.equals(dst)) {
            path = new ArrayList<>();
            do {
                path.add(current);
            } while ((current = previous.get(current)) != null);
            Collections.reverse(path); // Reverse the constructed path to get src->dst instead of dst->src
        }
        return path;
    }
    private double euclidianDistance(Node n1, Node n2, String xLabel, String yLabel) {
        return Math.sqrt(
                Math.pow(n1.data.getDouble(xLabel) - n2.data.getDouble(xLabel), 2)
                + Math.pow(n1.data.getDouble(yLabel) - n2.data.getDouble(yLabel), 2)
        );
    }

    /**
     *
     * @param src
     * @return
     */
    public ArrayList<Node> getReachableNodes(Node src) {
        return new ArrayList<Node>();
    }

    /**
     * Get the connected components of the graph, using BFS over the set of nodes.
     * @return
     */
    public ArrayList<Graph> getConnectedComponents() {
        ArrayList<Graph> components = new ArrayList<>();
        HashSet<Node> seenNodes = new HashSet<>();
        for (Node n : this.nodes.values()) {
            if (!seenNodes.contains(n)) components.add(getConnectedComponent(n, seenNodes));
        }
        return components;
    }

    /**
     * For a given source node, return a new graph (shallow copy) describing the nodes connected component.
     * This method requires the graph is undirected (by having edges in both directions).
     * @param src
     * @param seenNodes
     * @return
     */
    public Graph getConnectedComponent(Node src, HashSet<Node> seenNodes) {
        Graph newG = new Graph();            // Create a new graph for the connected component
        Queue<Node> q = new LinkedList<>();  // Create a queue for FIFO processing of nodes
        q.add(src);                          // Add starting node

        // If caller doesn't need to sync seen (visited) nodes then create here for local tracking
        if (seenNodes == null) seenNodes = new HashSet<>();

        while (!q.isEmpty()) {         // Until q empty, pop node and search neighbours, adding them to q if unseen
            Node n = q.remove();
            newG.addNode(n); // Add to graph
            seenNodes.add(n);
            for (Node adjNode : this.adjList.get(n).keySet()) {
                if (seenNodes.contains(adjNode)) continue; // Don't add to queue if node is seen
                q.add(adjNode);
                newG.addEdge(n, adjNode, null);
            }
        }
        return newG;
    }

    /**
     * Implementation of Tarjan's algorithm for getting strongly connected components of a graph.
     * @return
     */
    public ArrayList<Graph> getStronglyConnectedComponents() {
        return new ArrayList<Graph>();
    }

}

