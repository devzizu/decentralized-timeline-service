package app.exchange.zmq;

import app.central.usernode.IpPort;
import app.exchange.MessageWrapper;


public class Notification extends MessageWrapper {

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