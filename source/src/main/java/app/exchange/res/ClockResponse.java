package app.exchange.res;

import app.exchange.MessageWrapper;

public class ClockResponse extends MessageWrapper {

    public long clockValue;
    public String nodeID;

    public ClockResponse(long clockValue, String nodeID) {
        this.clockValue = clockValue;
        this.nodeID = nodeID;
    }


    @Override
    public String toString() {
        return "{" +
            " clockValue='" + this.clockValue + "'" +
            ", nodeID='" + this.nodeID + "'\n" +
            super.toString() +
            "}";
    }

}
