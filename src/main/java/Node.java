import org.json.JSONObject;
import java.util.Objects;

public class Node {
    String id;
    JSONObject data;

    public Node(String id) {
        this.id = id;
        this.data = new JSONObject();
    }

    public Node(String id, JSONObject data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
