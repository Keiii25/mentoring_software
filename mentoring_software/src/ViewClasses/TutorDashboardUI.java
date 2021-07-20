package ViewClasses;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TutorDashboardUI extends UI {
    private JPanel rootPanel;
    private JLabel title;
    private JButton back;
    private JLabel label;
    private JScrollPane scrollPane;


    public TutorDashboardUI(){
        super.setRootPanel(rootPanel);
    }

    public JLabel getLabel() {
        return label;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void addBackListener(ActionListener actionListener) {
        back.addActionListener(actionListener);
    }
}
