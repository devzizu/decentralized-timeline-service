package app.central.usernode;

import java.io.Serializable;

public class Connection implements Serializable {

    private static final long serialVersionUID = 1234567L;
    
    //it is the node that is subscribed by the origin node(it can be the same node as this one)
    public String origin_node;
    //it is connected to this node because wants to receive messages from the orgin node
    public String dependent_node;


    public Connection(String origin_node, String dependent_node) {
        this.origin_node = origin_node;
        this.dependent_node = dependent_node;
    }

    @Override
    public String toString() {
        return "{" +
            " origin_node='" + this.origin_node + "'" +
            ", dependent_node='" + this.dependent_node + "'" +
            "}";
    }

}
    
