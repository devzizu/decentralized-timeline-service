
package app.exchange.req;

import java.io.Serializable;

import app.exchange.MessageWrapper;

public class SubscribeRequest extends MessageWrapper implements Serializable {
   
    private static final long serialversionUID = 129348938L;

    public String nodeId;

    public String subscription;

    public SubscribeRequest(String nodeId, String subscription) {
        this.nodeId = nodeId;
        this.subscription = subscription;
    }  

    @Override
    public String toString() {
        return "subrequest = {" +
            " nodeId='" + this.nodeId + "'" +
            ", subscription='" + this.subscription + "'" +
            "}";
    }

}
