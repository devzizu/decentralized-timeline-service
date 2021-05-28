package app.node.runnable;


import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.central.usernode.NodeNetwork;
import app.exchange.*;
import app.util.gui.GUI;

public class PubRunnable implements Runnable {

    private ZContext context;
    private NodeNetwork nodeNetwork;

    public PubRunnable(ZContext context, NodeNetwork nodeNetwork) {
        this.nodeNetwork = nodeNetwork;
        this.context = context;
    }

    @Override
    public void run(){
        try(ZMQ.Socket inProcPull = context.createSocket(SocketType.PULL);
            ZMQ.Socket pubSocket = context.createSocket(SocketType.PUB)) {

            inProcPull.bind("inproc://"+ServiceConstants.INPROC_PUB);
            pubSocket.bind("tcp://"+nodeNetwork.host+":"+nodeNetwork.pubPort);

            while(true) {
                byte[] postBytes = inProcPull.recv();
                System.out.println("(publishing) received inproc to pub: " + (new String(postBytes)));
                pubSocket.send(postBytes);
            }
        }
    }
}