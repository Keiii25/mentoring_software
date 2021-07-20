package ViewClasses;

import javax.swing.*;

public class ViewExpiredContractsUI extends UI {
    private JPanel rootPanel;
    private JButton back;
    private JScrollPane scrollPane;
    private JPanel noExpiredContractMessage;

    public ViewExpiredContractsUI() {
        setRootPanel(rootPanel);
    }

    public JScrollPane getScrollPane() {return scrollPane; }

    public JButton getBackButton() { return back; }

    public JPanel getNoExpiredContractMessage() { return noExpiredContractMessage; }
}
