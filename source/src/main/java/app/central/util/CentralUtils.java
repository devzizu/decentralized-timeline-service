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
            UserNode user = new UserNode(username, network, true, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>());

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

            Map<String,IpPort> connecting = new HashMap<>();
            Map<String,IpPort> recoverPorts = new HashMap<>();

            for(String subscription : user.subscriptions){
                
                UserNode electedNode = electNode2Connect(subscription, user);

                if(user.dependsOn.containsKey(electedNode.username))
                    user.dependsOn.get(electedNode.username).add(subscription);
                else
                    user.dependsOn.put(electedNode.username, (new HashSet<>(Arrays.asList(subscription))));

                if (electedNode.connections.containsKey(subscription))
                    electedNode.connections.get(subscription).add(new Connection(subscription, loginRequest.nodeID));
                else
                    electedNode.connections.put(subscription, (new HashSet<>(Arrays.asList(new Connection(subscription, loginRequest.nodeID)))));

                connecting.put(subscription, new IpPort(electedNode.network.host,electedNode.network.pubPort));

                recoverPorts.put(subscription, new IpPort(electedNode.network.host,electedNode.network.replyPort)); //porta de reply para fazer o recover da subscrição 
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
            
            SubscribeResponse subRes = new SubscribeResponse(null,null);

            subRes.setStatusCode(false);
            subRes.setStatusMessage("oops something went wrong! mirs on the way!");
            
            return subRes;

        } else {

            if (userSubscriber.subscriptions.contains(subscription)) {
                
                SubscribeResponse subRes = new SubscribeResponse(null,null);

                subRes.setStatusCode(false);
                subRes.setStatusMessage("already subscribed");
            
                return subRes;
            
            } else {

                UserNode electedUser = electNode2Connect(subscription,userSubscriber);
            
                if(userSubscriber.dependsOn.containsKey(electedUser.username))
                    userSubscriber.dependsOn.get(electedUser.username).add(subscription);
                else
                    userSubscriber.dependsOn.put(electedUser.username, (new HashSet<>(Arrays.asList(userSubscription.username))));

                // if the elected node is the source subscription
                if (electedUser.username.equals(userSubscription.username)) {      
                    electedUser = userSubscription;
                }

                userSubscriber.subscriptions.add(subscription);
                userSubscription.subscribers.add(subscriber);
                
                if (electedUser.connections.containsKey(subscription))
                    electedUser.connections.get(subscription).add(new Connection(subscription,subscriber));
                else
                    electedUser.connections.put(subscription, (new HashSet<>(Arrays.asList(new Connection(subscription,subscriber)))));

                redisConnector.setNode(userSubscriber.username, userSubscriber);
                redisConnector.setNode(userSubscription.username, userSubscription);
                
                if (!electedUser.username.equals(userSubscription.username))
                    redisConnector.setNode(electedUser.username, electedUser);
                
                SubscribeResponse subRes = new SubscribeResponse(new IpPort(electedUser.network.host,electedUser.network.pubPort),new IpPort(electedUser.network.host,electedUser.network.replyPort));
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

        for (String subscription : user.connections.keySet()) {
            
            Set<Connection> conns = user.connections.get(subscription);

            UserNode originNode = redisConnector.getNode(subscription);

            List<UserNode> onlineSubscribers = getOnlineSubscribers(originNode);

            for(Connection c : conns) {
    
                UserNode dependent = redisConnector.getNode(c.dependent_node);
    
                UserNode nc = electNode2Connect(onlineSubscribers, originNode, dependent);

                if(nc!=null){

                    dependent.dependsOn.remove(username);

                    if(dependent.dependsOn.containsKey(nc.username))
                        dependent.dependsOn.get(nc.username).add(subscription);
                    else
                        dependent.dependsOn.put(nc.username, (new HashSet<>(Arrays.asList(subscription))));

                    redisConnector.setNode(nc.username, nc);

                    IpPort newPorts = new IpPort(nc.network.host,nc.network.pubPort);

                    NotifyNode.notify(dependent.network.host, dependent.network.pullPort, c.origin_node, newPorts);
        
                    if (nc.connections.containsKey(c.origin_node))
                        nc.connections.get(c.origin_node).add(new Connection(c.origin_node,c.dependent_node));
                    else
                    nc.connections.put(c.origin_node, (new HashSet<>(Arrays.asList(new Connection(c.origin_node,c.dependent_node)))));

                    redisConnector.setNode(nc.username, nc);
                }
            }
        }

        for (String userIdepend: user.dependsOn.keySet()) {

            UserNode nodeIDepend = redisConnector.getNode(userIdepend);
            
            Set<String> origins = user.dependsOn.get(nodeIDepend.username);

            for (String sub : origins) {

                nodeIDepend.connections.get(sub).remove(new Connection(sub, username));   
            }
        }
        
        LogoutResponse response = new LogoutResponse(username);
        response.setStatusCode(true);
        response.setStatusMessage("logout ok");
        
        return response;
    }


    private UserNode electNode2Connect(String subscription, UserNode subscriber){

        UserNode subscript = redisConnector.getNode(subscription);

        List<UserNode> onSubscribers = getOnlineSubscribers(subscript);

        return electNode2Connect(onSubscribers, subscript, subscriber); //aqui teremos que percorrer a lista de subcritores do parametro subscription e escolher um
                   //consoante um certo raciocínio
    }


    private UserNode electNode2Connect(List<UserNode> onSubscribers, UserNode subscription,UserNode subscriber){

        double electedPoints = -1;
        UserNode elected = null;

        if(subscription.online){
            electedPoints = points(subscription,subscriber, subscription);
            elected = subscription;
        }


        for(UserNode user : onSubscribers){
            //subscriber looking for connection cannot connect to himself
            if(!user.username.equals(subscriber.username)) {
                
                double tmp = points(user, subscriber,subscription);

                if(tmp > electedPoints){
                    electedPoints = tmp;
                    elected = user;
                }
            }
        }

        return elected;
    }


    private List<UserNode> getOnlineSubscribers(UserNode subscription){
        List<UserNode> result = new ArrayList<>();

        for(String sub : subscription.subscribers){
            UserNode subscriber = redisConnector.getNode(sub);
            if(subscriber.online) result.add(subscriber);
        }

        return result;
    }


    private double points (UserNode candidate, UserNode subscriber, UserNode subscription){


        // avgTime/distance(diference between pub ports)

        return candidate.getaverageUpTime() / (double)Math.abs(subscriber.network.pubPort-candidate.network.pubPort);
    }
}