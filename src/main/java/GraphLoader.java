import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;


public class GraphLoader {

    /**
     * Load node data from JSON file (does not include edge data).
     * @param file JSON file that is or contains a field with a JSON array of node data
     * @return Array of nodes with associated json data
     */
    public static void loadNodes(Graph g, File file, String nodesKey) throws FileNotFoundException {

        // Create a reader for the json file
        BufferedReader input = new BufferedReader(new FileReader(file));
        JSONTokener tokener = new JSONTokener(input);

        // Get the array of node data. .json file could be array itself, or a key may contain array
        JSONArray nodesJSONArr = (nodesKey == null)
                ? new JSONArray(tokener)
                : (new JSONObject(tokener)).getJSONArray(nodesKey);

        // Get the json nodes array and put each node into native datastructure
        // (assumes: no duplicate nodes, "id" property on nodes with unique id)
        if (nodesJSONArr.getJSONObject(0).keySet().size() > 1) // If more than just "id" property
            for (int i = 0; i < nodesJSONArr.length(); i++) {
                JSONObject nodeJsonObj = nodesJSONArr.getJSONObject(i);  // Get the next node as JSON object
                String id = nodeJsonObj.getString("id");            // Get id of node
                nodeJsonObj.remove("id");                           // Remove redundant id property
                g.addNode(new Node(id, nodeJsonObj));                    // Create new node
            }
        else {
            for (int i = 0; i < nodesJSONArr.length(); i++) { // If only "id" property exists
                JSONObject nodeJsonObj = nodesJSONArr.getJSONObject(i);  // Get the next node as JSON object
                String id = nodeJsonObj.getString("id");            // Get id of node
                g.addNode(new Node(id));                                 // Create new node
            }
        }
    }


    /**
     * Add a set of paths to a graph g (ensuring edges are added).
     * @param g
     * @param file
     * @param pathsKey
     * @param pathsNodesKey
     * @throws FileNotFoundException
     */
    public static void addPathsData(Graph g, File file, String pathsKey, String pathsNodesKey) throws FileNotFoundException {

        // Create a reader for the json file
        BufferedReader input = new BufferedReader(new FileReader(file));
        JSONTokener tokener = new JSONTokener(input);

        // Get the array of node data. .json file could be array itself, or a key may contain array
        JSONArray paths = (new JSONObject(tokener)).getJSONArray(pathsKey);

        // For each path, add edges or edit edges to add path label
        for (int i = 0; i < paths.length(); i++) {
            JSONObject pathData = paths.getJSONObject(i);
            JSONArray path = pathData.getJSONArray(pathsNodesKey);

            // Assuming node array implies directed path from i=0 to i=n, add edges and edge data
            for (int j = 1; j < path.length(); j++) {
                // Get edge if exists. If not (create) else (add label data)
                Edge e = g.getEdge(path.getString(j-1), path.getString(j));
                if (e == null) {
                    g.addEdge(path.getString(j - 1), path.getString(j), null);
                }
            }
        }
    }

    /**
     * Add a set of labelled paths to a Graph. If a path edge already exists it is labelled, otherwise it is created.
     * @param g
     * @param file
     * @param pathsKey
     * @param pathsLabelKey
     * @param pathsNodesKey
     * @throws FileNotFoundException
     */
    public static void addPathsData(Graph g, File file, String pathsKey, String pathsLabelKey, String pathsNodesKey) throws FileNotFoundException {

        // Create a reader for the json file
        BufferedReader input = new BufferedReader(new FileReader(file));
        JSONTokener tokener = new JSONTokener(input);

        // Get the array of node data. .json file could be array itself, or a key may contain array
        JSONArray paths = (new JSONObject(tokener)).getJSONArray(pathsKey);

        // For each path, add edges or edit edges to add path label
        for (int i = 0; i < paths.length(); i++) {
            JSONObject pathData = paths.getJSONObject(i);
            String labelVal = pathData.getString(pathsLabelKey);
            JSONArray path = pathData.getJSONArray(pathsNodesKey);

            // Assuming node array implies directed path from i=0 to i=n, add edges and edge data
            for (int j = 1; j < path.length(); j++) {
                // Get edge if exists. If not (create) else (add label data)
                Edge e = g.getEdge(path.getString(j-1), path.getString(j));
                if (e == null) {
                    JSONObject edgeLabel = new JSONObject();
                    edgeLabel.put(pathsLabelKey, labelVal);
                    g.addEdge(path.getString(j - 1), path.getString(j), edgeLabel);
                }
                else { e.addData(pathsLabelKey, labelVal); }
            }
        }
    }

    /**
     * Add a set of labelled paths to a Graph. If a path edge already exists it is labelled, otherwise it is created.
     * @param g
     * @param file
     * @param pathsKey
     * @param pathsLabelKey
     * @param pathsNodesKey
     * @throws FileNotFoundException
     */
    public static void addPathsDataUndirected(Graph g, File file, String pathsKey, String pathsLabelKey,
                                              String pathsNodesKey, String pathLabelExtensionForward,
                                              String pathLabelExtensionBackward) throws FileNotFoundException {

        // Create a reader for the json file
        BufferedReader input = new BufferedReader(new FileReader(file));
        JSONTokener tokener = new JSONTokener(input);

        // Get the array of node data. .json file could be array itself, or a key may contain array
        JSONArray paths = (new JSONObject(tokener)).getJSONArray(pathsKey);

        // For each path, add edges or edit edges to add path label
        for (int i = 0; i < paths.length(); i++) {
            JSONObject pathData = paths.getJSONObject(i);
            String labelVal = pathData.getString(pathsLabelKey);
            JSONArray path = pathData.getJSONArray(pathsNodesKey);

            // Assuming node array implies undirected path from i=0 to i=n, add edges and edge data in both directions
            for (int j = 1; j < path.length(); j++) {
                // Get edge if exists. If not (create) else (add label data)
                Edge e = g.getEdge(path.getString(j-1), path.getString(j));
                if (e == null) {
                    JSONObject edgeLabel = new JSONObject();
                    edgeLabel.put(pathsLabelKey, labelVal+pathLabelExtensionForward);
                    g.addEdge(path.getString(j - 1), path.getString(j), edgeLabel);
                }
                else { e.addData(pathsLabelKey, labelVal+pathLabelExtensionForward); }
                e = g.getEdge(path.getString(j), path.getString(j-1));
                if (e == null) {
                    JSONObject edgeLabel = new JSONObject();
                    edgeLabel.put(pathsLabelKey, labelVal+pathLabelExtensionBackward);
                    g.addEdge(path.getString(j), path.getString(j-1), edgeLabel);
                }
                else { e.addData(pathsLabelKey, labelVal+pathLabelExtensionBackward); }
            }
        }
    }



}
