package app.exchange.res;

import app.exchange.MessageWrapper;

import app.central.usernode.IpPort;

public class SubscribeResponse extends MessageWrapper {

    public IpPort connectionForPub;

    public SubscribeResponse(IpPort connectionForPub) {
        this.connectionForPub = connectionForPub;
    }    
}
