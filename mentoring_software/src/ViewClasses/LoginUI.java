package ViewClasses;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LoginUI extends UI {
    private JPanel rootPanel;
    private JButton button;
    private JLabel title;
    private JTextField usernameField;
    private JLabel hintUsername;
    private JLabel hintPassword;
    private JTextField passwordField;

    public LoginUI() {
        // set ViewClasses.UI's rootPanel as rootPanel
        setRootPanel(rootPanel);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText().trim();
    }

    public void addLoginListener(ActionListener listener) {
        button.addActionListener(listener);
    }
}
