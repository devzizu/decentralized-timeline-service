
package app.node.persist;

import java.util.List;
import java.util.Map;

import app.node.persist.timeline.TimelineMessage;

public class NodeDatabase {
    
    public String node_id;
    public long last_message_id;
    public List<TimelineMessage> timeline;
    public Map<String, Long> subscriptionClocks;

    public NodeDatabase() {

    }

    public void setNodeID(String id) {
        this.node_id = id;
    }

    public void loadDatabase(String databaseFile) {

        // todo
    }
}
