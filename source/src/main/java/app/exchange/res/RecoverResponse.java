package app.exchange.res;

import app.exchange.MessageWrapper;
import app.exchange.zmq.Post;
import java.util.TreeSet;

public class RecoverResponse extends MessageWrapper {

    // requested node clock
    public String nodeID;
    public TreeSet<Post> posts;

    public RecoverResponse(String nodeID,TreeSet<Post> posts) {
        this.nodeID = nodeID;
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "RecoverResponse = {" +
            " nodeID='" + this.nodeID + "'" +
            ", posts='" + this.posts + "'" +
            "}";
    }

}
