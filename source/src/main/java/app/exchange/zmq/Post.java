package app.exchange.zmq;

import java.io.Serializable;
import java.util.Map;

import com.google.gson.Gson;

public class Post implements Serializable {
    
    private String message;
    private Map<String, Long> subscriptionClocks;

    public Post(String message, Map<String, Long> subscriptionClocks) {
        this.message = message;
        this.subscriptionClocks = subscriptionClocks;
    }

    public String toJSON() {

        return (new Gson()).toJson(this);
    }

    public static Post fromJSON(String json) {

        return (new Gson()).fromJson(json, Post.class);
    }

    @Override
    public String toString() {
        return "Notification = {" +
            " postMessage='" + this.message + "'" +
            ", subscriptionClocks='" + this.subscriptionClocks + "'" +
            "}";
    }
}
