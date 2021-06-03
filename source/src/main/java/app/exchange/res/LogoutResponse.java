
package app.exchange.res;

import app.exchange.MessageWrapper;

public class LogoutResponse extends MessageWrapper {

    public String nodeId;

    public LogoutResponse(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "LogoutResponse = {" +
            " nodeId='" + this.nodeId + "'" + "',\n" +
            super.toString() + 
            "}";
    }
}
