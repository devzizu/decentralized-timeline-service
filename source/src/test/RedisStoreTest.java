package app;

import java.util.Arrays;

import app.central.usernode.Connection;
import app.central.usernode.Network;
import app.central.usernode.UserNode;

public class RedisStoreTest {

    static RedisUtils redis = new RedisUtils();

    public static void main(String[] args) {

        // Create example node

        Network nodeNetwork = new Network();
        nodeNetwork.setHost("localhost");
        nodeNetwork.setPubPort(10000);
        nodeNetwork.setPullPort(10001);
        nodeNetwork.setReplyPort(10002);
        String nodeID = "node1";
        UserNode nodeContent = new UserNode(nodeID, nodeNetwork, true, Arrays.asList("node2"),
                Arrays.asList("node3", "node4"), Arrays.asList(new Connection("node2", "node3")));

        redis.setNode(nodeID, nodeContent);

        UserNode getNodeContent = redis.getNode(nodeID);

        System.out.println(getNodeContent);

        redis.closePool();
    }
}
