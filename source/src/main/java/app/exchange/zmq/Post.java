package app.exchange.zmq;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

public class Post implements Serializable {
    
    public String nodeID;
    public String message;
    public ConcurrentHashMap<String, Long> subscriptionClocks;

    public Post(String nodeID, String message, ConcurrentHashMap<String, Long> subscriptionClocks) {
        this.message = message;
        this.subscriptionClocks = subscriptionClocks;
        this.nodeID = nodeID;
    }

    public String toJSON() {

        return (new Gson()).toJson(this);
    }

    public static Post fromJSON(String json) {

        return (new Gson()).fromJson(json, Post.class);
    }

    @Override
    public String toString() {
        return "Notification ("+this.nodeID+") = {" +
            " postMessage='" + this.message + "'" +
            ", subscriptionClocks='" + this.subscriptionClocks + "'" +
            "}";
    }
}
