import org.json.JSONObject;

import java.util.Objects;

public class Edge {
    Node tail;
    Node head;
    JSONObject data;

    public Edge(Node tail, Node head, JSONObject data) {
        this.tail = tail;
        this.head = head;
        this.data = (data == null) ? new JSONObject() : data;
    }

    public void addData(JSONObject newData) {
        JSONObject combinedData = new JSONObject(this.data, JSONObject.getNames(this.data));
        for(String key : JSONObject.getNames(newData)) { combinedData.put(key, newData.get(key)); }
        this.data = combinedData;
    }

    public void addData(String key, String val) {
        this.data.put(key, val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tail.id, head.id);
    }
}
