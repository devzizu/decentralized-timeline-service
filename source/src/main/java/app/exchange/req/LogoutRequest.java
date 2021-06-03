package app.exchange.req;

import app.exchange.MessageWrapper;

public class LogoutRequest extends MessageWrapper {

    public String nodeId;

    public LogoutRequest(String nodeId) {
        this.nodeId = nodeId;
    }
    
}
