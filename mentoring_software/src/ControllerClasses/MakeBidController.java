package ControllerClasses;

import ModelClasses.User;
import ViewClasses.MakeBidUI;
import ViewClasses.View;
import WebService.GetInterfaces.GetSubjects;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.MakeBid;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MakeBidController extends Controller implements GetUser, GetSubjects, MakeBid {
    private JSONArray subjects = getSubjects(1);
    private int competencyLevelValue;
    private int hoursPerLessonValue;
    private int sessionsPerWeekValue;
    private int rateValue;
    private int contractDurationValue;
    private User user;
    private MakeBidUI ui;

    public MakeBidController(User user, View view) {
        super(view);
        page = new MakeBidUI();
        this.user = user;
        this.ui = ((MakeBidUI) page);
        this.ui.addPublishListener(new MakeBidListener());
        this.ui.addBackListener(new BackListener());
    }

    class MakeBidListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String subjectId = null;
            if (verify(user.getId())) {
                // finding ID of selected subject
                String selectedSubject = ui.getSubject();
                for (int i = 0; i < subjects.length(); i++) {
                    JSONObject subject = subjects.getJSONObject(i);
                    if (selectedSubject.equals(subject.getString("name") + ": " + subject.getString("description"))) {
                        subjectId = subject.getString("id");
                    }
                }

                // getting bid type
                String bid = ui.getTypeBid();
                Boolean isOpen = true;
                if (bid.equals("open")) {
                    isOpen = true;
                } else if (bid.equals("close")) {
                    isOpen = false;
                }

                // getting contract duration
                contractDurationValue = ui.getContractDuration();

                makeBid(user.getId(), subjectId, competencyLevelValue, hoursPerLessonValue, sessionsPerWeekValue, rateValue, ui.getRateType(), isOpen, contractDurationValue);
                JOptionPane.showMessageDialog(null, "Bid has been created successfully.");
                backToHomePage();
            }

        }

        // function to check if user inputs on GUI is valid
        private Boolean verify(String userId) {
            // initialise valid, chosenSubjectId and get student's competencies
            Boolean valid = false;
            JSONArray competencies = getUser(userId, 3).getJSONArray("competencies");
            String chosenSubjectId = null;
            String competencyLevel = ui.getCompetencyLevel();
            String hoursPerLesson = ui.getHourPerLesson();
            String sessionsPerWeek = ui.getSessionPerWeek();
            String rate = ui.getRate();

            // getting user input and finding which subject was chosen and take it's id as chosenSubjectId
            String selectedSubject = ui.getSubject();
            // for loop to through each subject and check whether it's name and description matches the user's selection
            for (int i = 0; i < subjects.length(); i++) {
                JSONObject subject = subjects.getJSONObject(i);
                if (selectedSubject.equals(subject.getString("name") + ": " + subject.getString("description"))) {
                    chosenSubjectId = subject.getString("id");
                }
            }

            // initialise minCompLevel
            int minCompLevel = 2;

            // initialise i and found for while loop
            int i = 0;
            Boolean found = false;

            // while loop to search their competencies to see if student have competency in the subject they chosen
            while (!found && i < competencies.length()) {
                if (competencies.getJSONObject(i).getJSONObject("subject").getString("id").equals(chosenSubjectId)) {
                    found = true;
                    minCompLevel = competencies.getJSONObject(i).getInt("level") + 2;
                }
                i++;
            }

            // check if all input fields have been filled
            if (competencyLevel.length() > 0 && hoursPerLesson.length() > 0 && sessionsPerWeek.length() > 0 && rate.length() > 0) {
                // initialise an errorMessage
                String errorMessage = "\n";

                // checking competency input input
                try {
                    if (Integer.parseInt(competencyLevel) >= minCompLevel) {
                        competencyLevelValue = Integer.parseInt(competencyLevel);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    errorMessage += "Minimum competency level must at least " + minCompLevel + ".\n";
                }

                // checking if hours per lesson input
                try {
                    if (Integer.parseInt(hoursPerLesson) > 0 && Integer.parseInt(hoursPerLesson) < 24) {
                        hoursPerLessonValue = Integer.parseInt(hoursPerLesson);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    errorMessage += "Hours per lesson must be larger than 0 and can not exceed 24 hours.\n";
                }

                // checking sessions per week input
                try {
                    if (Integer.parseInt(sessionsPerWeek) > 0) {
                        sessionsPerWeekValue = Integer.parseInt(sessionsPerWeek);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    errorMessage += "Sessions per week must be larger than 0.\n";
                }

                // checking rate input
                try {
                    if (Integer.parseInt(rate) > 0) {
                        rateValue = Integer.parseInt(rate);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    errorMessage += "Rates must be larger than $0.\n";
                }

                // if no exceptions occurred, make valid true
                if (errorMessage.equals("\n")) {
                    valid = true;
                } else { // else display error message with a pop up window
                    JOptionPane.showMessageDialog(null, errorMessage);
                }
            } else { // else display an error message with a pop up window
                JOptionPane.showMessageDialog(null, "Please fill in the form fully");
            }

            return valid;
        }
    }

    class BackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            backToHomePage();
        }
    }

    private void backToHomePage() {
        user.updateActionUI();
        new HomePageController(view, user).changeView();
    }

}
