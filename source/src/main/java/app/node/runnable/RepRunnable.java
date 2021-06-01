package app.node.runnable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.central.usernode.NodeNetwork;
import app.exchange.*;

public class RepRunnable implements Runnable {
    
    private ZContext context;
    private NodeNetwork nodeNetwork;




    public RepRunnable(ZContext context, NodeNetwork nodeNetwork) {
        this.context = context;
        this.nodeNetwork = nodeNetwork;
    }


    

    @Override
    public void run(){

        try(ZMQ.Socket repSocket = context.createSocket(SocketType.REP)){

            repSocket.bind("tcp://"+nodeNetwork.host+":"+nodeNetwork.replyPort);

            while(true){
                byte[] msg = repSocket.recv();

                MessageWrapper message = (MessageWrapper) Serialization.deserialize(msg);

            }

            }
    }
}