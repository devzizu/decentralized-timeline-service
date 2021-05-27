package app.node.runnable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.exchange.ServiceConstants;
import app.exchange.zmq.Post;
import app.util.gui.GUI;

public class TimelineRunnable implements Runnable {

    private ZContext context;
    private String nodeID;

    public TimelineRunnable(ZContext context, String nodeID) {

        this.nodeID = nodeID;
        this.context = context;
    }

    @Override
    public void run() {

        try (ZMQ.Socket pullInprocSocket = context.createSocket(SocketType.PULL))
        {
            
            pullInprocSocket.bind("inproc://"+ServiceConstants.INPROC_TIMELINE);
            
            while(true) {

                //"nodeID#{...post em json...}"

                byte[] messageBytes = pullInprocSocket.recv();
                String messageStr = new String(messageBytes);
                String messageParts[] = messageStr.split("#");

                Post postMessage = Post.fromJSON(messageParts[1]);

                GUI.showMessageFromNode(nodeID, "Got sub message:");
                GUI.showMessageFromNode(nodeID, postMessage.toString());
            }
        }
    }
}
