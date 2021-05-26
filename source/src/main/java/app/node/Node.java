

package app.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import app.central.Central;
import app.central.usernode.NodeNetwork;
import app.exchange.MessageWrapper;
import app.exchange.res.LoginResponse;
import app.exchange.res.RegisterResponse;
import app.node.api.CentralAPI;
import app.node.persist.NodeDatabase;
import app.node.services.NodeService;
import app.util.config.ConfigReader;
import app.util.gui.GUI;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Node {

    private static Namespace setup_argparser(String[] args) {

        ArgumentParser parser = ArgumentParsers.newFor("NodeServer").build()
            .defaultHelp(true)
            .description("Setup a node server for the P2P Network.");
        parser.addArgument("-node").required(true)
            .help("This node ID.");
        parser.addArgument("--login").required(false).action(Arguments.storeTrue())
            .help("Log-In to a Central node (specify -node).");
        parser.addArgument("--register").required(false).action(Arguments.storeTrue())
            .help("Register to a Central node (specify -node).");
        parser.addArgument("-host").required(false)
            .setDefault("localhost")
            .help("Hostname for the listenning servers.");
        parser.addArgument("-pub").required(true).type(Long.class)
            .help("Network port of the ZMQ.PUB server (timeline posts).");
        parser.addArgument("-pull").required(true).type(Long.class)
            .help("Network port of the ZMQ.PULL server (central notifications).");
        parser.addArgument("-reply").required(true).type(Long.class)
            .help("Network port of the Atomix server (timeline recovery).");

        Namespace ns = null;

        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }


        if (ns == null) System.exit(1);

        return ns;
    }

    public static void main(String[] args) throws Exception {

        GUI.clearScreen(); 

        // program configuration
        ConfigReader config = new ConfigReader();
        config.read("../cnf.toml");

        // read and setup program arguments
        Namespace progArgs = setup_argparser(args);

        System.out.println("Node settings: " + progArgs.toString());
        
        // create node services

        NodeNetwork nodeNetwork = new NodeNetwork();
        nodeNetwork.setHost(progArgs.getString("host"));
        nodeNetwork.setPubPort(progArgs.getLong("pub"));
        nodeNetwork.setPullPort(progArgs.getLong("pull"));
        nodeNetwork.setReplyPort(progArgs.getLong("reply"));

        String nodeID = progArgs.getString("node");

        NodeDatabase nodeDatabase = new NodeDatabase();
        nodeDatabase.setNodeID(nodeID);

        // create and start services

        NodeService nodeService = new NodeService(config, nodeID, nodeNetwork);
        nodeService.start();

        CentralAPI centralAPI = new CentralAPI(config, nodeService, nodeDatabase, nodeNetwork);

        // process sign-in/sign-up

        boolean processedSignUp = false;

        if (progArgs.getBoolean("register")) {

            // register in central node

            GUI.showMessageFromNode(nodeID, "warn: register request sent");
            
            CompletableFuture<MessageWrapper> futureRegisterResponse = centralAPI.central_register();

            MessageWrapper registerResponse = futureRegisterResponse.get();

            GUI.showMessageFromNode(nodeID, "warn: got response from register:");
            GUI.showMessageFromNode(nodeID, registerResponse.toString());

            if (registerResponse instanceof RegisterResponse && registerResponse.statusCode == true) {

                // register ok, start zeromq
                processedSignUp = true;

            } else if (!registerResponse.statusCode) {

                GUI.showMessageFromNode(nodeID, "info: exiting...");
                System.exit(0);
            }

        } else if (progArgs.getBoolean("login")) {

            // login in central node

            GUI.showMessageFromNode(nodeID, "warn: login request sent");

            CompletableFuture<MessageWrapper> futureLoginResponse = centralAPI.central_login();

            MessageWrapper loginResponse = futureLoginResponse.get();

            GUI.showMessageFromNode(nodeID, "warn: got response from login:");
            GUI.showMessageFromNode(nodeID, loginResponse.toString());

            if (loginResponse instanceof LoginResponse && loginResponse.statusCode == true) {
                
                // login ok, start zeromq
                processedSignUp = true;
                
            } else if (!loginResponse.statusCode) {

                GUI.showMessageFromNode(nodeID, "info: exiting...");
                System.exit(0);
            }

        } else {

            // invalid run option
            GUI.showMessageFromNode(nodeID, "error: please provide valid arguments");
        }

        if (processedSignUp) {

            GUI.showMessageFromNode(nodeID, "info: sign up processed, node is listenning for user input...");
            
            new Thread(new GUIRunnable(nodeID, centralAPI)).start();
        }

    }

    private static class GUIRunnable implements Runnable {

        private String nodeID;
        private CentralAPI centralAPI;

        private static HashSet<String> menuOptions = new HashSet<>(Arrays.asList("logout", "timeline", "subscribe <nodeID>"));
        
        public GUIRunnable(String nodeID, CentralAPI centralAPI) {
            this.nodeID = nodeID;
            this.centralAPI = centralAPI;
        }

        @Override
        public void run() {

            boolean continueDisplaying = true;
            Scanner sysin = new Scanner(System.in);
            
            do {

                System.out.println();

                GUI.buildMenu(new ArrayList<>(menuOptions));

                System.out.print("> ");
                String option = sysin.nextLine();

                if (menuOptions.contains(option)) {

                    if (option.equals("logout")) {

                        GUI.showMessageFromNode(nodeID, "contacting central for logout...");

                        continueDisplaying = false;

                    } else if (option.equals("timeline")) {
                        //todo
                    } else if (option.startsWith("subscribe ")) {
                        //todo
                    }

                } else {
                    GUI.showMessageFromNode(nodeID, "error: command <" + option + "> invalid");
                }

            } while(continueDisplaying);
            
            sysin.close();
        }
    }
}