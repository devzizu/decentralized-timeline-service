package app.exchange.res;

import app.exchange.MessageWrapper;

import app.central.usernode.IpPort;

public class SubscribeResponse extends MessageWrapper {

    public IpPort connectionForPub;
    public IpPort connectionForReq;

    public SubscribeResponse(IpPort connectionForPub,IpPort connectionForReq) {
        this.connectionForPub = connectionForPub;
        this.connectionForReq = connectionForReq;
    }    

    @Override
    public String toString() {
        return "{" +
            " connectionForPub='" + this.connectionForPub + "',\n" +
            " connectionForReq='" + this.connectionForReq + "',\n" +
            super.toString() + 
            "\n}";
    }

}
