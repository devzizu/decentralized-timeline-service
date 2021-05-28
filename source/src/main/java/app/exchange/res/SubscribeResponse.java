package app.exchange.res;

import app.exchange.MessageWrapper;

import app.central.usernode.IpPort;

public class SubscribeResponse extends MessageWrapper {

    public IpPort connectionForPub;

    public SubscribeResponse(IpPort connectionForPub) {
        this.connectionForPub = connectionForPub;
    }    

    @Override
    public String toString() {
        return "{" +
            " connectionForPub='" + this.connectionForPub + "',\n" +
            super.toString() + 
            "\n}";
    }

}
