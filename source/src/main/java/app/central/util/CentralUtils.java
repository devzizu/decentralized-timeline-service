package app.central.util;

import java.time.LocalDateTime;
import java.util.*;

import app.central.store.RedisUtils;
import app.central.usernode.*;
import app.central.zmq.*;
import app.exchange.req.*;
import app.exchange.res.*;

public class CentralUtils {

    private RedisUtils redisConnector;

    public CentralUtils(RedisUtils redisConnector) {
        
        this.redisConnector = redisConnector;
    }

    //used for registering a new user
    public RegisterResponse register_node(RegisterRequest req){

        String username = req.nodeId;
        NodeNetwork network = req.network;

        RegisterResponse res = new RegisterResponse();

        if (redisConnector.hasNode(username)) {

            res.setStatusCode(false);
            res.setStatusMessage("node already exists");

        } else {
            UserNode user = new UserNode(username, network, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            user.lastSession = new Session(); 
            user.lastSession.setTimeStart(LocalDateTime.now());
            user.incrementSessions();

            redisConnector.setNode(username,user);
            res.setStatusCode(true);
            res.setStatusMessage("node created");    
        }
        
        return res;
    }

    //used for registering a new user
    public LoginResponse login_node(LoginRequest loginRequest) {
        
        UserNode user = redisConnector.getNode(loginRequest.nodeID);
        
        System.out.println(user);

        if(user==null || user.online) {
            LoginResponse res = new LoginResponse();
            res.setStatusCode(false);
            res.setStatusMessage("could not find user or user is already online");
            return res;
        } else {
            user.online = true;
            user.network = loginRequest.network;
            user.connections.clear();

            user.lastSession = new Session(); 
            user.lastSession.setTimeStart(LocalDateTime.now());
            user.incrementSessions();

            Set<IpPort> connecting = new HashSet<>();
            Map<String,IpPort> recoverPorts = new HashMap<>();

            for(String subscription : user.subscriptions){
                
                String electedNodeID = electNode2Connect(subscription);
                
                if (redisConnector.hasNode(electedNodeID)) {
                    
                    UserNode electedNode = redisConnector.getNode(electNode2Connect(subscription));
                    electedNode.connections.add(new Connection(subscription, loginRequest.nodeID));
                    connecting.add(new IpPort(electedNode.network.host,electedNode.network.pubPort));

                    recoverPorts.put(subscription, new IpPort(electedNode.network.host,electedNode.network.replyPort)); //porta de reply para fazer o recover da subscrição
                }
            }

            redisConnector.setNode(loginRequest.nodeID, user);

            LoginResponse res = new LoginResponse(connecting, recoverPorts);

            res.setStatusCode(true);
            res.setStatusMessage("login ok");

            return res;
        }
    }

    public SubscribeResponse subscribe_node(SubscribeRequest request) {

        String subscriber = request.nodeId;
        String subscription = request.subscription;

        UserNode userSubscriber = redisConnector.getNode(subscriber);
        UserNode userSubscription = redisConnector.getNode(subscription);

        if(userSubscriber==null || userSubscription==null || !(userSubscriber.online)){
            
            SubscribeResponse subRes = new SubscribeResponse(null);

            subRes.setStatusCode(false);
            subRes.setStatusMessage("oops something went wrong! mirs on the way!");
            
            return subRes;

        } else {

            if (userSubscriber.subscriptions.contains(subscription)) {
                
                SubscribeResponse subRes = new SubscribeResponse(null);

                subRes.setStatusCode(false);
                subRes.setStatusMessage("already subscribed");
            
                return subRes;
            
            } else {

                String electedUsername = electNode2Connect(subscription);
                
                UserNode electedNode = null;
                if (!electedUsername.equals(userSubscription.username)) {    
                    electedNode = redisConnector.getNode(electedUsername);                                                 
                } else {
                    electedNode = userSubscription;
                }

                userSubscriber.subscriptions.add(subscription);
                userSubscription.subscribers.add(subscriber);
                
                electedNode.connections.add(new Connection(subscription,subscriber));                

                redisConnector.setNode(userSubscriber.username, userSubscriber);
                redisConnector.setNode(userSubscription.username, userSubscription);
                
                if (!electedNode.username.equals(userSubscription.username))
                    redisConnector.setNode(electedNode.username, electedNode); 
                
                SubscribeResponse subRes = new SubscribeResponse(new IpPort(electedNode.network.host,electedNode.network.pubPort));
                subRes.setStatusCode(true);
                subRes.setStatusMessage("sub ok");

                return subRes;
            }
        }
    }

    public LogoutResponse logout_node(LogoutRequest request){
        String username = request.nodeId;

        UserNode user = redisConnector.getNode(username);

        if(user==null || !user.online){ 
            LogoutResponse res = new LogoutResponse(username);
            res.setStatusCode(false);
            res.setStatusMessage("something went wrong!");
            return res;
        }

        user.online = false;

        user.lastSession.setTimeEnd(LocalDateTime.now());
        user.updateAverageUpTime();

        redisConnector.setNode(username, user);
        
        for(Connection c : user.connections){
            //TODO temos que ter em atenção que este nodo que está a fazer logout não pode ser considerado na eleição
            String newConnection = electNode2Connect(c.origin_node);

            UserNode nc = redisConnector.getNode(newConnection);

            UserNode dependent = redisConnector.getNode(c.dependent_node);

            IpPort newPorts = new IpPort(nc.network.host,nc.network.pubPort);

            NotifyNode.notify(dependent.network.host, dependent.network.pullPort, c.origin_node, newPorts);

            nc.connections.add(new Connection(c.origin_node,c.dependent_node));

            redisConnector.setNode(newConnection, nc);
        }
        
        LogoutResponse response = new LogoutResponse(username);
        response.setStatusCode(true);
        response.setStatusMessage("logout ok");
        
        return response;
    }

    private String electNode2Connect(String subscription){
        return subscription; //aqui teremos que percorrer a lista de subcritores do parametro subscription e escolher um
                   //consoante um certo raciocínio
    }

}