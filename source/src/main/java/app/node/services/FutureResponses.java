package app.node.services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import app.exchange.MessageWrapper;

public class FutureResponses {
    
    private ConcurrentHashMap<Integer, CompletableFuture<MessageWrapper>> CFUTURE_REQUESTS;
    private int requestId;

    public FutureResponses() {

        this.CFUTURE_REQUESTS = new ConcurrentHashMap<>();
        this.requestId = 0;
    }

    public int getId() {
        return this.requestId;
    }

    public void addPendingRequest(CompletableFuture<MessageWrapper> request) {

        this.CFUTURE_REQUESTS.put(this.requestId++, request);
    }
/*
    public int complete(MessageWrapper res) {

        
        ClientResponse cli_r = null;
        try {
            cli_r = (ClientResponse) res.unwrapMessage();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (this.CFUTURE_REQUESTS.containsKey(cli_r.getMESSAGE_ID())) {

            this.CFUTURE_REQUESTS.get(cli_r.getMESSAGE_ID()).complete(res);
            this.CFUTURE_REQUESTS.remove(cli_r.getMESSAGE_ID());
        }
        return cli_r.getMESSAGE_ID();
    }*/
}
