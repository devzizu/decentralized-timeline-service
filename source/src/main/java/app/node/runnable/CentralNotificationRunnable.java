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
    private ZContext context;
    private SubRunnable subRunnable;

    public CentralNotificationRunnable(ZContext context, String nodeID, NodeNetwork nodeNetwork, SubRunnable subRunnable) {

        this.nodeNetwork = nodeNetwork;
        this.context = context;
        this.nodeID = nodeID;
        this.subRunnable = subRunnable;
    }

    @Override
    public void run() {

        try (ZMQ.Socket pullSocket = context.createSocket(SocketType.PULL))
        {

            pullSocket.bind("tcp://"+nodeNetwork.host+":" + nodeNetwork.pullPort);
            
            while(true) {

                byte[] notificationBytes = pullSocket.recv();

                Notification notificationReceived = (Notification) Serialization.deserialize(notificationBytes);

                GUI.showMessageFromNode(this.nodeID, "central notification: " + notificationReceived.toString());

                this.subRunnable.subscribe(notificationReceived.subscription, notificationReceived.conection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
