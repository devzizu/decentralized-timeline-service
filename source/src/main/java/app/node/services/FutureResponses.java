package app.node.services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import app.exchange.MessageWrapper;

public class FutureResponses {
    
    private ConcurrentHashMap<Integer, CompletableFuture<MessageWrapper>> futureRequests;
    private int requestId;

    public FutureResponses() {

        this.futureRequests = new ConcurrentHashMap<>();
        this.requestId = 0;
    }

    public int getId() {
        return this.requestId;
    }

    public void addPending(CompletableFuture<MessageWrapper> request) {

        this.futureRequests.put(this.requestId++, request);
    }

    public int complete(MessageWrapper wrapper) {

        if (this.futureRequests.containsKey(wrapper.messageID)) {

            this.futureRequests.get(wrapper.messageID).complete(wrapper);
            this.futureRequests.remove(wrapper.messageID);
        }
        return wrapper.messageID;
    }
}
