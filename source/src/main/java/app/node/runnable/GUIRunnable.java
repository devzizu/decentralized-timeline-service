package app.node.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;
import app.exchange.ServiceConstants;
import app.exchange.res.LogoutResponse;
import app.exchange.res.SubscribeResponse;
import app.exchange.zmq.Post;
import app.node.api.CentralAPI;
import app.node.persist.NodeDatabase;
import app.util.gui.GUI;

public class GUIRunnable implements Runnable {

    private String nodeID;
    private CentralAPI centralAPI;
    private NodeNetwork nodeNetwork;
    private NodeDatabase nodeDatabase;
    private SubRunnable subRunnable;
    private ZContext context;

    private static HashSet<String> menuOptions = new HashSet<>(Arrays.asList(
        "logout", "timeline", "post", "subscribe"
    ));
    
    public GUIRunnable(ZContext context, String nodeID, CentralAPI centralAPI, NodeNetwork nodeNetwork, NodeDatabase nodeDatabase, SubRunnable subRunnable) {
        this.nodeID = nodeID;
        this.centralAPI = centralAPI;
        this.nodeNetwork = nodeNetwork;
        this.nodeDatabase = nodeDatabase;
        this.subRunnable = subRunnable;
        this.context = context;
    }

    @Override
    public void run() {

        try (ZMQ.Socket inProcPushToPub = context.createSocket(SocketType.PUSH))
        {

            inProcPushToPub.connect("inproc://"+ServiceConstants.INPROC_PUB);

            boolean continueDisplaying = true;
            Scanner sysin = new Scanner(System.in);
            
            do {

                System.out.println();

                GUI.buildMenu(new ArrayList<>(menuOptions));

                System.out.print("> ");
                String option = sysin.nextLine();

                String[] optionParts = option.split(" ");

                if (menuOptions.contains(optionParts[0])) {

                    if (option.equals("logout")) {

                        GUI.showMessageFromNode(nodeID, "contacting central for logout...");

                        CompletableFuture<MessageWrapper> futureLogoutResponse = centralAPI.central_logout();

                        MessageWrapper logoutResponse = futureLogoutResponse.get();

                        GUI.showMessageFromNode(nodeID, "warn: got response from logout:");
                        GUI.showMessageFromNode(nodeID, logoutResponse.toString());
            
                        if (logoutResponse instanceof LogoutResponse && logoutResponse.statusCode == true) {
                            
                            // login ok, start zeromq
                            continueDisplaying = false;
                            
                            GUI.showMessageFromNode(nodeID, "warn: exiting node...");
                        }

                    } else if (option.equals("timeline")) {
                        
                        //todo


                    } else if (option.startsWith("subscribe ")) {
                        
                        if (optionParts.length == 2) {

                            CompletableFuture<MessageWrapper> futureSubResponse = centralAPI.central_subscribe(optionParts[1]);
    
                            MessageWrapper subResponse = futureSubResponse.get();

                            GUI.showMessageFromNode(nodeID, "warn: got subscription answer from central:");
                            GUI.showMessageFromNode(nodeID, subResponse.toString());

                            if (subResponse instanceof SubscribeResponse && subResponse.statusCode == true) {

                                SubscribeResponse subResCast = (SubscribeResponse) subResponse;

                                this.subRunnable.subscribe(optionParts[1], subResCast.connectionForPub);
                            }

                        } else {
                            GUI.showMessageFromNode(nodeID, "you should do: subscribe <node>");
                        }

                    } else if (option.startsWith("post ")) {

                        Post newPost = new Post(String.join(" ", Arrays.copyOfRange(optionParts, 1, optionParts.length)), this.nodeDatabase.subscriptionClocks);

                        String messageToPost = this.nodeID + "#" + newPost.toJSON();

                        System.out.println("in gui:"+messageToPost);

                        inProcPushToPub.send(messageToPost);
                    }

                } else {
                    GUI.showMessageFromNode(nodeID, "error: command <" + option + "> invalid");
                }

            } while(continueDisplaying);
            
            sysin.close();

            System.exit(0);

        } catch (Exception e) {
        
        }

    }   
}