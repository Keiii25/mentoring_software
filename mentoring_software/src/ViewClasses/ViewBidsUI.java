package ViewClasses;

import javax.swing.*;

public class ViewBidsUI extends UI {
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JButton back;
    private JPanel noBidMessage;

    /**
     * Allows ModelClasses.Tutor to view all valid bids in the system
     */
    public ViewBidsUI(){
        // set rootPanel of this ViewClasses.UI as rootPanel
        super.setRootPanel(rootPanel);
    }

    public JButton getBackButton() {
        return back;
    }

    public JPanel getNoBidMessage() {
        return noBidMessage;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
