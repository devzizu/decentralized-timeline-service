package app.exchange.req;

import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;

public class LoginRequest extends MessageWrapper {
    
    public String nodeID;
    public NodeNetwork network;

    public LoginRequest(String nodeID, NodeNetwork network) {
        this.nodeID = nodeID;
        this.network = network;
    }
}
