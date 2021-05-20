
package app.central.usernode;

import java.util.List;

public class UserNode {
    public String username;
    public Network network;
    public boolean online;
    public List<String> subscribers;
    public List<String> subscriptions;
    public List<Connection> connections;



    public UserNode(String username, Network network, boolean online, List<String> subscribers, List<String> subscriptions, List<Connection> connections) {
        this.username = username;
        this.network = network;
        this.online = online;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
        this.connections = connections;
    }



}