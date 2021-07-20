package ViewClasses;

import javax.swing.*;

/**
 * Allows user to look the contracts signed
 */
public class ViewContractUI extends UI {
    private JPanel rootPanel;
    private JButton backButton;
    private JLabel title;
    private JScrollPane scrollPane;
    private JPanel noContractMessage;

    public ViewContractUI() {
        // set this rootPanel as the ViewClasses.UI's rootPanel
        setRootPanel(rootPanel);
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JPanel getNoContractMessage() {
        return noContractMessage;
    }

    public JLabel getTitle() {
        return title;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
