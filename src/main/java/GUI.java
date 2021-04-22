import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.LaunchingConnector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 *
 * GUI contains a split pane
 * left hand side of the split pane displays all of the found classes
 * right hand side shows the declared methods and constructors of a user selected class in the list
 *
 * @author Mio
 * @version 2.0
 *
 * @see java.awt.event.ActionListener
 * @see java.awt.event.ActionEvent
 * @see java.awt
 * @see javax.swing
 * @see com.sun.jdi.Bootstrap
 * @see com.sun.jdi.VirtualMachine
 * @see com.sun.jdi.connect.LaunchingConnector
 * @see com.sun.jdi.connect.Connector.Argument
 * @see java.awt.event
 * @see java.io
 * @see java.net.URL
 * @see java.net.URLClassLoader
 * @see java.util.Hashtable
 * @see java.util.Map
 * @see javax.swing.event.ListSelectionEvent
 * @see javax.swing.event.ListSelectionListener
 *
 */
public class GUI extends JFrame implements ActionListener {
    BorderLayout borderLayout1 = new BorderLayout();
    JFileChooser jfchooser = new JFileChooser(".../");
    JPanel window; // main window

    File dir;
    File[] fileArray;

    ClassNameFilter filter = new ClassNameFilter();
    // Descriptive header labels and bar
    JToolBar header = new JToolBar();
    JLabel construct = new JLabel();
    JLabel mtdLabel = new JLabel();
    // toolbar buttons
    JButton openBtn = new JButton();
    JButton closeBtn = new JButton();
    JButton runBtn = new JButton();
    // grey viewport run counter area
    JTextArea runCounter = new JTextArea();
    // class list display view
    JList classList = new JList();
    // top menu bar with actions
    JMenuBar winMenu = new JMenuBar();
    JMenu fileMenu = new JMenu();
    JMenuItem fileExit = new JMenuItem();
    JMenu helpMenu = new JMenu();
    JMenuItem helpAbout = new JMenuItem();
    // views
    JScrollPane classScrollPane = new JScrollPane();
    JScrollPane csSkltnScrollPane = new JScrollPane();
    JSplitPane mainSplitPane = new JSplitPane();
    // display constructors and methods for selected class
    JTextArea rightTextArea = new JTextArea();
    JTextArea jText2 = new JTextArea();

    MyThread mt;
    URLClassLoader classLoader;
    URL[] urls = new URL[1];
    VirtualMachine vm = null;

    /**
     * Default GUI Constructor
     */
    public GUI() {
        this.enableEvents(64L);
        try {
            this.createWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayRemoteOutput(final InputStream stream) {
        Thread thr = new Thread("output reader") {
            public void run() {
                try {
                    dumpStream(stream);
                } catch (IOException ex) {
                    System.out.println("Failed reading output");
                }

            }
        };
        thr.setPriority(9);
        thr.start();
    }

    private void dumpStream(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));

        int i;
        try {
            while((i = in.read()) != -1) {
                System.out.print((char)i);
            }
        } catch (IOException ex) {
            String s = ex.getMessage();
            if (!s.startsWith("Bad file number")) {
                throw ex;
            }
        }

    }

