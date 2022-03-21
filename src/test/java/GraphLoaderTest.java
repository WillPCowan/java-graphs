import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

class GraphLoaderTest {

    @Test
    void loadNodes() throws FileNotFoundException {

        // Create graph with node data
        File file = new File("src/test/resources/test-train-network.json");
        Graph g = new Graph();
        GraphLoader.loadNodes(g, file, "stations");

        // Check that correct nodes exist and have data
        Node n = g.getNode("940GZZLUSRP");
        assertNotNull(n);
        assertEquals("South Ruislip", n.data.getString("name"));
        assertEquals(-0.398915, n.data.getDouble("longitude"));
        assertEquals(51.556853, n.data.getDouble("latitude"));

        n = g.getNode("940GZZLUPCO");
        assertNotNull(n);
        assertEquals("Pimlico", n.data.getString("name"));
        assertEquals(-0.133761, n.data.getDouble("longitude"));
        assertEquals(51.489097, n.data.getDouble("latitude"));
    }

    @Test
    void addPathsData() throws FileNotFoundException {
        // Create graph with node data (tested in prior step)
        File file = new File("src/test/resources/test-train-network.json");
        Graph g = new Graph();
        GraphLoader.loadNodes(g, file, "stations");
        GraphLoader.addPathsData(g, file, "lines", "name", "stations");

        // Test edges are created, updating edge set and adjacency list
        edgeAssertions(g, "940GZZLUHSC", "940GZZLUGHK", "Circle");
        edgeAssertions(g, "940GZZLUPCC", "940GZZLUOXC", "Bakerloo");
        edgeAssertions(g, "940GZZLUOAK", "940GZZLUCKS", "Piccadilly");
    }

    private void edgeAssertions(Graph g, String tailId, String headId, String name) {
        Edge e = g.getEdge(tailId, headId);
        assertNotNull(e);
        assertEquals(name, e.data.getString("name"));
        assertEquals(e, g.getAdjList().get(g.getNode(tailId)).get(g.getNode(headId))); // Adj list contains edge
    }
}