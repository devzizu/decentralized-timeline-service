package app.node.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.*;

import app.central.usernode.NodeNetwork;
import app.util.config.ConfigReader;
import app.exchange.ServiceConstants;
import app.exchange.req.*;
import app.exchange.res.*;
import app.exchange.zmq.*;
import app.node.persist.NodeDatabase;
import app.util.data.Serialization;
import app.util.gui.GUI;
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
    private NodeDatabase nodeDatabase;

    private static String nodeServiceID;

    public NodeService(ConfigReader config, String nodeID, NodeNetwork nodeNetwork,NodeDatabase nodeDatabase) {

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
        this.nodeDatabase = nodeDatabase;
    }

    public void start() {

        this.registerHandlers();

        this.messagingService.start();
    }

    public void registerHandlers() {

        this.register_central_login_response();
        this.register_central_register_response();
        this.register_central_logout_response();
        this.register_central_subscribe_response();
        this.register_peer_clock_response();
        this.register_peer_clock_request();
        this.register_peer_recover_request();
        this.register_peer_recover_response();
    }

    public void register_peer_recover_response() {

        this.messagingService.registerHandler(ServiceConstants.PEER_RECOVER_RESPONSE, (address, requestBytes) -> {

            try {

                RecoverResponse recoverResponse = null;

                recoverResponse = (RecoverResponse) Serialization.deserialize(requestBytes);
                
                this.centralResponses.complete(recoverResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_peer_recover_request() {

        this.messagingService.registerHandler(ServiceConstants.PEER_RECOVER_REQUEST, (address, requestBytes) -> {

            try {

                GUI.showMessageFromNode(nodeID, "GOT RECOVER REQ 1");

                RecoverRequest recoverRequest = null;

                recoverRequest = (RecoverRequest) Serialization.deserialize(requestBytes);

                final long clockVal = recoverRequest.clock;

                GUI.showMessageFromNode(nodeID, "GOT RECOVER REQ 2");

                TreeSet<Post> posts = null;

                if (recoverRequest.nodeID.equals(this.nodeID))
                    posts = nodeDatabase.myTimelineList.stream().collect(Collectors.toCollection(() -> new TreeSet<Post>()));
                else
                    posts = nodeDatabase.otherNodeMessages.get(recoverRequest.nodeID);

                RecoverResponse response = new RecoverResponse(recoverRequest.nodeID,posts.stream().filter(p -> p.subscriptionClocks.get(p.nodeID) > clockVal).collect(Collectors.toCollection(() -> new TreeSet<Post>())));

                response.messageID = recoverRequest.messageID;

                sendBytesAsync(Serialization.serialize(response),ServiceConstants.PEER_RECOVER_RESPONSE,address);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_central_login_response() {

        this.messagingService.registerHandler(ServiceConstants.CENTRAL_LOGIN_RESPONSE, (address, requestBytes) -> {

            try {

                LoginResponse loginResponse = null;

                loginResponse = (LoginResponse) Serialization.deserialize(requestBytes);
                
                this.centralResponses.complete(loginResponse);

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
                
                this.centralResponses.complete(registerResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_central_logout_response() {

        this.messagingService.registerHandler(ServiceConstants.CENTRAL_LOGOUT_RESPONSE, (address, requestBytes) -> {

            try {

                LogoutResponse logoutResponse = null;

                logoutResponse = (LogoutResponse) Serialization.deserialize(requestBytes);
                
                this.centralResponses.complete(logoutResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_central_subscribe_response() {

        this.messagingService.registerHandler(ServiceConstants.CENTRAL_SUBSCRIBE_RESPONSE, (address, requestBytes) -> {

            try {

                SubscribeResponse subResponse = null;

                subResponse = (SubscribeResponse) Serialization.deserialize(requestBytes);
                
                this.centralResponses.complete(subResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_peer_clock_request(){
        this.messagingService.registerHandler(ServiceConstants.PEER_CLOCK_REQUEST, (address, requestBytes) -> {

            try {

                ClockRequest clockRequest = null;

                clockRequest = (ClockRequest) Serialization.deserialize(requestBytes);
                
                ClockResponse response = new ClockResponse(nodeDatabase.subscriptionClocks.get(clockRequest.nodeID),clockRequest.nodeID);

                response.messageID = clockRequest.messageID;

                sendBytesAsync(Serialization.serialize(response),ServiceConstants.PEER_CLOCK_RESPONSE,address);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);

    }

    public void register_peer_clock_response() {

        this.messagingService.registerHandler(ServiceConstants.PEER_CLOCK_RESPONSE, (address, requestBytes) -> {

            try {

                ClockResponse clockResponse = null;

                clockResponse = (ClockResponse) Serialization.deserialize(requestBytes);
                
                this.centralResponses.complete(clockResponse);

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
