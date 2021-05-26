package app.node.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import app.central.usernode.NodeNetwork;
import app.util.config.ConfigReader;
import app.exchange.ServiceConstants;
import app.exchange.res.LoginResponse;
import app.exchange.res.RegisterResponse;
import app.util.data.Serialization;
import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

public class NodeService {
    
    private ScheduledExecutorService executorService;
    private NettyMessagingService messagingService;
    private FutureResponses centralResponses;
    private String nodeID;
    private NodeNetwork nodeNetwork;
    private ConfigReader config;

    private static String nodeServiceID;

    public NodeService(ConfigReader config, String nodeID, NodeNetwork nodeNetwork) {

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

        this.register_central_login_response();
        this.register_central_register_response();
    }

    public void register_central_login_response() {

        this.messagingService.registerHandler(ServiceConstants.CENTRAL_LOGIN_RESPONSE, (address, requestBytes) -> {

            try {

                LoginResponse loginResponse = null;

                loginResponse = (LoginResponse) Serialization.deserialize(requestBytes);
                
                int messageID = this.centralResponses.complete(loginResponse);

                System.out.println("(node:"+this.nodeID+") received " + ServiceConstants.CENTRAL_LOGIN_RESPONSE + " for messageID " + messageID);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_central_register_response() {

        this.messagingService.registerHandler(ServiceConstants.CENTRAL_REGISTER_RESPONSE, (address, requestBytes) -> {

            try {

                RegisterResponse registerResponse = null;

                registerResponse = (RegisterResponse) Serialization.deserialize(requestBytes);
                
                int messageID = this.centralResponses.complete(registerResponse);

                System.out.println("(node:"+this.nodeID+") received " + ServiceConstants.CENTRAL_REGISTER_RESPONSE + " for messageID " + messageID);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void sendBytesAsync(byte[] bytes, String type, Address address) {

        this.messagingService.sendAsync(address, type, bytes)
        .thenRun(() -> {
            System.out.println("(node:"+type+") requesting to " + address.toString());
        });
    }

    public FutureResponses getFutureResponses() {

        return this.centralResponses;
    }
}
