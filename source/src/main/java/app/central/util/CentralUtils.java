package app.central.util;

import java.util.*;

import app.central.store.RedisUtils;
import app.central.usernode.*;
import app.exchange.req.LoginRequest;
import app.exchange.res.LoginResponse;

public class CentralUtils {

    private RedisUtils redisConnector;

    public CentralUtils(RedisUtils redisConnector) {
        
        this.redisConnector = redisConnector;
    }

    //used for registering a new user
    public boolean addNewUser(String username, NodeNetwork network){
        boolean res = true;
        if (redisConnector.hasNode(username)) {
            res=false;
        } else {
            UserNode user = new UserNode(username, network, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            redisConnector.setNode(username,user);
        }
        return res;
    }

    //used for registering a new user
    public LoginResponse login_node(LoginRequest loginRequest) {
        
        UserNode user = redisConnector.getNode(loginRequest.nodeID);
        
        if(user==null || user.online) {
            return null;
        } else {
            user.online = true;
            user.network = loginRequest.network;

            Set<IpPort> connecting = new HashSet<>();
            Map<String,IpPort> recoverPorts = new HashMap<>();

            for(String subscription : user.subscriptions){
                UserNode electedNode = redisConnector.getNode(electNode2Connect(subscription));
                electedNode.connections.add(new Connection(subscription, loginRequest.nodeID));
                connecting.add(new IpPort(electedNode.network.host,electedNode.network.pubPort));

                recoverPorts.put(subscription,new IpPort(electedNode.network.host,electedNode.network.replyPort)); //porta de reply para fazer o recover da subscrição
            }

            return new LoginResponse(connecting, recoverPorts);
        }
    }


    public IpPort subscribe_node(String subscriber, String subscription){

        UserNode userSubscriber = redisConnector.getNode(subscriber);
        UserNode userSubscription = redisConnector.getNode(subscription);

        if(userSubscriber==null || userSubscription==null || !(userSubscriber.online)) return null;

        else{
            if(userSubscriber.subscriptions.contains(subscription)) return null;
            
            else{
                userSubscriber.subscriptions.add(subscription);
                userSubscription.subscribers.add(subscriber);
                UserNode electedNode = redisConnector.getNode(electNode2Connect(subscription));
                electedNode.connections.add(new Connection(subscription,subscriber));
                return new IpPort(electedNode.network.host,electedNode.network.pubPort); //info a quem tem que se ligar para receber os posts
            }
        }
    }

    public void logout_node(String username){
        UserNode user = redisConnector.getNode(username);

        if(user==null || !user.online) return;

        user.online = false;
        
        for(Connection c : user.connections){
            //TODO temos que ter em atenção que este nodo que está a fazer logout não ser considerado
            String newConnection = electNode2Connect(c.origin_node);

            UserNode nc = redisConnector.getNode(newConnection);

            IpPort newPorts = new IpPort(nc.network.host,nc.network.pubPort);

            //TODO send to c.dependentNode the IpPort (using the push-pull channel)
        }

    }



    private String electNode2Connect(String Subscription){
        return ""; //aqui teremos que percorrer a lista de subcritores do parametro subscription e escolher um
                   //consoante um certo raciocínio
    }

}