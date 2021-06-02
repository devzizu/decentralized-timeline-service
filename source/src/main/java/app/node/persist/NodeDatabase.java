
package app.node.persist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import app.node.persist.timeline.TimelineMessage;
import app.exchange.zmq.*;

public class NodeDatabase {
    
    public String node_id;
    public long last_message_id;
    public List<TimelineMessage> timeline;
    public ConcurrentHashMap<String, Long> subscriptionClocks;
    public List<Post> myTimelineList;
    public Set<Post> myOrderedTimeline;
    public Map<String, TreeSet<Post>> otherNodeMessages;

    public NodeDatabase() {

        this.myTimelineList = new ArrayList<>();
        this.myOrderedTimeline = new TreeSet<Post>(new PostClockComparator());
        this.otherNodeMessages = new HashMap<>();

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
    
    static class PostClockComparator implements Comparator<Post> {
        
        public int compare(Post a, Post b) {

            boolean allSmaller = true;
            boolean allBigger = true;

            for (String keyA: a.subscriptionClocks.keySet()) {

                if (b.subscriptionClocks.containsKey(keyA)) {

                    if (a.subscriptionClocks.get(keyA) == b.subscriptionClocks.get(keyA))
                        if (keyA.equals(a.nodeID))
                            allBigger = false;
                        else if (keyA.equals(b.nodeID))
                            allSmaller = false;

                    allSmaller = allSmaller && (a.subscriptionClocks.get(keyA) <= b.subscriptionClocks.get(keyA));
                    allBigger = allBigger && (a.subscriptionClocks.get(keyA) >= b.subscriptionClocks.get(keyA));
                }
            }

            System.out.println("small: " + allSmaller + " bigger: " + allBigger);

            if (allBigger == allSmaller) {
                return b.nodeID.compareTo(a.nodeID);
            }

            return allSmaller ? -1 : (allBigger? 1 : 0);
        }
    } 
}
