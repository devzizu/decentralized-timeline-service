package app.central.usernode;

public class Connection {
    
    //it is the node that is subscribed by the origin node(it can be the same node as this one)
    public String origin_node;
    //it is connected to this node because wants to receive messages from the orgin node
    public String dependent_node;


    public Connection(String origin_node, String dependent_node) {
        this.origin_node = origin_node;
        this.dependent_node = dependent_node;
    }
}
    
