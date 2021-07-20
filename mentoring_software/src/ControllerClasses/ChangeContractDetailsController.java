package ControllerClasses;

import ModelClasses.User;
import ViewClasses.ChangeContractDetailsUI;
import ViewClasses.View;
import WebService.DecodeJwt;
import WebService.DeleteInterfaces.DeleteContract;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.LoginJwt;
import WebService.PostInterfaces.MakeContract;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class ChangeContractDetailsController extends Controller implements MakeContract, DeleteContract, GetUser, LoginJwt, DecodeJwt {
    private ChangeContractDetailsUI ui;
    private JSONObject expiredContract;
    private String tutorId;
    private User student;
    private JSONArray expiredContracts;

    public ChangeContractDetailsController(View view, JSONObject expiredContract, String tutorId, JSONArray expiredContracts, User student) {
        super(view);
        page = new ChangeContractDetailsUI();
        this.ui = (ChangeContractDetailsUI) page;
        this.expiredContract = expiredContract;
        this.tutorId = tutorId;
        this.expiredContracts = expiredContracts;
        this.student = student;
        setUpPage();
    }

    private void setUpPage() {
        // adding the expired contract's details that can be changed onto the page
        JScrollPane detailPane = ui.getDetailDisplay();

        // initialise contentString
        String contentString = "<html><h2>Expired Contract Details</h2><br/><br/>";

        // adding subject, minimum competency level, contract duration, rate, lesson duration and weekly session number into content string respectively
        contentString += "Subject: " + expiredContract.getJSONObject("subject").getString("name") + ": " + expiredContract.getJSONObject("subject").getString("description") + "<br/>";
        contentString += "Minimum Competency Level: "  +expiredContract.getJSONObject("additionalInfo").getInt("minimumCompetency") + "<br/>";
        contentString += "Contract Duration: " + expiredContract.getJSONObject("additionalInfo").getInt("contractDuration") + " months<br/>";
        contentString += "Rate: $" + expiredContract.getJSONObject("paymentInfo").getInt("rate") + " " + expiredContract.getJSONObject("paymentInfo").getString("rateType") + "<br/>";
        contentString += "Duration of each lesson: " + expiredContract.getJSONObject("lessonInfo").getInt("hoursPerLesson") + " hour(s)<br/>";
        contentString += "Number of Sessions per week: " + expiredContract.getJSONObject("lessonInfo").getInt("sessionsPerWeek") + "<br/><br/>";

        //adding tutor details
        contentString += "Tutor: "  + expiredContract.getJSONObject("firstParty").getString("givenName") + " "  + expiredContract.getJSONObject("firstParty").getString("familyName") + "<br/><br/>";
        contentString += "Tutor Qualifications:<br/>";

        // getting tutor qualifications
        JSONArray tutorQualifications = expiredContract.getJSONObject("additionalInfo").getJSONArray("tutorQualifications");
        // adding tutor qualifications
        if (tutorQualifications.length() > 0) {
            // for loop to go through all tutor qualifications that are verified
            for (int i = 0; i < tutorQualifications.length(); i++) {
                if (tutorQualifications.getJSONObject(i).getBoolean("verified")) {
                    contentString += (i+1) + ". Title: " + tutorQualifications.getJSONObject(i).getString("title") + "<br/>";
                    contentString += "   Description: " + tutorQualifications.getJSONObject(i).getString("description") + "<br/><br/>";
                }
            }
        } else {
            contentString += "- None<br/><br/>";
        }

        // closing contentString
        contentString += "</html>";

        // adding information onto detailPanel as a JPanel
        JPanel contentPanel = new JPanel(new GridLayout(0, 1));
        contentPanel.add(new JLabel(contentString));
        detailPane.getViewport().add(contentPanel);

        // add action listener to back button
        JButton backButton = ui.getBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewExpiredContractsController(view, expiredContracts, student).changeView();
            }
        });

        // add action listener to confirm button
        JButton confirmButton  = ui.getConfirmButton();
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if user inputs are valid
                if (validate()) {
                    // getting expired contract's details
                    JSONObject paymentInfo = expiredContract.getJSONObject("paymentInfo");
                    JSONObject lessonInfo = expiredContract.getJSONObject("lessonInfo");
                    JSONObject additionalInfo = expiredContract.getJSONObject("additionalInfo");

                    // updating paymentInfo
                    paymentInfo.put("rateType", ui.getRateType());
                    paymentInfo.put("rate", Integer.parseInt(ui.getRate()));

                    // updating lessonInfo
                    lessonInfo.put("hoursPerLesson", Integer.parseInt(ui.getDurationLesson()));
                    lessonInfo.put("sessionsPerWeek", Integer.parseInt(ui.getNumSession()));

                    // preparing additionalInfo for offer
                    // setting a 1 month time to accept offer
                    additionalInfo.put("contractDuration", 1);
                    additionalInfo.put("offerContractDuration", ui.getContractDuration());
                    // making this an offer contract
                    additionalInfo.put("offer", true);
                    // resetting seen status of student and tutor
                    additionalInfo.put("studentSeen", false);
                    additionalInfo.put("tutorSeen", false);
                    // setting oneFreeLesson to false
                    additionalInfo.put("oneFreeLesson", false);

                    // get tutor's id if it was not initially given
                    if (tutorId.length() == 0) {
                        // put all updated info object back into expiredContract
                        expiredContract.put("paymentInfo", paymentInfo);
                        expiredContract.put("lessonInfo", lessonInfo);
                        expiredContract.put("additionalInfo", additionalInfo);

                        // move to choose tutor page
                        new ChooseTutorController(view, expiredContract, expiredContracts, student).changeView();
                    } else {
                        // make offer contract
                        makeContract(tutorId, student.getId(), expiredContract.getJSONObject("subject").getString("id"), paymentInfo, lessonInfo, additionalInfo, student.getId(), LocalDateTime.now());

                        // delete expired contract
                        deleteContract(expiredContract.getString("id"));

                        // removing renewed bid from expired bids
                        int i = 0;
                        Boolean found = false;
                        while (i < expiredContracts.length() && !found) {
                            if (expiredContracts.getJSONObject(i).getString("id").equals(expiredContract.getString("id"))) {
                                expiredContracts.remove(i);
                                found = true;
                            }
                            i++;
                        }

                        // go back to last page
                        new ViewExpiredContractsController(view, expiredContracts, student).changeView();
                    }
                }
            }
        });
    }

    // function to validate the input given by user
    private Boolean validate(){
        // initialise valid
        Boolean valid = false;
        String rate = ui.getRate();
        String durationLesson = ui.getDurationLesson();
        String numSession = ui.getNumSession();

        // check if all needed fields are filled
        if (rate.length() > 0 && durationLesson.length() > 0 && numSession.length() > 0) {
            // initialise error message
            String errorMessage = "\n";

            // check if an integer larger than 0 is the input for rate
            try {
                if (Integer.parseInt(rate) < 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                errorMessage += "Rate must be an larger than 0.\n";
            }

            // check if an integer larger than 0 is the input for durationLesson
            try {
                if (Integer.parseInt(durationLesson) < 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                errorMessage += "Duration of lesson must be larger than 0 hours.\n";
            }

            // check if an integer larger than 0 is the input for numSession
            try {
                if (Integer.parseInt(numSession) < 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                errorMessage += "Number of sessions per week must be larger than 0.\n";
            }

            // check if there was any errors based on any changes to errorMessage
            if (errorMessage.equals("\n")) {
                // if there was no errors, make valid true
                valid = true;
            } else { //else, display error message
                JOptionPane.showMessageDialog(null, errorMessage);
            }
        } else {
            // display error message to tell the user to fill in the form fully
            JOptionPane.showMessageDialog(null, "Please fill in the form fully");
        }

        return valid;
    }
}
