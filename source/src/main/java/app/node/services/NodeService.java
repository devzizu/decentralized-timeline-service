package app.node.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import app.central.usernode.Network;
import app.config.ConfigReader;
import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

public class NodeService {
    
    private ScheduledExecutorService executorService;
    private NettyMessagingService messagingService;
    private FutureResponses centralResponses;
    private Long nodeID;
    private Network nodeNetwork;
    private ConfigReader config;

    private static String nodeServiceID;

    public NodeService(ConfigReader config, Long nodeID, Network nodeNetwork) {

        // node information
        this.nodeID = nodeID;
        this.nodeNetwork = nodeNetwork;
        nodeServiceID = config.getString("node", "netty_service_node_id") + "_" + nodeID;
        Long nodeThreadPoolSize = config.getLong("node", "netty_service_thread_pool");

        // reply: configure netty service
        this.executorService = Executors.newScheduledThreadPool(nodeThreadPoolSize.intValue());
        this.messagingService = new NettyMessagingService(nodeServiceID, Address.from((int) nodeNetwork.replyPort), new MessagingConfig());
        
        // responses from central futures
        this.centralResponses = new FutureResponses();
    }


    public void start() {

        this.registerHandlers();

        this.messagingService.start();
    }

    public void registerHandlers() {

        // add handlers
    }
}
