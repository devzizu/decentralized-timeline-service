package app.central;

import java.util.*;t

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

            //adding the dependency to the node witch we will connect the user that logged in to receive messages of the subcription
            user.subscriptions.forEach(subscription -> database.get(electNode2Connect(subscription)).connections.add(new Connection(subscription,username)));

            //aqui falta fazer retrieve aos ips e depois contruir a resposta (talvez criar a espécie de um DTO...)

        }

        
        return res;
    }

    public String electNode2Connect(String Subscription){
        return ""; // aqui teremos que percorrer a lista de subcritores do parametro subscription e escolher um
                   //consoante um certo raciocínio
    }
    

}
