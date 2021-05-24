
package app.node;

import java.io.IOException;

import app.central.usernode.Network;
import app.config.ConfigReader;
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
        parser.addArgument("-pub").required(true)
            .help("Network port of the ZMQ.PUB server (timeline posts).");
        parser.addArgument("-pull").required(true)
            .help("Network port of the ZMQ.PULL server (central notifications).");
        parser.addArgument("-reply").required(true)
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

    public static void main(String[] args) throws IOException {

        GUI.clearScreen(); 

        // program configuration
        ConfigReader config = new ConfigReader();
        config.read("../cnf.toml");

        // read and setup program arguments
        Namespace progArgs = setup_argparser(args);

        System.out.println("Node settings: " + progArgs.toString());
        
        // create node services

        Network nodeNetwork = new Network();
        nodeNetwork.setHost(progArgs.getString("host"));
        nodeNetwork.setPubPort(progArgs.getLong("pub"));
        nodeNetwork.setPullPort(progArgs.getLong("pull"));
        nodeNetwork.setReplyPort(progArgs.getLong("reply"));

        // process sign-in/sign-up

        if (progArgs.getBoolean("register")) {

            // register in central node

            

        } else if (progArgs.getBoolean("login")) {

            // login in central node



        } else {

            // invalid run option
            System.out.println("error: You need to specify --register or --login");
        }
    }
}