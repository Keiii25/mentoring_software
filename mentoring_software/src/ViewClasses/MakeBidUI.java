package ViewClasses;

import WebService.GetInterfaces.GetSubjects;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MakeBidUI extends UI implements GetSubjects {
    // MakeBidUI.form element references
    private JComboBox subjectOption;
    private JTextField competencyLevel;
    private JTextField sessionsPerWeek;
    private JTextField rate;
    private JButton publish;
    private JPanel rootPanel;
    private JTextField hoursPerLesson;
    private JButton back;
    private JComboBox rateType;
    private JComboBox type;
    private JComboBox contractDuration;

    // class attributes
    private String[] rateTypes = {"per hour", "per session"};
    private String[] bidType = {"open", "close"};
    private String[] contractDurationOptions = {"Please select a duration option", "3 months", "6 months", "12 months", "24 months"};
    private JSONArray subjects = getSubjects(1);
    private ArrayList<String> subjectList = new ArrayList<>();

    // Constructor
    public MakeBidUI() {
        // add subjects into subjectList
        subjectsList();
        // set subjectOption drop down list with subjectList
        subjectOption.setModel(new DefaultComboBoxModel(subjectList.toArray()));
        // set rateType and type drop down list
        rateType.setModel(new DefaultComboBoxModel(rateTypes));
        type.setModel(new DefaultComboBoxModel(bidType));

        //set contract duration drop down list
        contractDuration.setModel(new DefaultComboBoxModel(contractDurationOptions));

        // set ViewClasses.UI's rootPanel as rootPanel
        super.setRootPanel(rootPanel);
    }

    public String getSubject() {
        return subjectOption.getSelectedItem().toString();
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

    public String getCompetencyLevel() {
        return competencyLevel.getText().trim();
    }

    public String getHourPerLesson() {
        return hoursPerLesson.getText().trim();
    }

    public String getSessionPerWeek() {
        return sessionsPerWeek.getText().trim();
    }

    public String getRate() {
        return rate.getText().trim();
    }

    public String getTypeBid() {
        return type.getSelectedItem().toString();
    }

    public String getRateType() {
        return rateType.getSelectedItem().toString();
    }

    public void addPublishListener(ActionListener actionListener){
        publish.addActionListener(actionListener);
    }

    public void addBackListener(ActionListener actionListener) {
        back.addActionListener(actionListener);
    }

    // function to add subjects into subjectList in the form of it's name and description
    private void subjectsList() {
        for (int i = 0; i < subjects.length(); i++) {
            JSONObject sub = subjects.getJSONObject(i);
            subjectList.add(sub.getString("name") + ": " + sub.getString("description"));
        }
    }
}
