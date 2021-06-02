package app.exchange.req;

import app.exchange.MessageWrapper;

public class ClockRequest extends MessageWrapper {

    // requested node clock
    public String nodeID;

    public ClockRequest(String nodeID) {
        this.nodeID = nodeID;
    }

    @Override
    public String toString() {
        return "ClockRequest = {" +
            " nodeID='" +this.nodeID + "'\n" +
            super.toString() +
            "}";
    }

}
