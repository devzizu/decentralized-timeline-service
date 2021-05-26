
package app.util.gui;

import java.util.List;

public class GUI {
    
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    public static void buildMenu(List<String> options) {

        System.out.println("-------------------------------------------------");
        System.out.println("Node menu (write a command):\n");

        int menuItemID = 0;
        for (String opt: options) {
            System.out.println(menuItemID++ + ": " + opt);
        }
        System.out.println();
    }

    public static void showMessageFromNode(String nodeID, String message) {
        System.out.println("(node:"+nodeID+") " + message + "...");
    }
}
