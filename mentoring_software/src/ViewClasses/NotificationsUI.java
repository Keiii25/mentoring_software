package ViewClasses;

import javax.swing.*;

public class NotificationsUI extends UI {
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JButton OKButton;

    public NotificationsUI() {
        setRootPanel(rootPanel);
    }

    public JScrollPane getScrollPane() { return scrollPane; }

    public JButton getOKButton() { return OKButton; }
}
