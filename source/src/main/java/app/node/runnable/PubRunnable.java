package app.node.runnable;


import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.central.usernode.NodeNetwork;
import app.exchange.*;

public class PubRunnable implements Runnable {

    private NodeNetwork nodeNetwork;

    public PubRunnable(NodeNetwork nodeNetwork) {
        this.nodeNetwork = nodeNetwork;
    }

    @Override
    public void run(){
        try(ZContext context = new ZContext();
        ZMQ.Socket inProcPull = context.createSocket(SocketType.PULL);
        ZMQ.Socket pubSocket = context.createSocket(SocketType.PULL)){

            inProcPull.bind("inproc://"+ServiceConstants.INPROC_PUB);
            pubSocket.bind("tcp://"+nodeNetwork.host+":"+nodeNetwork.pubPort);

            while(true) {
                
                byte[] postBytes = inProcPull.recv();

            }

        }
    }
}