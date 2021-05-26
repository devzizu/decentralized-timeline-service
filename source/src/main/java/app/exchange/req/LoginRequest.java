package app.exchange.req;

import java.io.Serializable;

import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;

public class LoginRequest extends MessageWrapper implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public String nodeID;
    public NodeNetwork network;

    public LoginRequest(String nodeID, NodeNetwork network) {
        this.nodeID = nodeID;
        this.network = network;
    }
}
