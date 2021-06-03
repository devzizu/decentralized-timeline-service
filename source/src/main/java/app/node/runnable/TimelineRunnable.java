package app.node.runnable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    private List<Post> futureNodeMessages;

    private NodeDatabase nodeDatabase;

    public TimelineRunnable(NodeDatabase nodeDatabase, ZContext context, String nodeID) {

        this.myNodeID = nodeID;
        this.context = context;
        this.nodeDatabase = nodeDatabase;
        this.futureNodeMessages = new ArrayList<>();
    }

    public List<Post> getOrderedTimeline() {
        return this.nodeDatabase.myOrderedTimeline.stream().collect(Collectors.toList());
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
                    
                    GUI.showMessageFromNode(this.myNodeID, "got new post from future");
                }
            }
        }
    }

    public void orderMessage(Post postMessage) {
        
        for(String nodeClock: this.nodeDatabase.subscriptionClocks.keySet()) {
            long old = this.nodeDatabase.subscriptionClocks.get(nodeClock);
            if (!nodeClock.equals(this.myNodeID))
                if (postMessage.subscriptionClocks.containsKey(nodeClock))
                    this.nodeDatabase.subscriptionClocks.put(nodeClock, Math.max(old, postMessage.subscriptionClocks.get(nodeClock)));
        }

        List<Post> toRemove = new ArrayList<>();

        if (postMessage.nodeID.equals(this.myNodeID)) {

            this.nodeDatabase.myTimelineList.add(postMessage);
    
        } else {

            if (!this.nodeDatabase.otherNodeMessages.containsKey(postMessage.nodeID)) {
                
                this.nodeDatabase.otherNodeMessages.put(postMessage.nodeID, new TreeSet<Post>());
            }

            TreeSet<Post> otherPosts = this.nodeDatabase.otherNodeMessages.get(postMessage.nodeID);

            otherPosts.add(postMessage);   

            while (otherPosts.size() > 2)
                toRemove.add(otherPosts.pollFirst());
        }

        for (Post p: toRemove)
            this.nodeDatabase.myOrderedTimeline.remove(p);

        this.nodeDatabase.myOrderedTimeline.add(postMessage);
    }

    public void processFutureMessages() {

        for (Post p : this.futureNodeMessages) {
            if (!this.isPostFromFuture(p))
                orderMessage(p);
        }
    }

    public boolean isPostFromFuture(Post p) {

        for (String nodeClock: p.subscriptionClocks.keySet()) {

            long diff = p.subscriptionClocks.get(nodeClock) - this.nodeDatabase.subscriptionClocks.get(nodeClock);

            if (nodeClock.equals(p.nodeID) && Math.abs(diff) > 1) return true;
            else if ((!nodeClock.equals(p.nodeID)) && diff>0) return true;
        }
        
        return false;
    }
}
