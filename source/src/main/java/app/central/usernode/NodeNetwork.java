package app.central.usernode;


import java.util.*;

public class NodeNetwork {
    
    public String host;
    public long pubPort;
    public long replyPort;
    public long pullPort;

    public NodeNetwork() {
    }

    public NodeNetwork(long pubPort, long replyPort, long pullPort) {
        this.pubPort = pubPort;
        this.replyPort = replyPort;
        this.pullPort = pullPort;
        //default
        this.host = "localhost";
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    public long getPubPort() {
        return this.pubPort;
    }

    public void setPubPort(long pubPort) {
        this.pubPort = pubPort;
    }

    public long getReplyPort() {
        return this.replyPort;
    }

    public void setReplyPort(long replyPort) {
        this.replyPort = replyPort;
    }

    public long getPullPort() {
        return this.pullPort;
    }

    public void setPullPort(long pullPort) {
        this.pullPort = pullPort;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof NodeNetwork)) {
            return false;
        }
        NodeNetwork network = (NodeNetwork) o;
        return Objects.equals(host, network.host) && pubPort == network.pubPort && replyPort == network.replyPort && pullPort == network.pullPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, pubPort, replyPort, pullPort);
    }

    @Override
    public String toString() {
        return "{" +
            " host='" + getHost() + "'" +
            ", pubPort='" + getPubPort() + "'" +
            ", replyPort='" + getReplyPort() + "'" +
            ", pullPort='" + getPullPort() + "'" +
            "}";
    }


}