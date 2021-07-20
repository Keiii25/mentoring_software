package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewContractUI;
import WebService.GetInterfaces.GetContract;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.SignContract;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViewContractController extends Controller implements GetUser, SignContract, GetContract {
    private ViewContractUI ui;
    private User user;
    private Boolean isStudent;
    private final String STUDENT_TITLE = "My Contracts";
    private final String TUTOR_TITLE = "Job Contracts";

    public ViewContractController(JSONArray myContracts, Boolean isStudent, User user, View view) {
        super(view);
        page = new ViewContractUI();
        this.ui = (ViewContractUI) page;
        this.user = user;
        this.view = view;
        this.isStudent = isStudent;
        setUpPage(myContracts);
    }

    private void setUpPage(JSONArray myContracts) {
        // setting up back button to return to Home Page
        JButton backButton = ui.getBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user.updateActionUI();
                new HomePageController(view, user).changeView();
            }
        });

        // setting title of ViewClasses.UI depending on role of user
        JLabel title = ui.getTitle();
        if (isStudent) {
            title.setText(STUDENT_TITLE);
        } else {
            title.setText(TUTOR_TITLE);
        }

        // checking if user have any contracts
        JScrollPane pageScrollPane = ui.getScrollPane();
        if (myContracts.length() > 0) {
            //initialising formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            // remove "no bids available message"
            pageScrollPane.remove(ui.getNoContractMessage());
            pageScrollPane.revalidate();
            pageScrollPane.repaint();

            // creating JPanel for page contents
            JPanel content = new JPanel(new GridLayout(0, 1));

            // for loop to go through each of the contracts
            for (int i = 0; i < myContracts.length(); i++) {
                // getting contract and its JSON Objects
                JSONObject currentContract = myContracts.getJSONObject(i);
                JSONObject tutorDetail = currentContract.getJSONObject("firstParty");
                JSONObject studentDetail = currentContract.getJSONObject("secondParty");
                JSONObject subject = currentContract.getJSONObject("subject");
                JSONObject paymentInfo = currentContract.getJSONObject("paymentInfo");
                JSONObject lessonInfo = currentContract.getJSONObject("lessonInfo");
                JSONObject additionalInfo = currentContract.getJSONObject("additionalInfo");

                // getting student's competency level in chosen subject
                JSONArray studentCompetencies= getUser(studentDetail.getString("id"), 3).getJSONArray("competencies");
                int studentCompetencyLevel = 0;

                int j = 0;
                Boolean found = false;
                while (!found && j <studentCompetencies.length()) {
                    if (studentCompetencies.getJSONObject(j).getJSONObject("subject").getString("id").equals(subject.getString("id"))) {
                        studentCompetencyLevel = studentCompetencies.getJSONObject(j).getInt("level");
                        found = true;
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
                        found = true;
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

                // ending content string
                contractString += "<br/></html>";

                // making a panel for the contract and adding the JLabel with contract details to it
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel(contractString));

                // add contract JPanel to content
                content.add(panel);

                // if contract is not signed and the user is not the one who first signed this contract
                if (!(additionalInfo.getString("firstSigner")).equals(user.getId()) && dateSigned == null) {
                    // adding a button to sign contract or reject contract to panel
                    JPanel buttonPanel = signContractButtons(currentContract.getString("id"), i, myContracts, view);
                    content.add(buttonPanel);
                }
            }

            // adding content to scrollPane
            pageScrollPane.getViewport().add(content);
        }
    }

    private JPanel signContractButtons(String contractId, int index, JSONArray myContracts, View view) {
        // initialise button panel
        JPanel buttonPanel = new JPanel();

        // create sign contract button
        JButton sign = new JButton("Sign");
        // add action listener
        sign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // sign the contract and get the updated signed contract
                signContract(contractId, LocalDateTime.now());
                JSONObject updatedContract = getContract(contractId);

                // make an updated myContracts with signed contract
                JSONArray myContractsUpdated = new JSONArray();
                for (int i = 0; i < myContracts.length(); i++) {
                    if (i == index) {
                        myContractsUpdated.put(updatedContract);
                    } else {
                        myContractsUpdated.put(myContracts.getJSONObject(i));
                    }
                }

                // pop up message about successful signing
                JOptionPane.showMessageDialog(null, "Contract has been successfully signed.");

                // refresh page
                new ViewContractController(myContractsUpdated, isStudent, user, view).changeView();
            }
        });

        // add sign button onto buttonPanel
        buttonPanel.add(sign);

        return buttonPanel;
    }
}
