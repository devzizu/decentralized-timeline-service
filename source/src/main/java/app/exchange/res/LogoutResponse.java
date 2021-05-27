
package app.exchange.res;

import java.io.Serializable;

import app.exchange.MessageWrapper;

public class LogoutResponse extends MessageWrapper implements Serializable {

    private static final long serialversionUID = 129348938L;

    public String nodeId;

    public LogoutResponse(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "{" +
            " nodeId='" + this.nodeId + "'" + "',\n" +
            super.toString() + 
            "}";
    }

}
