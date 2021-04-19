import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
 *
 */
public class GUI extends JFrame implements ActionListener {
    JPanel window; // main window
    // scroll view areas for method,construct,
    JSplitPane methodPane = new JSplitPane();
    JScrollPane methodView = new JScrollPane();
    JScrollPane constructView = new JScrollPane();
    // panels
    JPanel leftPanel = new JPanel();
    JPanel constructToolbar = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel methodToolbar = new JPanel();
    JToolBar header = new JToolBar(); // top toolbar showing current open class
    // layouts
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();
    // list views
    JList constructList = new JList();
    JList methodList = new JList();
    JList runCount = new JList();

    JScrollBar rightBar = new JScrollBar();
    // Object Button
    JLabel construct = new JLabel();
    // Method run button
    JLabel mtdLabel = new JLabel();

    JLabel classLabel = new JLabel();
    JViewport view = new JViewport();

    /**
     * Create GUI window and all components
     */
    public void createWindow() {
        // Begin main window //
        this.window = (JPanel)this.getContentPane();
        this.window.setLayout(this.borderLayout1);
        this.setSize(new Dimension(508, 513));
        this.setTitle("Lab 11 Q3 Testing Tool GUI ");
        // Begin select class tool bar //
        this.header.add(this.classLabel, (Object)null);
        this.classLabel.setText(" File  Help  ");

        this.view.setViewSize(new Dimension(300, 513));
        // begin right method and constructor view //
        this.methodView.setViewport(view);
        this.methodView.setPreferredSize(new Dimension(258, 150));
        this.methodView.getViewport().add(this.methodList, (Object)null);
        // header
        this.methodView.setRowHeaderView(new JLabel("0"));
        this.methodView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.runCount.setLayoutOrientation(JList.VERTICAL);

        // left window view //
        this.leftPanel.setLayout(this.borderLayout3);
        this.leftPanel.setMinimumSize(new Dimension(220, 163));
        this.leftPanel.setPreferredSize(new Dimension(228, 163));
        this.leftPanel.add(this.constructView, "Center");
        this.leftPanel.add(this.constructToolbar, "North");

        this.constructToolbar.add(this.construct, (Object)null);
        this.constructView.getViewport().add(this.constructList, (Object)null);
        // Right view //
        this.rightPanel.setLayout(this.borderLayout2);
        this.rightPanel.add(this.methodView, "Center");
        this.rightPanel.add(this.methodToolbar, "North");
        // Method Button and label
        this.mtdLabel.setRequestFocusEnabled(true);
        this.mtdLabel.setText("Methods & Constructors");
        this.construct.setText("Found Classes");

        this.methodToolbar.add(this.mtdLabel, (Object)null);
        this.methodPane.add(this.leftPanel, "left");
        this.methodPane.add(this.rightPanel, "right");
        // scroll bar //
        methodView.setVerticalScrollBar(rightBar);
        rightBar.setMaximum(513);
        rightBar.setVisible(true);
        // add views to window //
        this.window.add(this.methodPane, "Center");
        this.window.add(this.header, "North");
        // close window action
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
