package app.exchange.res;

import java.io.Serializable;

import app.exchange.MessageWrapper;

public class RegisterResponse extends MessageWrapper implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public String statusMessage;

    public RegisterResponse(String message) {
        this.statusMessage = message;
    }
}
