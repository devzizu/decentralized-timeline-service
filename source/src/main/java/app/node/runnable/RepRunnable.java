package app.node.runnable;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.central.usernode.NodeNetwork;
import app.exchange.*;
import app.exchange.req.*;
import app.exchange.res.ClockResponse;
import app.node.persist.NodeDatabase;
import app.util.data.Serialization;

public class RepRunnable implements Runnable {
    
    private ZContext context;
    private NodeNetwork nodeNetwork;
    private NodeDatabase nodeDatabase;



    public RepRunnable(ZContext context, NodeNetwork nodeNetwork, NodeDatabase nodeDatabase) {
        this.context = context;
        this.nodeNetwork = nodeNetwork;
        this.nodeDatabase = nodeDatabase;
    }

    @Override
    public void run(){

        try(ZMQ.Socket repSocket = context.createSocket(SocketType.REP)) {

            repSocket.bind("tcp://"+nodeNetwork.host+":"+nodeNetwork.replyPort);

            while(true){
                byte[] msg = repSocket.recv();

                MessageWrapper message = (MessageWrapper) Serialization.deserialize(msg);

                if (message instanceof ClockRequest) {
                    
                    ClockRequest rMessage = (ClockRequest) message;
                    ClockResponse responseClock = new ClockResponse(nodeDatabase.subscriptionClocks.get(rMessage.nodeID),rMessage.nodeID);
                    responseClock.setStatusCode(true);
                    responseClock.setStatusMessage("here comes the clock that you requested boiiiii!");
                    msg = Serialization.serialize(responseClock);
                    repSocket.send(msg);
                }
/*
                if (message instanceof RecoverRequest){
                    RecoverRequest rMessage = (RecoverRequest) message;

                    //Todo Logica do recover
                }
*/
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}