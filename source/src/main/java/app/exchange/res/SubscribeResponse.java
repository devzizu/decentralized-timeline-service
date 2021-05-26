package app.exchange.res;

import java.io.Serializable;

import app.exchange.MessageWrapper;

import app.central.usernode.IpPort;

public class SubscribeResponse extends MessageWrapper implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public IpPort connectionForPub;

    public SubscribeResponse(IpPort connectionForPub) {
        this.connectionForPub = connectionForPub;
    }

    
}
