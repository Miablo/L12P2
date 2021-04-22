import javax.swing.*;

/**
 *
 * Main class used as driver
 *
 * @author Mio, Cody
 * @version 2.0
 */
public class Main {
    /**
     * Main Constructor
     */
    public Main(){
        GUI gui = new GUI();

        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }

    /**
     * main method creates windows
     *
     * @param args not used
     */
    public static void main(String[] args) {

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception mainEX) {
            mainEX.printStackTrace();
        }

        new Main();
    }

}
