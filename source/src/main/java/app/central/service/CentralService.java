package app.central.service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import app.central.usernode.CentralNetwork;
import app.central.util.CentralUtils;
import app.config.ConfigReader;
import app.exchange.req.LoginRequest;
import app.exchange.res.LoginResponse;
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

        centralServiceID = config.getString("central", "netty_service_node_id") + "_" + centralID;

        this.executorService = Executors.newScheduledThreadPool(centralThreadPoolSize.intValue());
        this.messagingService = new NettyMessagingService(centralID, Address.from((int) centralNetwork.replyPort), new MessagingConfig());
    }

    public void start() {

        this.registerHandlers();

        this.messagingService.start();
    }

    public void registerHandlers() {

        this.register_node_login_request();
    }

    public void sendBytesAsync(byte[] bytes, String type, Address address) {

        this.messagingService.sendAsync(address, type, bytes)
        .thenRun(() -> {
            System.out.println("(central:"+type+") responding back to " + address.toString());
        });
    }

    public void register_node_login_request() {

        this.messagingService.registerHandler("node_login_request", (address, requestBytes) -> {

            try {

                LoginRequest loginRequest = null;

                loginRequest = (LoginRequest) Serialization.deserialize(requestBytes);

                LoginResponse loginResponse = centralUtils.login_node(loginRequest);

                byte[] responseBytes = Serialization.serialize(loginResponse);

                this.sendBytesAsync(responseBytes, "central_login_response", address);
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, this.executorService);
    }
}
