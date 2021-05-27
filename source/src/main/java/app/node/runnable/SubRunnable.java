
package app.node.runnable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.*;
import app.exchange.*;

import app.central.usernode.IpPort;

public class SubRunnable extends Thread {

    ZMQ.Socket subSocket;

    private Set<IpPort> connections;
    private Set<String> subscriptions;
    private ZContext context;

    public SubRunnable (ZContext context, Set<IpPort> conn, Set<String> subscriptions){
        this.connections = conn;
        this.subscriptions = subscriptions;
        this.context = context;
    }

    public void run() {

        try (ZMQ.Socket subSocket = context.createSocket(SocketType.SUB);
             ZMQ.Socket pushSocketPub = context.createSocket(SocketType.PUSH);
             ZMQ.Socket pushSocketTimeline = context.createSocket(SocketType.PUSH);)
        {

            this.subSocket = subSocket;

            pushSocketPub.connect("inproc://"+ServiceConstants.INPROC_PUB);
            pushSocketTimeline.connect("inproc://"+ServiceConstants.INPROC_TIMELINE);

            if (this.connections != null)
                //conects to all pubs that came from login
                this.connections.forEach(e -> connect(e));

            if (this.subscriptions != null){
                this.subscriptions.forEach(e->subscribe(e));
            }
            
            while(true) {
                byte[] message = subSocket.recv();
                // send to pub inproc
                pushSocketPub.send(message);
                // send to timeline inproc
                pushSocketTimeline.send(message);
            }
        }
    }
    
    public void subscribe(String nodeID, IpPort ipPort) {

        System.out.println("trying to sub: " + nodeID + " " + ipPort);

        connect(ipPort);

        this.subSocket.subscribe(nodeID);
    }

    public void subscribe(String nodeID) {
        this.subSocket.subscribe(nodeID);
    }


    public void unsubscribe(String nodeID) {

        this.subSocket.unsubscribe(nodeID);
    }


    public void connect(IpPort ipPort){
        subSocket.connect("tcp://"+ipPort.ip+":"+ipPort.port);
    }
}
