
package app.node.runnable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.*;
import app.exchange.*;

import app.central.usernode.IpPort;

public class SubRunnable extends Thread {

    ZMQ.Socket subSocket;

    private Map<String, IpPort> connectionsMap;
    private Map<String, IpPort> recoveryMap;
    private ZContext context;

    public SubRunnable (ZContext context, Map<String, IpPort> conn, Map<String, IpPort> recoveryMap){
        this.connectionsMap = conn;
        this.recoveryMap = recoveryMap;
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

            if (this.connectionsMap != null) {
                //conects to all pubs that came from login
                for (Map.Entry<String, IpPort> e: this.connectionsMap.entrySet())
                    connect(e.getValue());
            } else {
                this.connectionsMap = new HashMap<>();
            }

            if (this.recoveryMap != null) {
                this.recoveryMap.keySet().forEach(e->addSub(e));
            } else {
                this.connectionsMap = new HashMap<>();
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

        if (this.connectionsMap.containsKey(nodeID)) {
            IpPort oldIp = this.connectionsMap.get(nodeID);
            this.connectionsMap.put(nodeID, ipPort);
            // disconnect from old ip
            disconnect(oldIp);
        }

        // connect to new ip
        connect(ipPort);

        // sub, if not already
        this.subSocket.subscribe(nodeID);
    }

    public void removeSub(String nodeID) {
        this.subSocket.unsubscribe(nodeID);
    }

    public void addSub(String nodeID) {
        this.subSocket.subscribe(nodeID);
    }

    public void connect(IpPort ipPort){
        subSocket.connect("tcp://"+ipPort.ip+":"+ipPort.port);
    }

    public void disconnect(IpPort ipPort) {
        subSocket.disconnect("tcp://"+ipPort.ip+":"+ipPort.port);
    }
}
