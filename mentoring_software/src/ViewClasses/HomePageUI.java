package ViewClasses;

import javax.swing.*;

public class HomePageUI extends UI {
    private JScrollPane scrollPane;
    private JPanel rootPanel;
    private JLabel title;

    public HomePageUI() {
        // set ViewClasses.UI's rootPanel as rootPanel
        setRootPanel(rootPanel);
    }

    public JScrollPane getScrollPane() { return scrollPane; }

    public JLabel getTitle() { return title;}
}
