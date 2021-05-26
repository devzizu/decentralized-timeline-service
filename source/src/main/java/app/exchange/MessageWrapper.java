package app.exchange;

import java.io.Serializable;

public abstract class MessageWrapper implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public int messageID;
    public String statusMessage;
    public boolean statusCode;

    public void setStatusMessage(String message) {
        this.statusMessage = message;
    }

    public void setStatusCode(boolean statusCode) {
        this.statusCode = statusCode;   
    }

    @Override
    public String toString() {
        return "{" +
            " messageID='" + this.messageID + "'" +
            ", statusMessage='" + this.statusMessage + "'" +
            ", statusCode='" + this.statusCode + "'" +
            "}";
    }

}
