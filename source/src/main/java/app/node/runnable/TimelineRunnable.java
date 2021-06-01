package app.node.runnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.exchange.ServiceConstants;
import app.exchange.zmq.Post;
import app.node.persist.NodeDatabase;
import app.util.gui.GUI;

public class TimelineRunnable implements Runnable {

    private ZContext context;
    private String myNodeID;

    private List<Post> myTimelineList;
    private Set<Post> myOrderedTimeline;
    private Map<String, Post> otherNodeMessages;
    private List<Post> futureNodeMessages;

    private NodeDatabase nodeDatabase;

    public TimelineRunnable(NodeDatabase nodeDatabase, ZContext context, String nodeID) {

        this.myNodeID = nodeID;
        this.context = context;
        this.myTimelineList = new ArrayList<>();
        this.myOrderedTimeline = new TreeSet<Post>(new PostClockComparator());
        this.otherNodeMessages = new HashMap<>();
        this.nodeDatabase = nodeDatabase;
        this.futureNodeMessages = new ArrayList<>();
    }

    @Override
    public void run() {

        try (ZMQ.Socket pullInprocSocket = context.createSocket(SocketType.PULL);)
        {
            
            pullInprocSocket.bind("inproc://"+ServiceConstants.INPROC_TIMELINE);
            
            while(true) {
                
                //"nodeID#{...post em json...}"
                byte[] messageBytes = pullInprocSocket.recv();
                String messageStr = new String(messageBytes);
                String messageParts[] = messageStr.split("#");

                Post postMessage = Post.fromJSON(messageParts[1]);

                if (!this.isPostFromFuture(postMessage)) {

                    processFutureMessages();
                    orderMessage(postMessage);

                } else {

                    this.futureNodeMessages.add(postMessage);
                    
                    GUI.showMessageFromNode(this.myNodeID, "got new post from FUTURE");
                }
            }
        }
    }

    public void orderMessage(Post postMessage) {
        
        for(String nodeClock: this.nodeDatabase.subscriptionClocks.keySet()) {
            long old = this.nodeDatabase.subscriptionClocks.get(nodeClock);
            if (!nodeClock.equals(this.myNodeID))
                this.nodeDatabase.subscriptionClocks.put(nodeClock, Math.max(old, postMessage.subscriptionClocks.get(nodeClock)));
        }

        if (postMessage.nodeID.equals(this.myNodeID)) {

            this.myTimelineList.add(postMessage);
    
        } else {

            this.otherNodeMessages.put(postMessage.nodeID, postMessage);
        }

        this.myOrderedTimeline.add(postMessage);
    }

    public void processFutureMessages() {

        for (Post p : this.futureNodeMessages) {
            if (!this.isPostFromFuture(p))
                orderMessage(p);
        }
    }

    public boolean isPostFromFuture(Post p) {

        for (String nodeClock: p.subscriptionClocks.keySet()) {

            long diff = p.subscriptionClocks.get(nodeClock)-this.nodeDatabase.subscriptionClocks.get(p.nodeID);

            if (nodeClock.equals(p.nodeID) && diff > 1) return true;
            else if (diff>0) return true;
        }
        
        return false;
    }

    static class PostClockComparator implements Comparator<Post> {
        
        public int compare(Post a, Post b) {

            boolean allSmaller = true;
            boolean allBigger = true;

            for (String keyA: a.subscriptionClocks.keySet()) {
                if (b.subscriptionClocks.contains(keyA)) {
                    allSmaller = allSmaller && (a.subscriptionClocks.get(keyA) <= b.subscriptionClocks.get(keyA));
                    allBigger = allBigger && (a.subscriptionClocks.get(keyA) >= b.subscriptionClocks.get(keyA));
                }
            }

            if (allBigger == allSmaller) {
                return a.nodeID.compareTo(b.nodeID);
            }

            return allSmaller ? -1 : (allBigger? 1 : 0);
        }
    } 
}
