import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;

import javax.swing.*;
import java.io.*;
import java.util.Map;

/**
 *
 * Main class used as driver
 *
 * @author Mio, Cody
 * @version 2.0
 */
public class Main {
    /**
     * main method creates windows
     *
     * @param args not used
     */
    public static void main(String[] args) {
        GUI gui = new GUI();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // show window
                gui.setVisible(true);
            }
        });
    }

}
