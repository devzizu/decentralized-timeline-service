package app.exchange.res;

public class ClockResponse extends MessageWrapper {

    public long clockValue;
    public String nodeID;

    public ClockResponse(long clockValue, String nodeID) {
        this.clockValue = clockValue;
        this.nodeID = nodeID;
    }

}
