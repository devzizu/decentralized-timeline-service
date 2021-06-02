package app.exchange.req;

import app.exchange.MessageWrapper;

public class RecoverRequest extends MessageWrapper {

    // requested node clock
    public String nodeID;
    public long clock;

    public RecoverRequest(String nodeID,long clock) {
        this.nodeID = nodeID;
        this.clock = clock;
    }
}
