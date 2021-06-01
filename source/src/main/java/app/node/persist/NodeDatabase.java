
package app.node.persist;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import app.node.persist.timeline.TimelineMessage;

public class NodeDatabase {
    
    public String node_id;
    public long last_message_id;
    public List<TimelineMessage> timeline;
    public ConcurrentHashMap<String, Long> subscriptionClocks;

    public NodeDatabase() {

        this.subscriptionClocks = new ConcurrentHashMap<>();
    }

    public void incrementMine() {
        this.subscriptionClocks.put(node_id, this.subscriptionClocks.get(node_id) + 1); 
    }

    public void setNodeID(String id) {
        this.node_id = id;
    }

    public void loadDatabase(String databaseFile) {

        // todo
    }

    public void setClock(String nodeId, long value){
        subscriptionClocks.put(nodeId,value);
    }
}
