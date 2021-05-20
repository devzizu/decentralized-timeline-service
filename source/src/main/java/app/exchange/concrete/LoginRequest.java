package app.exchange.concrete;

import java.io.Serializable;

import app.central.usernode.Network;

public class LoginRequest implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public Network network;

    public LoginRequest(Network network) {
        this.network = network;
    }
}
