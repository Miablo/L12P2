import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RunBtn_ActionListenerAdapter implements ActionListener {

    GUI adapter;

    RunBtn_ActionListenerAdapter(GUI adapter){
        this.adapter = adapter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adapter.runActionPerformed(e);
    }
}
