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

    public int complete(MessageWrapper wrapper) {

        if (this.CFUTURE_REQUESTS.containsKey(wrapper.messageID)) {

            this.CFUTURE_REQUESTS.get(wrapper.messageID).complete(wrapper);
            this.CFUTURE_REQUESTS.remove(wrapper.messageID);
        }
        return wrapper.messageID;
    }
}
