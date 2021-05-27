package app.node.runnable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.central.usernode.NodeNetwork;
import app.exchange.zmq.Notification;
import app.util.data.Serialization;
import app.util.gui.GUI;

public class CentralNotificationRunnable implements Runnable {

    private NodeNetwork nodeNetwork;
    private String nodeID;

    public CentralNotificationRunnable(String nodeID, NodeNetwork nodeNetwork) {

        this.nodeNetwork = nodeNetwork;
        this.nodeID = nodeID;
    }

    @Override
    public void run() {

        try (ZContext context = new ZContext();
             ZMQ.Socket pullSocket = context.createSocket(SocketType.PULL))
        {

            pullSocket.bind("tcp://"+nodeNetwork.host+":" + nodeNetwork.pullPort);
            
            while(true) {

                byte[] notificationBytes = pullSocket.recv();

                Notification notificationReceived = (Notification) Serialization.deserialize(notificationBytes);

                GUI.showMessageFromNode(this.nodeID, "notification:" + notificationReceived.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
