package app.central.usernode;


import java.net.*;
import java.util.*;

public class Network {
    
    public InetAddress pubHost;
    public int pubPort;
    public InetAddress replyHost;
    public int replyPort;
    public InetAddress pullHost;
    public int pullPort;

    public Network() {
    }

    public Network(InetAddress pubHost, int pubPort, InetAddress replyHost, int replyPort, InetAddress pullHost, int pullPort) {
        this.pubHost = pubHost;
        this.pubPort = pubPort;
        this.replyHost = replyHost;
        this.replyPort = replyPort;
        this.pullHost = pullHost;
        this.pullPort = pullPort;
    }

    public InetAddress getPubHost() {
        return this.pubHost;
    }

    public void setPubHost(InetAddress pubHost) {
        this.pubHost = pubHost;
    }

    public int getPubPort() {
        return this.pubPort;
    }

    public void setPubPort(int pubPort) {
        this.pubPort = pubPort;
    }

    public InetAddress getReplyHost() {
        return this.replyHost;
    }

    public void setReplyHost(InetAddress replyHost) {
        this.replyHost = replyHost;
    }

    public int getReplyPort() {
        return this.replyPort;
    }

    public void setReplyPort(int replyPort) {
        this.replyPort = replyPort;
    }

    public InetAddress getPullHost() {
        return this.pullHost;
    }

    public void setPullHost(InetAddress pullHost) {
        this.pullHost = pullHost;
    }

    public int getPullPort() {
        return this.pullPort;
    }

    public void setPullPort(int pullPort) {
        this.pullPort = pullPort;
    }

    public Network pubHost(InetAddress pubHost) {
        setPubHost(pubHost);
        return this;
    }

    public Network pubPort(int pubPort) {
        setPubPort(pubPort);
        return this;
    }

    public Network replyHost(InetAddress replyHost) {
        setReplyHost(replyHost);
        return this;
    }

    public Network replyPort(int replyPort) {
        setReplyPort(replyPort);
        return this;
    }

    public Network pullHost(InetAddress pullHost) {
        setPullHost(pullHost);
        return this;
    }

    public Network pullPort(int pullPort) {
        setPullPort(pullPort);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Network)) {
            return false;
        }
        Network network = (Network) o;
        return Objects.equals(pubHost, network.pubHost) && pubPort == network.pubPort && Objects.equals(replyHost, network.replyHost) && replyPort == network.replyPort && Objects.equals(pullHost, network.pullHost) && pullPort == network.pullPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pubHost, pubPort, replyHost, replyPort, pullHost, pullPort);
    }

    @Override
    public String toString() {
        return "{" +
            " pubHost='" + getPubHost() + "'" +
            ", pubPort='" + getPubPort() + "'" +
            ", replyHost='" + getReplyHost() + "'" +
            ", replyPort='" + getReplyPort() + "'" +
            ", pullHost='" + getPullHost() + "'" +
            ", pullPort='" + getPullPort() + "'" +
            "}";
    }


}