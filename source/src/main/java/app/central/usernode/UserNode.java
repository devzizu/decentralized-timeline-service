
package app.central.usernode;

import java.io.Serializable;
import java.util.List;

public class UserNode implements Serializable {

    private static final long serialVersionUID = 1234567L;

    public String username;
    public NodeNetwork network;
    public boolean online;
    public List<String> subscribers;
    public List<String> subscriptions;
    public List<Connection> connections;

    public Session lastSession;
    public double averageUpTime;
    public int numberOfSessions;

    public UserNode(String username, NodeNetwork network, boolean online, List<String> subscribers, List<String> subscriptions, List<Connection> connections) {
        this.username = username;
        this.network = network;
        this.online = online;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
        this.connections = connections;
        this.lastSession = null;
        this.numberOfSessions = 0;
    }

    public void setSession(Session s) {
        this.lastSession = s;
    }

    public void incrementSessions() {
        this.numberOfSessions++;
    }

    public void updateAverageUpTime() {
        this.averageUpTime = (this.averageUpTime*(this.numberOfSessions-1) + this.lastSession.getTimeDiffSeconds())/this.numberOfSessions; 
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + this.username + "'" +
            ", network='" + this.network.toString() + "'" +
            ", online='" + this.online + "'" +
            ", subscribers='" + this.subscribers + "'" +
            ", subscriptions='" + this.subscriptions + "'" +
            ", connections='" + this.connections + "'" +
            "}";
    }

}