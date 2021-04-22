import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI_JListSelecterAdapter implements ListSelectionListener {
    GUI adapter;

    GUI_JListSelecterAdapter(GUI adapter) {
        this.adapter = adapter;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.adapter.classListValueChange(e);
    }
}
