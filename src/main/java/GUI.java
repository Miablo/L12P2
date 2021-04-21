import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Connector.Argument;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * GUI contains a split pane
 * left hand side of the split pane displays all of the found classes
 * right hand side shows the declared methods and constructors of a user selected class in the list
 *
 * @author Mio, Cody
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
    JFileChooser jfchooser = new JFileChooser("../");

    JPanel window; // main window
    BorderLayout borderLayout1 = new BorderLayout();

    ClassNameFilter filter = new ClassNameFilter();

    JToolBar header = new JToolBar(); // top toolbar showing current open class

    JScrollBar rightBar = new JScrollBar();
    // Object Button
    JLabel construct = new JLabel();
    // Method run button
    JLabel mtdLabel = new JLabel();

    JButton openBtn = new JButton();
    JButton closeBtn = new JButton();
    JButton runBtn = new JButton();

    JTextArea runCounter = new JTextArea();
    JList classList = new JList();

    JMenuBar winMenu = new JMenuBar();
    JMenu fileMenu = new JMenu();
    JMenuItem fileExit = new JMenuItem();
    JMenu helpMenu = new JMenu();
    JMenuItem helpAbout = new JMenuItem();

    JScrollPane classPanel = new JScrollPane();
    JScrollPane constMethPanel = new JScrollPane();
    JSplitPane mainPane = new JSplitPane();

    JTextArea rightTextArea = new JTextArea();

    MyThread mt;
    URLClassLoader classLoader;
    URL[] urls = new URL[1];
    VirtualMachine vm = null;

    File dir = null;
    File[] fileArray;

    /**
     * Create GUI window and all components
     */
    public void createWindow() throws Exception {
        // Begin main window //
        this.window = (JPanel)this.getContentPane();
        this.window.setLayout(this.borderLayout1);
        this.setSize(new Dimension(691, 602));
        this.setTitle("Lab 11 Q3 Testing Tool GUI ");
        // Begin menubar //
        this.fileMenu.setText("File");
        this.fileExit.setText("Exit");
        this.fileExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        this.helpMenu.setText("Help");
        this.helpAbout.setText("About");
        this.openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Openbtn_actionPerformed(e);
            }
        });

        this.openBtn.setText("Open File");
        this.closeBtn.setText("Close File");

        this.classPanel.setPreferredSize(new Dimension(158, 130));
        this.mainPane.setOneTouchExpandable(true);

        this.classList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int idx = classList.getSelectedIndex();
                String[] skelStringArr = new String[0];
                try {
                    getName(idx);
                    rightTextArea.setText(skelStringArr[0]);
                    runCounter.setText(skelStringArr[1] + "\n    ");
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
            }
        });

        this.runBtn.setText("Run");
        this.runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LaunchingConnector lc = Bootstrap.virtualMachineManager().defaultConnector();
                Map map = lc.defaultArguments();
                Argument ca = (Argument)map.get("main");
                int idx = classList.getSelectedIndex();

                try {
                    int i = fileArray[idx].getName().indexOf(".class");
                    String cName = dir.getName() + "." + fileArray[idx].getName().substring(0, i);
                    ca.setValue("-cp \"" + dir.getParentFile() + "\" " + cName);
                    vm = lc.launch(map);
                    Process process = vm.process();
                    vm.setDebugTraceMode(0);
                    displayRemoteOutput(process.getInputStream());
                    mt = new MyThread(vm, false, dir.getName(), fileArray.length, this);
                } catch (Exception var9) {
                    System.out.println(e);
                }
            }
        });

        this.runCounter.setText("     ");
        this.runCounter.setBackground(Color.GRAY);

        this.header.add(this.openBtn);
        this.header.add(this.closeBtn);
        this.header.add(this.runBtn, (Object)null);

        this.window.add(this.mainPane, "Center");

        this.mainPane.add(this.classPanel, "left");
        this.mainPane.add(this.constMethPanel, "right");
        this.constMethPanel.getViewport().add(this.rightTextArea);
        this.constMethPanel.setRowHeaderView(this.runCounter);
        this.classPanel.getViewport().add(this.classList, (Object)null);

        this.fileMenu.add(this.fileExit);
        this.helpMenu.add(this.helpAbout);
        this.winMenu.add(this.fileMenu);
        this.winMenu.add(this.helpMenu);
        this.setJMenuBar(this.winMenu);

        this.window.add(this.header, "North");
        // left window view //

        // Method Button and label
        this.mtdLabel.setRequestFocusEnabled(true);
        this.mtdLabel.setText("Methods & Constructors");
        this.construct.setText("Found Classes");

        rightBar.setMaximum(513);
        rightBar.setVisible(true);
        // add views to window //

        this.window.add(this.header, "North");
        // close window action
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

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
                    GUI.this.dumpStream(stream);
                } catch (IOException var2) {
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

    private void getName(int idx) throws ClassNotFoundException {
        int i = this.fileArray[idx].getName().indexOf(".class");
        String cName = this.fileArray[idx].getName().substring(0, i);
        Class c = Class.forName(this.dir.getName() + "." + cName, true, this.classLoader);
        ClassSkeleton cs = new ClassSkeleton(c);
        String[] skelStringArr;
        if (this.mt != null) {
            Hashtable ht = this.mt.getHashTable();
            skelStringArr = cs.getSkeleton(ht);
        } else {
            skelStringArr = cs.getSkeleton();
        }
    }

    public void Openbtn_actionPerformed(ActionEvent e) {

        this.jfchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = this.jfchooser.showOpenDialog(this);
        if (returnVal == 0) {
            this.dir = this.jfchooser.getSelectedFile();
            this.fileArray = this.dir.listFiles(this.filter);
            DefaultListModel dlm = new DefaultListModel();

            for(int i = 0; i < (this.fileArray != null ? this.fileArray.length : 0); ++i) {
                dlm.add(i, this.fileArray[i].getName());
            }

            this.classList.setModel(dlm);

            try {
                URL u = this.dir.getParentFile().toURL();
                this.urls[0] = u;
                this.classLoader = new URLClassLoader(this.urls);
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
