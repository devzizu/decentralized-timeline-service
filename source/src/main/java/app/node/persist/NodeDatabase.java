
package app.node.persist;

import java.util.List;

import app.node.persist.timeline.TimelineMessage;

public class NodeDatabase {
    
    public String node_id;
    public long last_message_id;
    public List<TimelineMessage> timeline;

    public NodeDatabase() {

    }

    public void setNodeID(String id) {
        this.node_id = id;
    }

    public void loadDatabase(String databaseFile) {

        // todo
    }
}
