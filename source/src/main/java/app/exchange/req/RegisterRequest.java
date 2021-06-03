package app.exchange.req;

import app.central.usernode.NodeNetwork;

import app.exchange.MessageWrapper;

public class RegisterRequest extends MessageWrapper {

    public String nodeId;
    public NodeNetwork network;

    public RegisterRequest(String nodeId, NodeNetwork network) {
        this.nodeId = nodeId;
        this.network = network;
    }

    @Override
    public String toString() {
        return "{" +
            " nodeId='" + this.nodeId + "'" +
            ", network='" + this.network + "'" +
            "}";
    }

}
