package app.central;

import java.util.*;

import app.central.usernode.*;

public class Central {

    public Map<String, UserNode> database;

    public Central() {
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
                connecting.add(new IpPort(electedNode.network.pubHost,electedNode.network.pubPort));
            }
        }

        
        return res;
    }



    public String electNode2Connect(String Subscription){
        return ""; //aqui teremos que percorrer a lista de subcritores do parametro subscription e escolher um
                   //consoante um certo raciocínio
    }
    

}
