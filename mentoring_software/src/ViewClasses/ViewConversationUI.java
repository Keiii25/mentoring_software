package ViewClasses;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Display the offer made to the bid and conversation between both parties
 */

public class ViewConversationUI extends UI {
    private JLabel rateType;
    private JLabel hoursLesson;
    private JLabel rate;
    private JLabel sessionWeek;
    private JLabel freeLesson;
    private JTextField message;
    private JButton send;
    private JButton backButton;
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JLabel nameLabel;
    private JLabel contractDuration;

    public ViewConversationUI() {
        // set this rootPanel as the ViewClasses.UI's rootPanel
        setRootPanel(rootPanel);
    }

    public String getMessage() {
        return message.getText();
    }

    public JScrollPane getScrollPane() { return scrollPane; }

    public JLabel getNameLabel() { return nameLabel; }

    public JLabel getRateType() { return rateType; }

    public JLabel getHoursLesson() { return hoursLesson; }

    public JLabel getRate() { return rate; }

    public JLabel getFreeLesson() { return freeLesson; }

    public JLabel getSessionWeek() { return sessionWeek; }

    public JLabel getContractDuration() {return contractDuration; }

    public void addSendMessageListener(ActionListener actionListener) {
        send.addActionListener(actionListener);
    }

    public void addBackButtonListener(ActionListener actionListener) { backButton.addActionListener(actionListener); }
}
