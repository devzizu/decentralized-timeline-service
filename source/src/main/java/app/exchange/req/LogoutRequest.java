package app.exchange.req;

import java.io.Serializable;

import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;

public class LogoutRequest extends MessageWrapper implements Serializable{

    private static final long serialversionUID = 129348938L;

    public String nodeId;


    public LogoutRequest(String nodeId) {
        this.nodeId = nodeId;
    }
    
}
