
package app.node;

import java.io.IOException;

import app.config.ConfigReader;
import app.util.GUI;
import net.sourceforge.argparse4j.ArgumentParsers;
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
        parser.addArgument("-host").required(false)
            .setDefault("localhost")
            .help("Hostname for the listenning servers.");
        parser.addArgument("-pub").required(true)
            .help("Network port of the ZMQ.PUB server (timeline posts).");
        parser.addArgument("-pull").required(true)
            .help("Network port of the ZMQ.PULL server (central notifications).");
        parser.addArgument("-reply").required(true)
            .help("Network port of the ZMQ.REP server (timeline recovery).");

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

        // read and setup program arguments
        Namespace progArgs = setup_argparser(args);

        System.out.println("Node settings: " + progArgs.toString());
        
        
    }
}