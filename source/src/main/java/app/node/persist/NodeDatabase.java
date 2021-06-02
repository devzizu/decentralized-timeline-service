
package app.node.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import app.node.persist.timeline.TimelineMessage;
import app.exchange.zmq.*;

public class NodeDatabase implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public String node_id;
    public long last_message_id;
    public List<TimelineMessage> timeline;
    public ConcurrentHashMap<String, Long> subscriptionClocks;
    public List<Post> myTimelineList;
    public TreeSet<Post> myOrderedTimeline;
    public Map<String, TreeSet<Post>> otherNodeMessages;
    public boolean loaded;

    public NodeDatabase() {

        this.myTimelineList = new ArrayList<>();
        this.myOrderedTimeline = new TreeSet<Post>(new PostClockComparator());
        this.otherNodeMessages = new HashMap<>();
        this.timeline = new ArrayList<>();

        this.subscriptionClocks = new ConcurrentHashMap<>();
        this.loaded = false;
    }

    public String getNodeID() {
        return node_id;
    }

    public void incrementMine() {
        this.subscriptionClocks.put(node_id, this.subscriptionClocks.get(node_id) + 1); 
    }

    public void setNodeID(String id) {
        node_id = id;
    }

    public void setLoaded() {
        this.loaded = true;
    }

    public NodeDatabase loadDatabase(String databaseFile) {

        try {

            FileInputStream fis = new FileInputStream(new File(databaseFile));

            ObjectInputStream ois = new ObjectInputStream(fis);

            NodeDatabase db = (NodeDatabase) ois.readObject();

            ois.close();
            fis.close();

            db.setLoaded();

            return db;

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean storeDatabase() {

        try {

            FileOutputStream fos = new FileOutputStream(new File("../nodes/"+node_id+".db"));

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this);
            oos.close();
            fos.close();

            return true;

        } catch(Exception e) {

            return false;
        }        
    }

    public void setClock(String nodeId, long value){
        subscriptionClocks.put(nodeId,value);
    }
    
    static class PostClockComparator implements Serializable, Comparator<Post> {
        
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

            if (allBigger == allSmaller) {
                return b.nodeID.compareTo(a.nodeID);
            }

            return allSmaller ? -1 : (allBigger? 1 : 0);
        }
    } 

    @Override
    public String toString() {
        return "{" +
            " last_message_id='" + last_message_id + "'" +
            ", timeline='" + timeline + "'" +
            ", subscriptionClocks='" + subscriptionClocks + "'" +
            ", myTimelineList='" + myTimelineList + "'" +
            ", myOrderedTimeline='" + myOrderedTimeline + "'" +
            ", otherNodeMessages='" + otherNodeMessages + "'" +
            ", loaded='" + loaded + "'" +
            "}";
    }
    
}
