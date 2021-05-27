package app.exchange.zmq;

import java.io.Serializable;

import app.central.usernode.IpPort;
import app.exchange.MessageWrapper;


public class Notification extends MessageWrapper implements Serializable {

    private static final long serialversionUID = 129348938L;

    public String subscription;
    public IpPort conection;

    public Notification(String subscription, IpPort conection) {
        this.subscription = subscription;
        this.conection = conection;
    }
    
    @Override
    public String toString() {
        return "{" +
            " subscription='" + this.subscription + "'" +
            ", conection='" + this.conection + "'" + "',\n" +
            super.toString() + 
            "}";
    }

}