package ViewClasses;

import javax.swing.*;

public class ViewOfferContractsUI extends UI {
    private JPanel rootPanel;
    private JButton backButton;
    private JPanel noOfferContractsMessage;
    private JScrollPane scrollPane;

    public ViewOfferContractsUI() {
        setRootPanel(rootPanel);
    }

    public JScrollPane getScrollPane() { return scrollPane; }

    public JButton getBackButton() { return backButton; }

    public JPanel getNoOfferContractsMessage() { return noOfferContractsMessage; }
}
