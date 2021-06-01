package app.exchange.req;

import app.exchange.MessageWrapper;

public class ClockRequest extends MessageWrapper {

    // requested node clock
    public String nodeID;
    public Long clock;

    public ClockRequest(String nodeID,Long clock) {
        this.nodeID = nodeID;
        this.clock = clock;
    }
}
