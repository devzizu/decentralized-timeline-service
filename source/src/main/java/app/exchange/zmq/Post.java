package app.exchange.zmq;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

public class Post implements Serializable, Comparable<Post> {
    
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
        return "post ("+this.nodeID+") = {" +
            " msg='" + this.message + "'" +
            ", clocks='" + this.subscriptionClocks + "'" +
            "}";
    }

    @Override
    public int compareTo(Post p) {
        if(this.nodeID.equals(p.nodeID)) {
            return Long.compare(this.subscriptionClocks.get(this.nodeID),p.subscriptionClocks.get(p.nodeID));
        }
        return -1;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Post){
            Post p = (Post) o;
            if( p.nodeID.equals(this.nodeID) && p.subscriptionClocks.get(p.nodeID) == this.subscriptionClocks.get(this.nodeID)) 
                return true;
        }
        return false;
    }
}
