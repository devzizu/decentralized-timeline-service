
package app.util.gui;

import java.util.List;

public class GUI {
    
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    public static void buildMenu(List<String> options) {

        System.out.println("Node menu:\n");

        int menuItemID = 0;
        for (String opt: options) {
            System.out.println(menuItemID + ": " + opt);
        }
    }
}
