package ViewClasses;

import javax.swing.*;

public class ChangeContractDetailsUI extends UI {
    private JPanel rootPanel;
    private JComboBox contractDuration;
    private JComboBox rateType;
    private JTextField rate;
    private JTextField durationLesson;
    private JTextField numSession;
    private JButton back;
    private JButton confirm;
    private JScrollPane detailPane;

    private String[] rateTypeOption = {"per hour", "per session"};
    private String[] contractDurationOptions = {"Please select a duration option", "3 months", "6 months", "12 months", "24 months"};

    public ChangeContractDetailsUI() {
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

    public JButton getBackButton() { return back; }

    public JButton getConfirmButton() { return confirm; }

    public JScrollPane getDetailDisplay() { return detailPane; }
}