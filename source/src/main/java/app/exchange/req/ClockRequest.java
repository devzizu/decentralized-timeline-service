package app.exchange.req;

import app.exchange.MessageWrapper;

public class ClockRequest extends MessageWrapper {

    // requested node clock
    public String nodeID;

    public ClockRequest(String nodeID) {
        this.nodeID = nodeID;
    }
}
