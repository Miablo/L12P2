import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

public class OpenBtn_ActionListenerAdapter implements ActionListener {
    GUI adapter;

    OpenBtn_ActionListenerAdapter(GUI adapter){
        this.adapter = adapter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adapter.Openbtn_actionPerformed(e);
    }

}
