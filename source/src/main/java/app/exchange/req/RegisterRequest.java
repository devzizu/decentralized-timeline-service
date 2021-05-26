package app.exchange.req;


import java.io.Serializable;

import app.central.usernode.NodeNetwork;

import app.exchange.MessageWrapper;

public class RegisterRequest extends MessageWrapper implements Serializable {

    public String nodeId;
    public NodeNetwork network;

    public RegisterRequest(String nodeId, NodeNetwork network) {
        this.nodeId = nodeId;
        this.network = network;
    }
}
