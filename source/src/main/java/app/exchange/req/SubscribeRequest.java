
package app.exchange.req;

import app.exchange.MessageWrapper;

public class SubscribeRequest extends MessageWrapper {
   
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
