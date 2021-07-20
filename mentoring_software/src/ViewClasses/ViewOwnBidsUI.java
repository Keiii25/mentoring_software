package ViewClasses;

import javax.swing.*;


/**
 * Allows requester to look at the bids they have posted
 */

public class ViewOwnBidsUI extends UI {
    private JPanel rootPanel;
    private JButton back;
    private JScrollPane scrollPane;
    private JPanel noBidMessage;

    public ViewOwnBidsUI() {
        // set this rootPanel as the ViewClasses.UI's rootPanel
        setRootPanel(rootPanel);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JButton getBackButton() {
        return back;
    }

    public JPanel getNoBidMessage() {
        return noBidMessage;
    }
}
