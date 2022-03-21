import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
class GraphTest {

    @Test
    void getConnectedComponent() throws FileNotFoundException {
        // Create a basic graph
        Graph g = loadGraph("src/test/resources/test-graph-undirected-1.json");

        // Search connected component from node "1"
        assertNotNull(g.getNode("1"));
        Graph n1CC = g.getConnectedComponent(g.getNode("1"), null);

        // Assert that only reachable nodes are present
        for (String nodeId : new String[] {"1", "2", "3", "4", "5", "6", "7"})
            assertNotNull(n1CC.getNode(nodeId));
        for (String nodeId : new String[] {"8", "9", "10"})
            assertNull(n1CC.getNode(nodeId));
    }

    @Test
    void getConnectedComponents() throws FileNotFoundException {
        Graph g = loadGraph("src/test/resources/test-graph-undirected-1.json");  // Create a basic graph
        ArrayList<Graph> CCs = g.getConnectedComponents();  // Get the connected components

        // Assertions
        assertEquals(3, CCs.size());
        Graph CC = CCs.get(0);
        for (String nodeId : new String[] {"1", "2", "3", "4", "5", "6", "7"})
            assertNotNull(CC.getNode(nodeId));
        for (String nodeId : new String[] {"8", "9", "10"})
            assertNull(CC.getNode(nodeId));

        CC = CCs.get(1);
        for (String nodeId : new String[] {"8", "9"})
            assertNotNull(CC.getNode(nodeId));
        for (String nodeId : new String[] {"1", "2", "3", "4", "5", "6", "7", "10"})
            assertNull(CC.getNode(nodeId));

        CC = CCs.get(2);
        for (String nodeId : new String[] {"10"})
            assertNotNull(CC.getNode(nodeId));
        for (String nodeId : new String[] {"8", "9", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
            assertNull(CC.getNode(nodeId));
    }

    @Test
    void getShortestPath() throws FileNotFoundException {
        Graph g = loadGraph("src/test/resources/test-graph-vec2d-1.json", "nodes",
                "paths", "nodes", null);  // Create a basic graph
        ArrayList<Node> shortestPath = g.getShortestPath(g.getNode("1"), g.getNode("10"), "x", "y");

        // Assert that shortestPath is correct
        assertEquals(shortestPath, Arrays.asList(g.getNode("1"),
                g.getNode("3"),
                g.getNode("10")
            )
        );
    }

    @Test
    void getShortestPathTrain() throws FileNotFoundException {
        Graph g = loadUndirectedGraph("src/test/resources/test-train-network.json", "stations",
                "lines", "stations", "name", "-north", "-south");  // Create a basic graph
        ArrayList<Node> shortestPath = g.getShortestPath(g.getNode("940GZZLUWLA"), g.getNode("940GZZLUWSP"), "longitude", "latitude");

        // Assert that shortestPath is correct
        assertEquals(shortestPath, Arrays.asList(g.getNode("940GZZLUWLA"),
                        g.getNode("940GZZLULRD"),
                        g.getNode("940GZZLULAD"),
                        g.getNode("940GZZLUWSP")
                )
        );

        shortestPath = g.getShortestPath(g.getNode("940GZZLURGP"), g.getNode("940GZZLUTCR"), "longitude", "latitude");
        if (shortestPath == null) System.out.println("No path...");
        else {
            for (int i = 1; i < shortestPath.size(); i++) {
                Node n1 = shortestPath.get(i-1);
                Node n2 = shortestPath.get(i);
                System.out.println(n1.id + "--(" + g.getEdge(n1.id, n2.id).data.getString("name") + ")-->" + n2.id);
            }
        }
    }

    // HELPERS
    private static Graph loadGraph(String graphFilePath) throws FileNotFoundException {
        File file = new File(graphFilePath);
        Graph g = new Graph();
        GraphLoader.loadNodes(g, file, "nodes");
        GraphLoader.addPathsData(g, file, "paths", "nodes");
        return g;
    }

    private static Graph loadGraph(String graphFilePath, String nodesKey, String pathsKey, String pathsNodesKey, String pathsLabelKey) throws FileNotFoundException {
        File file = new File(graphFilePath);
        Graph g = new Graph();
        GraphLoader.loadNodes(g, file, nodesKey);
        if (pathsLabelKey == null) {
            GraphLoader.addPathsData(g, file, pathsKey, pathsNodesKey);
        } else {
            GraphLoader.addPathsData(g, file, pathsKey, pathsLabelKey, pathsNodesKey);
        }

        return g;
    }

    private static Graph loadUndirectedGraph(String graphFilePath, String nodesKey, String pathsKey,
                                             String pathsNodesKey, String pathsLabelKey,
                                             String pathLabelExtensionForward, String pathLabelExtensionBackward)
            throws FileNotFoundException {
        File file = new File(graphFilePath);
        Graph g = new Graph();
        GraphLoader.loadNodes(g, file, nodesKey);
        if (pathsLabelKey == null || pathsLabelKey == "") throw new IllegalArgumentException("Must supply path label.");
        GraphLoader.addPathsDataUndirected(g, file, pathsKey, pathsLabelKey, pathsNodesKey, pathLabelExtensionForward, pathLabelExtensionBackward);
        return g;
    }
}