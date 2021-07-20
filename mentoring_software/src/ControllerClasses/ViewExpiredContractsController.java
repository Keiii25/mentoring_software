package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewExpiredContractsUI;
import WebService.DeleteInterfaces.DeleteContract;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.MakeContract;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViewExpiredContractsController extends Controller implements GetUser, DeleteContract, MakeContract {
    private ViewExpiredContractsUI ui;
    private User student;
    private JSONArray expiredContracts;

    // Constructor
    public ViewExpiredContractsController(View view, JSONArray expiredContracts, User student) {
        super(view);
        page = new ViewExpiredContractsUI();
        this.ui = (ViewExpiredContractsUI) page;
        this.student = student;
        this.expiredContracts = expiredContracts;
        setUpPage();
    }

    // function to add content to ViewClasses.UI page
    private void setUpPage() {
        // setting up back button to return to Home Page
        JButton backButton = ui.getBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                student.updateActionUI();
                new HomePageController(view, student).changeView();
            }
        });

        // check if there are any expired contracts to display
        if (expiredContracts.length() > 0) {
            // getting the page's scroll pane
            JScrollPane scrollPane = ui.getScrollPane();

            // removing "No expired contracts" message
            scrollPane.remove(ui.getNoExpiredContractMessage());
            scrollPane.revalidate();
            scrollPane.repaint();

            //initialising formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            // creating JPanel for page contents
            JPanel content = new JPanel(new GridLayout(0, 1));

            // for loop to go through each of the contracts
            for (int i = 0; i < expiredContracts.length(); i++) {
                // getting the contract and its JSON Objects
                JSONObject currentContract = expiredContracts.getJSONObject(i);
                JSONObject tutorDetail = currentContract.getJSONObject("firstParty");
                JSONObject studentDetail = currentContract.getJSONObject("secondParty");
                JSONObject subject = currentContract.getJSONObject("subject");
                JSONObject paymentInfo = currentContract.getJSONObject("paymentInfo");
                JSONObject lessonInfo = currentContract.getJSONObject("lessonInfo");
                JSONObject additionalInfo = currentContract.getJSONObject("additionalInfo");

                // getting student's competency level in chosen subject
                JSONArray studentCompetencies = getUser(studentDetail.getString("id"), 3).getJSONArray("competencies");
                int studentCompetencyLevel = 0;

                // using while loop to through student's competencies and find their competency level in the contract subject
                int j = 0;
                Boolean found = false;
                while (!found && j <studentCompetencies.length()) {
                    if (studentCompetencies.getJSONObject(j).getJSONObject("subject").getString("id").equals(subject.getString("id"))) {
                        studentCompetencyLevel = studentCompetencies.getJSONObject(j).getInt("level");
                    }
                    j++;
                }

                // getting tutor's competency level and qualification in chosen subject
                JSONArray tutorCompetencies = getUser(tutorDetail.getString("id"), 3).getJSONArray("competencies");
                int tutorCompetencyLevel = 0;

                j = 0;
                found = false;
                while (!found && j <tutorCompetencies.length()) {
                    if (tutorCompetencies.getJSONObject(j).getJSONObject("subject").getString("id").equals(subject.getString("id"))) {
                        tutorCompetencyLevel = tutorCompetencies.getJSONObject(j).getInt("level");
                    }
                    j++;
                }

                // getting date of contract creation, expiry and date when it was signed
                LocalDateTime dateCreated = LocalDateTime.parse(currentContract.getString("dateCreated"), formatter);
                LocalDateTime expiryDate = LocalDateTime.parse(currentContract.getString("expiryDate"), formatter);
                LocalDateTime dateSigned = null;

                if (!currentContract.get("dateSigned").equals(null)) {
                    dateSigned = LocalDateTime.parse(currentContract.getString("dateSigned"), formatter);
                }

                // initialising contract string for JPanel
                String contractString = "<html><div><h1>Contract " + (i+1) + "</h1></div>";
                // subject
                contractString += "Subject: " + subject.getString("name") + ": " + subject.getString("description") + "<br/><br/>";

                // contract date of creation
                String hourCreated;
                if (dateCreated.getHour() < 10) {
                    hourCreated = "0" + String.valueOf(dateCreated.getHour());
                } else {
                    hourCreated = String.valueOf(dateCreated.getHour());
                }

                String minuteCreated;
                if (dateCreated.getMinute() < 10) {
                    minuteCreated = "0" + String.valueOf(dateCreated.getMinute());
                } else {
                    minuteCreated = String.valueOf(dateCreated.getMinute());
                }

                contractString += "Date Created: " + hourCreated + ":" + minuteCreated + " (" +  dateCreated.getDayOfMonth() + "/"  + dateCreated.getMonthValue() + "/" + dateCreated.getYear() + ")<br/>";

                // contract date of expiry
                String hourExpiry;
                if (expiryDate.getHour() < 10) {
                    hourExpiry = "0" + String.valueOf(expiryDate.getHour());
                } else {
                    hourExpiry = String.valueOf(expiryDate.getHour());
                }

                String minuteExpiry;
                if (expiryDate.getMinute() < 10) {
                    minuteExpiry = "0" + String.valueOf(expiryDate.getMinute());
                } else {
                    minuteExpiry = String.valueOf(expiryDate.getMinute());
                }
                contractString += "Expiry Date: " + hourExpiry + ":" + minuteExpiry + " (" +  expiryDate.getDayOfMonth() + "/"  + expiryDate.getMonthValue() + "/" + expiryDate.getYear() + ")<br/>";

                // contract signed status
                if (dateSigned == null) {
                    contractString += "Status: Unsigned<br/><br/>";
                } else {
                    contractString += "Status: Signed<br/>";

                    String hourSigned;
                    if (dateSigned.getHour() < 10) {
                        hourSigned = "0" + String.valueOf(dateSigned.getHour());
                    } else {
                        hourSigned = String.valueOf(dateSigned.getHour());
                    }

                    String minuteSigned;
                    if (dateSigned.getMinute() < 10) {
                        minuteSigned = "0" + String.valueOf(dateSigned.getMinute());
                    } else {
                        minuteSigned = String.valueOf(dateSigned.getMinute());
                    }
                    contractString += "Date Signed: " + hourSigned + ":" + minuteSigned + " (" +  dateSigned.getDayOfMonth() + "/"  + dateSigned.getMonthValue() + "/" + dateSigned.getYear() + ")<br/><br/>";
                }

                // tutor details
                contractString += "Tutor: " + "<br/>";
                contractString += "Name: " + tutorDetail.getString("givenName") + " " + tutorDetail.getString("familyName") + "<br/>";
                contractString += "Competency level in chosen subject: " + tutorCompetencyLevel + "<br/><br/>";

                // adding tutor's qualification
                contractString += "Qualifications:<br/>";
                JSONArray tutorQualifications = additionalInfo.getJSONArray("tutorQualifications");

                if (tutorQualifications.length() > 0) {
                    // for loop to go through all tutor qualifications that are verified
                    for (j = 0; j < tutorQualifications.length(); j++) {
                        if (tutorQualifications.getJSONObject(j).getBoolean("verified")) {
                            contractString += (j+1) + ". Title: " + tutorQualifications.getJSONObject(j).getString("title") + "<br/>";
                            contractString += "   Description: " + tutorQualifications.getJSONObject(j).getString("description") + "<br/><br/>";
                        }
                    }
                } else {
                    contractString += "- None<br/><br/>";
                }

                // student details
                contractString += "Student: " + "<br/>";
                contractString += "Name: " + studentDetail.getString("givenName") + " " + studentDetail.getString("familyName") + "<br/>";
                contractString += "Competency level in chosen subject: " + studentCompetencyLevel + "<br/><br/>";

                // payment information
                contractString += "Payment Information:" + "<br/>";
                contractString += "Rate: $" + paymentInfo.getInt("rate") + " " + paymentInfo.getString("rateType") + "<br/><br/>";

                // lesson information
                contractString += "Lesson Information:" + "<br/>";
                contractString += "Duration of each lesson: " + lessonInfo.getInt("hoursPerLesson") + " hours per lesson<br/>";
                contractString += "Number of sessions per week: " + lessonInfo.getInt("sessionsPerWeek") + " sessions per week<br/><br/>";

                // additional information
                contractString += "Additional Information:<br/>";
                if (additionalInfo.getBoolean("oneFreeLesson")) {
                    contractString += "- One free lesson is provided.<br/>";
                } else {
                    contractString += "- No free lessons are provided.<br/>";
                }

                contractString += "Minimum competency level: " + additionalInfo.getInt("minimumCompetency");

                // ending content string
                contractString += "<br/></html>";

                // making a panel for the contract and adding the JLabel with contract details to it
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel(contractString));

                // add contract JPanel to content
                content.add(panel);

                // adding buttons to renew contract with same tutor or reuse contract to panel
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(getRenewContractButton(currentContract));
                buttonPanel.add(getReuseContractButton(currentContract));

                // adding a button to delete this expired contract only. associated bid of this contract is not included.
                JButton deleteButton = new JButton("Delete");
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // delete the contract
                        deleteContract(currentContract.getString("id"));

                        // while loop to remove this contract from expiredContract
                        Boolean found = false;
                        int j = 0;
                        while (j < expiredContracts.length() && !found) {
                            if (expiredContracts.getJSONObject(j).getString("id").equals(currentContract.getString("id"))) {
                                expiredContracts.remove(j);
                                found = true;
                            }

                            j++;
                        }

                        // refresh page
                        new ViewExpiredContractsController(view, expiredContracts, student).changeView();
                    }
                });
                buttonPanel.add(deleteButton);

                // add buttonPanel to content
                content.add(buttonPanel);
            }

            // adding content to scrollPane
            scrollPane.getViewport().add(content);
        }
    }

    private JButton getRenewContractButton(JSONObject expiredContract) {
        JButton renewButton =  new JButton("Renew Contract");
        renewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pop up window to ask if students want to change terms of engagement of contract
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to make changes to this contract before renewing it?",
                        "Make Changes",
                        JOptionPane.YES_NO_OPTION);

                // checking input
                if (n == 0) { // move to edit contract detail page
                    new ChangeContractDetailsController(view, expiredContract, expiredContract.getJSONObject("firstParty").getString("id"), expiredContracts, student).changeView();
                } else { // move on with deleting the expired contract and making an offer contract for tutor
                    // initialise formatter
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

                    // getting contract details
                    String tutorId = expiredContract.getJSONObject("firstParty").getString("id");
                    String studentId = expiredContract.getJSONObject("secondParty").getString("id");
                    String subjectId = expiredContract.getJSONObject("subject").getString("id");
                    LocalDateTime dateCreated = LocalDateTime.parse(expiredContract.getString("dateCreated"), formatter);
                    JSONObject paymentInfo = expiredContract.getJSONObject("paymentInfo");
                    JSONObject lessonInfo = expiredContract.getJSONObject("lessonInfo");
                    JSONObject additionalInfo = expiredContract.getJSONObject("additionalInfo");

                    // changing additionalInfo details
                    // setting a 1 month time to accept offer and adding a field to state the actual contract's offer duration
                    additionalInfo.put("offerContractDuration", additionalInfo.getInt("contractDuration"));
                    additionalInfo.put("contractDuration", 1);
                    // making this an offer contract
                    additionalInfo.put("offer", true);
                    // resetting seen status of student and tutor
                    additionalInfo.put("studentSeen", false);
                    additionalInfo.put("tutorSeen", false);
                    // setting oneFreeLesson to false
                    additionalInfo.put("oneFreeLesson", false);

                    // make the offer contract
                    makeContract(tutorId, studentId, subjectId, paymentInfo, lessonInfo, additionalInfo, studentId, dateCreated);

                    // delete the expired contract
                    deleteContract(expiredContract.getString("id"));

                    // remove the renewed contract from expiredContracts
                    int i = 0;
                    Boolean found = false;
                    while (i < expiredContracts.length() && !found) {
                        if (expiredContracts.getJSONObject(i).getString("id").equals(expiredContract.getString("id"))) {
                            expiredContracts.remove(i);
                            found = true;
                        }
                        i++;
                    }

                    // refresh page
                    new ViewExpiredContractsController(view, expiredContracts, student).changeView();
                }
            }
        });

        return renewButton;
    }

    private JButton getReuseContractButton(JSONObject expiredContract) {
        // initialise reuseButton
        JButton reuseButton = new JButton("Reuse Contract");

        // adding action listener to button
        reuseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pop up window to ask if students want to change terms of engagement of contract
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to make changes to this contract before reusing it?",
                        "Make Changes",
                        JOptionPane.YES_NO_OPTION);

                // checking input
                if (n == 0) { // move to edit contract detail page
                    new ChangeContractDetailsController(view, expiredContract, "", expiredContracts, student).changeView();
                } else { // go to page to enter tutor's username
                    new ChooseTutorController(view, expiredContract, expiredContracts, student).changeView();
                }
            }
        });

        return reuseButton;
    }
}
