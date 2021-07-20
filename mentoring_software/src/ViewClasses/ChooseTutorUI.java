package ViewClasses;

import javax.swing.*;

public class ChooseTutorUI extends UI {
    private JPanel rootPanel;
    private JButton cancel;
    private JButton confirm;
    private JTextField tutorUsername;

    public ChooseTutorUI() {
        setRootPanel(rootPanel);
    }

    public JButton getCancelButton() { return cancel; }

    public JButton getConfirmButton() { return confirm; }

    public String getTutorUsername() { return tutorUsername.getText().trim();}
}
