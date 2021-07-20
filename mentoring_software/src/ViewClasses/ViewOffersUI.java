package ViewClasses;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Display the list of offers made to a bid
 */
public class ViewOffersUI extends UI {
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JButton back;
    private JPanel noOffersMessage;

    public ViewOffersUI() {
        // setting the ViewClasses.UI's rootPanel
        setRootPanel(rootPanel);
    }

    public void addBackListener (ActionListener actionListener) {
        back.addActionListener(actionListener);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JPanel getNoOffersMessage() {
        return noOffersMessage;
    }
}
