
package app.central;

import app.central.util.*;
import app.config.ConfigReader;
import app.util.gui.GUI;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import app.central.service.CentralService;
import app.central.store.RedisUtils;
import app.central.usernode.CentralNetwork;
import net.sourceforge.argparse4j.inf.Namespace;

public class Central {

    private static Namespace setup_argparser(String[] args) {

        ArgumentParser parser = ArgumentParsers.newFor("NodeServer").build()
            .defaultHelp(true)
            .description("Setup a central server for the P2P Network.");
        parser.addArgument("-central").required(true)
            .help("This central ID.");
        parser.addArgument("-host").required(false)
            .setDefault("localhost")
            .help("Hostname for the listenning servers.");
        parser.addArgument("-reply").required(true)
            .help("Network port of the Atomix server (node requests).");

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
    
    public static void main(String[] args) {

        GUI.clearScreen(); 

        // program configuration
        ConfigReader config = new ConfigReader();
        config.read("../cnf.toml");
        Namespace progArgs = setup_argparser(args);
        System.out.println("Central settings: " + progArgs.toString());

        // central identification
        CentralNetwork centralNetwork = new CentralNetwork();
        centralNetwork.setHost(progArgs.getString("host"));
        centralNetwork.setReplyPort(progArgs.getLong("reply"));

        String centralID = progArgs.getString("central");

        // central service and utils configuration
        CentralUtils centralUtils = new CentralUtils(new RedisUtils());
        
        CentralService centralService = new CentralService(config, centralID, centralNetwork, centralUtils);
        
        centralService.start();
    }
}
