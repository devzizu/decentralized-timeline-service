package app.node.api;

import java.util.concurrent.CompletableFuture;

import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;
import app.exchange.ServiceConstants;
import app.exchange.req.ClockRequest;
import app.exchange.req.LoginRequest;
import app.exchange.req.LogoutRequest;
import app.exchange.req.RecoverRequest;
import app.exchange.req.RegisterRequest;
import app.exchange.req.SubscribeRequest;
import app.node.persist.NodeDatabase;
import app.node.services.FutureResponses;
import app.node.services.NodeService;
import app.util.config.ConfigReader;
import app.util.data.Serialization;
import io.atomix.utils.net.Address;

public class GeneralAPI {
    
    private NodeService nodeService;
    private NodeDatabase nodeDatabase;
    private NodeNetwork nodeNetwork;
    private Address centralAddress;
    private ConfigReader config;

    public GeneralAPI(ConfigReader config, NodeService service, NodeDatabase nodeDatabase, NodeNetwork nodeNetwork) {

        this.nodeService = service;
        this.nodeDatabase = nodeDatabase;
        this.nodeNetwork = nodeNetwork;
        this.config = config;
        this.centralAddress = Address.from(this.config.getString("central", "main_address_host") + ":" + config.getLong("central", "main_address_atomix_port"));
    }

    public CompletableFuture<MessageWrapper> peer_recover_node(String nodeID, long lastClock, Address peerAddress) {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            RecoverRequest recoverRequest = new RecoverRequest(nodeID, lastClock);
            
            recoverRequest.messageID = futureResponses.getId();

            byte[] requestBytes = Serialization.serialize(recoverRequest);

            this.nodeService.sendBytesAsync(requestBytes, ServiceConstants.PEER_RECOVER_REQUEST, peerAddress);

            CompletableFuture<MessageWrapper> recoverResponseFuture = new CompletableFuture<>();

            futureResponses.addPending(recoverResponseFuture);

            return recoverResponseFuture;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<MessageWrapper> peer_get_clock(String nodeID,Address endereco) {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            ClockRequest clockRequest = new ClockRequest(nodeID);
            
            clockRequest.messageID = futureResponses.getId();

            byte[] requestBytes = Serialization.serialize(clockRequest);

            this.nodeService.sendBytesAsync(requestBytes, ServiceConstants.PEER_CLOCK_REQUEST, endereco);

            CompletableFuture<MessageWrapper> clockResponseFuture = new CompletableFuture<>();

            futureResponses.addPending(clockResponseFuture);

            return clockResponseFuture;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<MessageWrapper> central_login() {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            LoginRequest loginRequest = new LoginRequest(nodeDatabase.getNodeID(), nodeNetwork);
            
            loginRequest.messageID = futureResponses.getId();

            byte[] requestBytes = Serialization.serialize(loginRequest);

            this.nodeService.sendBytesAsync(requestBytes, ServiceConstants.NODE_LOGIN_REQUEST, this.centralAddress);

            CompletableFuture<MessageWrapper> loginResponseFuture = new CompletableFuture<>();

            futureResponses.addPending(loginResponseFuture);

            return loginResponseFuture;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<MessageWrapper> central_register() {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            RegisterRequest registerRequest = new RegisterRequest(nodeDatabase.getNodeID(), nodeNetwork);
            
            registerRequest.messageID = futureResponses.getId();

            byte[] requestBytes = Serialization.serialize(registerRequest);

            this.nodeService.sendBytesAsync(requestBytes, ServiceConstants.NODE_REGISTER_REQUEST, this.centralAddress);

            CompletableFuture<MessageWrapper> registerResponseFuture = new CompletableFuture<>();

            futureResponses.addPending(registerResponseFuture);

            return registerResponseFuture;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<MessageWrapper> central_logout() {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            LogoutRequest logoutRequest = new LogoutRequest(nodeDatabase.getNodeID());
            
            logoutRequest.messageID = futureResponses.getId();

            byte[] requestBytes = Serialization.serialize(logoutRequest);

            this.nodeService.sendBytesAsync(requestBytes, ServiceConstants.NODE_LOGOUT_REQUEST, this.centralAddress);

            CompletableFuture<MessageWrapper> logoutResponseFuture = new CompletableFuture<>();

            futureResponses.addPending(logoutResponseFuture);

            return logoutResponseFuture;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<MessageWrapper> central_subscribe(String subNodeID) {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            SubscribeRequest subRequest = new SubscribeRequest(nodeDatabase.getNodeID(), subNodeID);
            
            System.out.println(subRequest.toString());

            subRequest.messageID = futureResponses.getId();

            byte[] requestBytes = Serialization.serialize(subRequest);

            this.nodeService.sendBytesAsync(requestBytes, ServiceConstants.NODE_SUBSCRIBE_REQUEST, this.centralAddress);

            CompletableFuture<MessageWrapper> subResponseFuture = new CompletableFuture<>();

            futureResponses.addPending(subResponseFuture);

            return subResponseFuture;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
