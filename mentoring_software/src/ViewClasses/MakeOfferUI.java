package ViewClasses;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * An ViewClasses.UI that allows users to interact to make offers to bids
 */
public class MakeOfferUI extends UI {
    private JPanel rootPanel;
    private JButton confirm;
    private JCheckBox freeLesson;
    private JComboBox rateType;
    private JTextField rate;
    private JTextField durationLesson;
    private JTextField numSession;
    private JButton back;
    private JComboBox contractDuration;

    private String[] rateTypeOption = {"per hour", "per session"};
    private String[] contractDurationOptions = {"Please select a duration option", "3 months", "6 months", "12 months", "24 months"};

    public MakeOfferUI() {
        // set drop down table values of rateType
        rateType.setModel(new DefaultComboBoxModel(rateTypeOption));

        //set contract duration drop down list
        contractDuration.setModel(new DefaultComboBoxModel(contractDurationOptions));

        // set this rootPanel as the ViewClasses.UI's rootPanel
        setRootPanel(rootPanel);
    }

    public int getContractDuration() {
        String chosenOption = contractDuration.getSelectedItem().toString();
        int duration = 6;
        if (chosenOption.equals(contractDurationOptions[1])) {
            duration = 3;
        } else if (chosenOption.equals(contractDurationOptions[3])) {
            duration = 12;
        } else if (chosenOption.equals(contractDurationOptions[4])) {
            duration = 24;
        }

        return duration;
    }

    public Boolean getFreeLesson() {
        return freeLesson.isSelected();
    }

    public String getRateType() {
        return rateType.getSelectedItem().toString();
    }

    public String getRate() {
        return rate.getText().trim();
    }

    public String getDurationLesson() {
        return durationLesson.getText().trim();
    }

    public String getNumSession() {
        return numSession.getText().trim();
    }

    public void addMakeOfferListener(ActionListener actionListener) {
        confirm.addActionListener(actionListener);
    }

    public void addBackListener(ActionListener actionListener) {
        back.addActionListener(actionListener);
    }
}