    /**
     * Open button click opens dialog and performs following functions
     *
     * @param e action event
     */
    public void Openbtn_actionPerformed(ActionEvent e) {
        this.jfchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = this.jfchooser.showOpenDialog(this);
        //Handle open button action.

        if (returnVal == 0) {
            this.dir = this.jfchooser.getSelectedFile();
            this.fileArray = this.dir.listFiles(this.filter);

            DefaultListModel model = new DefaultListModel();

            // System.out.print("Opening: " + dir.getName() + ".\n"); -- for testing

            for(int i = 0; i < Objects.requireNonNull(this.fileArray).length; ++i) {
                model.add(i, this.fileArray[i].getName());
                // System.out.print("Added: " + fileArray[i].getName() + "\n"); -- for testing
            }

            this.classList.setModel(model);

            try {

                URL u = this.dir.getParentFile().toURI().toURL();
                this.urls[0] = u;
                this.classLoader = new URLClassLoader(this.urls);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Update numbers for run
     *
     * @throws ClassNotFoundException if class not found
     */
    public void addNumbers() throws ClassNotFoundException {
        int idx = this.classList.getSelectedIndex();

        try {
            int i = this.fileArray[idx].getName().indexOf(".class");

            String cName = this.fileArray[idx].getName().substring(0, i);
            Class c = Class.forName(this.dir.getName() + "." + cName, true, this.classLoader);

            ShowClass cs = new ShowClass(c);
            String[] strArr = new String[0];

            if (this.mt != null) {
                Hashtable hash = this.mt.getHashTable();
                strArr = cs.getSkeleton(hash);
            } else {
                strArr= cs.getSkeleton();
            }

            this.runCounter.setText(strArr[1] + "\n    ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Check class list to see if new option is selected
     *
     * @param e list action event
     */
    void classListValueChange(ListSelectionEvent e){
        int idx = this.classList.getSelectedIndex();

        try {
            int i = this.fileArray[idx].getName().indexOf(".class");
            String cName = this.fileArray[idx].getName().substring(0, i);
            Class c = Class.forName(this.dir.getName() + "." + cName,
                    true, this.classLoader);

            ShowClass showClass = new ShowClass(c);
            String[] strArr;

            if (this.mt != null) {
                Hashtable ht = this.mt.getHashTable();
                strArr = showClass.getSkeleton(ht);
            } else {
                strArr = showClass.getSkeleton();
            }

            this.jText2.setText(strArr[0]);
            this.runCounter.setText(strArr[1] + "\n    ");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * if run is clicked run method or constructor
     *
     * @param e action event
     */
    public void runActionPerformed(ActionEvent e){
        LaunchingConnector lc = Bootstrap.virtualMachineManager().defaultConnector();
        Map map = lc.defaultArguments();
        Argument ca = (Argument)map.get("main");

        int idx = this.classList.getSelectedIndex();

        try {

            int i = this.fileArray[idx].getName().indexOf(".class");

            String cName = this.dir.getName() + "." + this.fileArray[idx].getName().substring(0, i);

            ca.setValue("-cp \"" + this.dir.getParentFile().toString() + "\" " + cName);

            this.vm = lc.launch(map);

            Process process = vm.process();

            this.vm.setDebugTraceMode(0);
            this.displayRemoteOutput(process.getInputStream());

            this.mt = new MyThread(this.vm, false, this.dir.getName(),
                    this.fileArray.length, this);

        } catch (Exception ex) {
            System.out.println(e);
        }
    }


    /**
     * Create GUI window and all components
     */
    private void createWindow() throws Exception {
        // Begin main window //
        this.window = (JPanel)this.getContentPane();
        this.window.setLayout(this.borderLayout1);
        this.setSize(new Dimension(691, 602));
        this.setTitle("Lab 11 Q3 Testing Tool GUI ");
        this.classScrollPane.setPreferredSize(new Dimension(158, 130));

        // window titles
        this.mtdLabel.setRequestFocusEnabled(true);
        this.mtdLabel.setText("Methods & Constructors");
        this.construct.setText("Found Classes");

        // Begin menubar //
        this.fileMenu.setText("File");
        this.fileExit.setText("Exit");
        // add action listeners menu
        this.fileExit.addActionListener(e -> System.exit(0));
        this.helpMenu.setText("Help");
        this.helpAbout.setText("About");

        // begin second header - toolbar
        this.openBtn.setText("Open File");
        this.openBtn.addActionListener(new OpenBtn_ActionListenerAdapter(this));

        this.closeBtn.setText("Close File");

        this.runBtn.setText("Run");
        this.runBtn.addActionListener(new RunBtn_ActionListenerAdapter(this));

        // Class list view setup
        this.classList.addListSelectionListener(new GUI_JListSelecterAdapter(this));

        // view port run counter area
        this.runCounter.setText("     ");
        this.runCounter.setBackground(Color.GRAY);

        // top header buttons
        this.header.add(this.openBtn);
        this.header.add(this.closeBtn);
        this.header.add(this.runBtn, (Object)null);
        // add components to window
        this.window.add(this.mainSplitPane, "Center");
        this.mainSplitPane.add(this.classScrollPane, "left");
        this.mainSplitPane.add(this.csSkltnScrollPane, "right");
        // Class print view
        this.csSkltnScrollPane.getViewport().add(this.jText2);
        this.csSkltnScrollPane.setRowHeaderView(this.runCounter);
        this.classScrollPane.getViewport().add(this.classList, (Object)null);

        // add menu options to menubar
        this.fileMenu.add(this.fileExit);
        this.helpMenu.add(this.helpAbout);
        this.winMenu.add(this.fileMenu);
        this.winMenu.add(this.helpMenu);

        this.setJMenuBar(this.winMenu);
        this.window.add(this.header, "North");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
