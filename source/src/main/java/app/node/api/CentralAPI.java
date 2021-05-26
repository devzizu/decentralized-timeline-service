package app.node.api;

import java.util.concurrent.CompletableFuture;

import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;
import app.exchange.ServiceConstants;
import app.exchange.req.LoginRequest;
import app.exchange.req.RegisterRequest;
import app.node.persist.NodeDatabase;
import app.node.services.FutureResponses;
import app.node.services.NodeService;
import app.util.config.ConfigReader;
import app.util.data.Serialization;
import io.atomix.utils.net.Address;

public class CentralAPI {
    
    private NodeService nodeService;
    private NodeDatabase nodeDatabase;
    private NodeNetwork nodeNetwork;
    private Address centralAddress;
    private ConfigReader config;

    public CentralAPI(ConfigReader config, NodeService service, NodeDatabase nodeDatabase, NodeNetwork nodeNetwork) {

        this.nodeService = service;
        this.nodeDatabase = nodeDatabase;
        this.nodeNetwork = nodeNetwork;
        this.centralAddress = Address.from(config.getString("central", "main_address_host") + ":" + config.getLong("central", "main_address_atomix_port"));
        this.config = config;
    }

    public CompletableFuture<MessageWrapper> central_login() {

        FutureResponses futureResponses = this.nodeService.getFutureResponses();

        try {

            LoginRequest loginRequest = new LoginRequest(nodeDatabase.node_id, nodeNetwork);
            
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

            RegisterRequest registerRequest = new RegisterRequest(nodeDatabase.node_id, nodeNetwork);
            
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

            RegisterRequest registerRequest = new RegisterRequest(nodeDatabase.node_id, nodeNetwork);
            
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
}
