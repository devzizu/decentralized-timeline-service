package app.central.service;

import java.util.*;

import app.central.usernode.*;

public class CentralService {

    public Map<String, UserNode> database;

    public CentralService() {
        this.database = new HashMap<>();    
    }


    //used for registering a new user
    public boolean addNewUser(String username,Network network){
        boolean res = true;
        if(database.containsKey(username)) res=false;
        
        else{
            UserNode user = new UserNode(username, network, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            database.put(username,user);
        }
        return res;
    }


    //used for registering a new user
    public boolean loginUser(String username,Network network){
        boolean res = true;
        UserNode user = database.get(username);
        if(user==null || user.online) res=false;
        else{
            user.online = true;
            user.network = network;

            Set<IpPort> connecting = new HashSet<>();

            for(String subscription : user.subscriptions){
                UserNode electedNode = database.get(electNode2Connect(subscription));
                electedNode.connections.add(new Connection(subscription,username));
                connecting.add(new IpPort(electedNode.network.host,electedNode.network.pubPort));
            }
        }
        return res;
    }


    public IpPort subscribe(String subscriber, String subscription){

        UserNode userSubscriber = database.get(subscriber);
        UserNode userSubscription = database.get(subscription);

        if(userSubscriber==null || userSubscription==null || !(userSubscriber.online)) return null;

        else{
            if(userSubscriber.subscriptions.contains(subscription)) return null;
            
            else{
                userSubscriber.subscriptions.add(subscription);
                userSubscription.subscribers.add(subscriber);
                UserNode electedNode = database.get(electNode2Connect(subscription));
                electedNode.connections.add(new Connection(subscription,subscriber));
                return new IpPort(electedNode.network.host,electedNode.network.pubPort); //info a quem tem que se ligar para receber os posts
            }
        }
    }



    public void logout(String username){
        UserNode user = database.get(username);

        if(user==null || !user.online) return;

        user.online = false;
        
        for(Connection c : user.connections){
            //TODO temos que ter em atenção que este nodo que está a fazer logout não ser considerado
            String newConnection = electNode2Connect(c.origin_node);

            UserNode nc = database.get(newConnection);

            IpPort newPorts = new IpPort(nc.network.host,nc.network.pubPort);

            //TODO send to c.dependentNode the IpPort (using the push-pull channel)
        }

    }



    private String electNode2Connect(String Subscription){
        return ""; //aqui teremos que percorrer a lista de subcritores do parametro subscription e escolher um
                   //consoante um certo raciocínio
    }

}