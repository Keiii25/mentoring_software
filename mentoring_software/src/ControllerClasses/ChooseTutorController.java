package ControllerClasses;

import ModelClasses.User;
import ViewClasses.ChooseTutorUI;
import ViewClasses.View;
import WebService.DecodeJwt;
import WebService.DeleteInterfaces.DeleteContract;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.LoginJwt;
import WebService.PostInterfaces.MakeContract;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;

public class ChooseTutorController extends Controller implements LoginJwt, DecodeJwt, GetUser, MakeContract, DeleteContract {
    private ChooseTutorUI ui;
    private JSONObject expiredContract;
    private JSONArray expiredContracts;
    private User student;
    private String tutorId = "";

    public ChooseTutorController(View view, JSONObject expiredContract, JSONArray expiredContracts, User student) {
        super(view);
        page = new ChooseTutorUI();
        ui = (ChooseTutorUI) page;
        this.expiredContracts = expiredContracts;
        this.expiredContract = expiredContract;
        this.student = student;
        setUpPage();
    }

    private void setUpPage() {
        // adding action listener to cancel button to go back to ViewExpiredContracts page
        JButton cancelButton = ui.getCancelButton();
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewExpiredContractsController(view, expiredContracts, student).changeView();
            }
        });

        // adding action listener to confirm button
        JButton confirmButton = ui.getConfirmButton();
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // getting the tutor's id based on user input, validating the input and tutor
                getTutorId();

                if (tutorId.length() > 0) {
                    // extracting relevant information from expired contract
                    String subjectId = expiredContract.getJSONObject("subject").getString("id");
                    JSONObject paymentInfo = expiredContract.getJSONObject("paymentInfo");
                    JSONObject lessonInfo = expiredContract.getJSONObject("lessonInfo");
                    JSONObject additionalInfo = expiredContract.getJSONObject("additionalInfo");

                    // changing contract duration to 1 month so that the offer is open for one month if offerContractDuration is not present
                    if (!additionalInfo.has("offerContractDuration")) {
                        additionalInfo.put("offerContractDuration", additionalInfo.getInt("contractDuration"));
                        additionalInfo.put("contractDuration", 1);
                    }

                    // setting oneFreeLesson to false
                    additionalInfo.put("oneFreeLesson", false);

                    // make offer contract
                    makeContract(tutorId, student.getId(), subjectId, paymentInfo, lessonInfo, additionalInfo, student.getId(), LocalDateTime.now());

                    // delete expired contract
                    deleteContract(expiredContract.getString("id"));

                    // delete expired contract from expiredContracts
                    int i = 0;
                    Boolean found = false;
                    while (i < expiredContracts.length() && !found) {
                        if (expiredContracts.getJSONObject(i).getString("id").equals(expiredContract.getString("id"))) {
                            expiredContracts.remove(i);
                            found = true;
                        }
                    }

                    // go back to last page
                    new ViewExpiredContractsController(view, expiredContracts, student).changeView();
                }
            }
        });
    }

    // function to validate the tutor username inputted
    private void getTutorId() {
        // get tutor's username entered
        String usernameInput = ui.getTutorUsername();

        // check if text field is empty
        if (usernameInput.length() == 0) {
            JOptionPane.showMessageDialog(null, "Please enter contract's recipient tutor's username");
        } else {
            String id = "";
            // try to log in with input
            try {
                // login with username and get the ID from the decoded jwt token
                id = decodeJwt(loginJwt(ui.getTutorUsername(), ui.getTutorUsername()).getString("jwt")).getString("sub");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Username entered does not exist. Please try again.");
            }

            // getting tutor competencies, subjectID, minimum competency level and initialising tutorCompetencyLevel
            JSONArray tutorCompetencies = getUser(id, 3).getJSONArray("competencies");
            String subjectId = expiredContract.getJSONObject("subject").getString("id");
            int minComplevel = expiredContract.getJSONObject("additionalInfo").getInt("minimumCompetency");
            int tutorCompetencyLevel = 0;

            // try to see if the tutor teaches the subject the contract specifies
            try {
                // while loop to go through each competencies in tutor for tutor's competency level in contract subject
                int i = 0;
                Boolean found = false;
                while (i < tutorCompetencies.length() && !found) {
                    if  (tutorCompetencies.getJSONObject(i).getJSONObject("subject").getString("id").equals(subjectId)) {
                        found = true;
                        tutorCompetencyLevel = tutorCompetencies.getJSONObject(i).getInt("level");
                    }

                    i++;
                }

                // if tutor didn't teach the contract subject, raise exception
                if (!found) {
                    throw new Exception();
                }

                // try to see if tutor's competency level is at least the minimum competency level of the contract
                try {
                    // check if tutor meet minimum competency Level
                    if (tutorCompetencyLevel >= minComplevel) {
                        // set id as tutorId
                        tutorId = id;
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Tutor with given username does not meet minimum competency level of contract. Please try a different tutor");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Tutor with given username does not teach the contract's subject. Please try a different tutor");
            }
        }
    }
}
