package app.central.usernode;


import java.util.*;

public class CentralNetwork {
    
    public String host;
    public long replyPort;

    public CentralNetwork() {
    }

    public CentralNetwork(long replyPort) {
        this.replyPort = replyPort;
        //default
        this.host = "localhost";
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    public long getReplyPort() {
        return this.replyPort;
    }

    public void setReplyPort(long replyPort) {
        this.replyPort = replyPort;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CentralNetwork)) {
            return false;
        }
        CentralNetwork centralNetwork = (CentralNetwork) o;
        return Objects.equals(host, centralNetwork.host) && replyPort == centralNetwork.replyPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, replyPort);
    }

    @Override
    public String toString() {
        return "{" +
            " host='" + getHost() + "'" +
            ", replyPort='" + getReplyPort() + "'" +
            "}";
    }

}