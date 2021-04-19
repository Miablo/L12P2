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
        // init Window
        gui.createWindow();
        // show window
        gui.setVisible(true);
    }
}
