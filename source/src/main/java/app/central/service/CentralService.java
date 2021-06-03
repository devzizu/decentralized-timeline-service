package app.central.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import app.central.usernode.CentralNetwork;
import app.central.util.CentralUtils;
import app.util.config.ConfigReader;
import app.exchange.ServiceConstants;
import app.exchange.req.LoginRequest;
import app.exchange.req.LogoutRequest;
import app.exchange.req.RegisterRequest;
import app.exchange.req.SubscribeRequest;
import app.exchange.res.LoginResponse;
import app.exchange.res.LogoutResponse;
import app.exchange.res.RegisterResponse;
import app.exchange.res.SubscribeResponse;
import app.util.data.Serialization;
import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

public class CentralService {

    // messaging service
    private ScheduledExecutorService executorService;
    private NettyMessagingService messagingService;
    private String centralServiceID;

    // central network and api utils
    private CentralNetwork centralNetwork;
    private CentralUtils centralUtils;

    public CentralService(ConfigReader config, String centralID, CentralNetwork centralNetwork, CentralUtils centralUtils) {

        Long centralThreadPoolSize = config.getLong("central", "netty_service_thread_pool");

        this.centralServiceID = config.getString("central", "netty_service_node_id") + "_" + centralID;

        this.centralNetwork = centralNetwork;
        this.executorService = Executors.newScheduledThreadPool(centralThreadPoolSize.intValue());
        this.messagingService = new NettyMessagingService(this.centralServiceID, Address.from((int) this.centralNetwork.replyPort), new MessagingConfig());

        this.centralUtils = centralUtils;
    }

    public void start() {

        this.registerHandlers();

        this.messagingService.start();
    }

    public void registerHandlers() {

        this.register_node_login_request();
        this.register_node_register_request();
        this.register_node_logout_request();
        this.register_node_subscribe_request();
    }

    public void sendBytesAsync(byte[] bytes, String type, Address address) {

        this.messagingService.sendAsync(address, type, bytes)
        .thenRun(() -> {
            System.out.println("(central:"+type+") responding back to " + address.toString());
        });
    }

    public void register_node_login_request() {

        this.messagingService.registerHandler(ServiceConstants.NODE_LOGIN_REQUEST, (address, requestBytes) -> {

            try {

                LoginRequest loginRequest = null;

                loginRequest = (LoginRequest) Serialization.deserialize(requestBytes);

                LoginResponse loginResponse = centralUtils.login_node(loginRequest);

                loginResponse.messageID = loginRequest.messageID;
                
                byte[] responseBytes = Serialization.serialize(loginResponse);

                this.sendBytesAsync(responseBytes, ServiceConstants.CENTRAL_LOGIN_RESPONSE, address);
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_node_register_request() {

        this.messagingService.registerHandler(ServiceConstants.NODE_REGISTER_REQUEST, (address, requestBytes) -> {

            try {

                RegisterRequest registerRequest = null;

                registerRequest = (RegisterRequest) Serialization.deserialize(requestBytes);
                
                RegisterResponse registerResponse = centralUtils.register_node(registerRequest);

                registerResponse.messageID = registerRequest.messageID;
                
                byte[] responseBytes = Serialization.serialize(registerResponse);

                this.sendBytesAsync(responseBytes, ServiceConstants.CENTRAL_REGISTER_RESPONSE, address);
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_node_logout_request() {

        this.messagingService.registerHandler(ServiceConstants.NODE_LOGOUT_REQUEST, (address, requestBytes) -> {

            try {

                LogoutRequest logoutRequest = null;

                logoutRequest = (LogoutRequest) Serialization.deserialize(requestBytes);
                
                LogoutResponse logoutResponse = centralUtils.logout_node(logoutRequest);

                logoutResponse.messageID = logoutRequest.messageID;
                
                byte[] responseBytes = Serialization.serialize(logoutResponse);

                this.sendBytesAsync(responseBytes, ServiceConstants.CENTRAL_LOGOUT_RESPONSE, address);
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }

    public void register_node_subscribe_request() {

        this.messagingService.registerHandler(ServiceConstants.NODE_SUBSCRIBE_REQUEST, (address, requestBytes) -> {

            try {

                SubscribeRequest subRequest = null;

                subRequest = (SubscribeRequest) Serialization.deserialize(requestBytes);
                
                SubscribeResponse subResponse = centralUtils.subscribe_node(subRequest);

                subResponse.messageID = subRequest.messageID;
                
                byte[] responseBytes = Serialization.serialize(subResponse);

                this.sendBytesAsync(responseBytes, ServiceConstants.CENTRAL_SUBSCRIBE_RESPONSE, address);
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }
}
