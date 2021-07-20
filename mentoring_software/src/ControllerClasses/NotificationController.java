package ControllerClasses;

import ModelClasses.User;
import ViewClasses.NotificationsUI;
import ViewClasses.View;
import WebService.DeleteInterfaces.DeleteContract;
import WebService.PostInterfaces.MakeContract;
import WebService.PostInterfaces.SignContract;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationController extends Controller implements MakeContract, SignContract, DeleteContract {
    private User user;
    private NotificationsUI ui;

    public NotificationController(View view, User user) {
        super(view);
        this.user = user;
        page = new NotificationsUI();
        ui = (NotificationsUI) page;
        setUpPage();
    }

    private void setUpPage() {
        // getting scroll pane from ViewClasses.UI class
        JScrollPane scrollPane = ui.getScrollPane();

        // getting contracts that are almost expired
        JSONArray aboutToExpireContracts = user.getAboutToExpireContracts();

        // JPanel to hold page's content
        JPanel content = new JPanel(new GridLayout(0, 1));
        // string that holds information of contracts that will be displayed. initialise header and number of contracts
        String contentString = "<html><h1>Almost Expired Contracts</h1><br/>";
        contentString += "You have " + aboutToExpireContracts.length() + " contract(s) that are about to expire.<br/><br/>";

        // for loop to go through all contracts
        for (int i = 0; i < aboutToExpireContracts.length(); i++) {
            JSONObject currentContract = aboutToExpireContracts.getJSONObject(i);

            // contract header
            contentString += "<h2>Contract " + (i+1) + "</h2>";

            // subject details
            contentString += "Subject: " + currentContract.getJSONObject("subject").getString("name") + ": " + currentContract.getJSONObject("subject").getString("description") + "<br/><br/>";

            //initialising formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            // getting date of contract creation anf expiry
            LocalDateTime dateCreated = LocalDateTime.parse(currentContract.getString("dateCreated"), formatter);
            LocalDateTime expiryDate = LocalDateTime.parse(currentContract.getString("expiryDate"), formatter);

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

            contentString += "Date Created: " + hourCreated + ":" + minuteCreated + " (" +  dateCreated.getDayOfMonth() + "/"  + dateCreated.getMonthValue() + "/" + dateCreated.getYear() + ")<br/>";

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
            contentString += "Expiry Date: " + hourExpiry + ":" + minuteExpiry + " (" +  expiryDate.getDayOfMonth() + "/"  + expiryDate.getMonthValue() + "/" + expiryDate.getYear() + ")<br/><br/>";

            if (currentContract.getJSONObject("firstParty").getString("id").equals(user.getId())) {
                contentString += "Student: " + currentContract.getJSONObject("secondParty").getString("givenName") + " " + currentContract.getJSONObject("secondParty").getString("familyName") + "<br/>";
            } else {
                contentString += "Tutor: " + currentContract.getJSONObject("firstParty").getString("givenName") + " " + currentContract.getJSONObject("firstParty").getString("familyName") + "<br/>";

                JSONArray tutorQualifications = currentContract.getJSONObject("additionalInfo").getJSONArray("tutorQualifications");

                contentString += "Qualifications:<br/>";

                if (tutorQualifications.length() > 0) {
                    // for loop to go through all tutor qualifications that are verified
                    for (int j = 0; j < tutorQualifications.length(); j++) {
                        if (tutorQualifications.getJSONObject(j).getBoolean("verified")) {
                            contentString += (j+1) + ". Title: " + tutorQualifications.getJSONObject(j).getString("title") + "<br/>";
                            contentString += "   Description: " + tutorQualifications.getJSONObject(j).getString("description") + "<br/><br/>";
                        }
                    }
                } else {
                    contentString += "- None<br/><br/>";
                }
            }

            // payment information
            contentString += "Payment Information:" + "<br/>";
            contentString += "Rate: $" + currentContract.getJSONObject("paymentInfo").getInt("rate") + " " + currentContract.getJSONObject("paymentInfo").getString("rateType") + "<br/><br/>";

            // lesson information
            contentString += "Lesson Information:" + "<br/>";
            contentString += "Duration of each lesson: " + currentContract.getJSONObject("lessonInfo").getInt("hoursPerLesson") + " hours per lesson<br/>";
            contentString += "Number of sessions per week: " + currentContract.getJSONObject("lessonInfo").getInt("sessionsPerWeek") + " sessions per week<br/><br/>";

            // additional information
            contentString += "Additional Information:<br/>";
            if (currentContract.getJSONObject("additionalInfo").getBoolean("oneFreeLesson")) {
                contentString += "- One free lesson is provided.<br/>";
            } else {
                contentString += "- No free lessons are provided.<br/>";
            }
        }

        // closing constantString and adding it as a JLabel to content
        contentString += "</html>";
        content.add(new JLabel(contentString));

        // adding content to scrollPane
        scrollPane.getViewport().add(content);

        // getting the ok button from ViewClasses.UI page and giving it the function to move onto the home page
        JButton acknowledgeButton = ui.getOKButton();
        acknowledgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // for loop to go through all contracts
                for (int i = 0; i < aboutToExpireContracts.length(); i++) {
                    // get contract at current index
                    JSONObject currentContract = aboutToExpireContracts.getJSONObject(i);

                    //initialise formatter
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

                    //getting IDs from current contract
                    String tutorId = currentContract.getJSONObject("firstParty").getString("id");
                    String studentId = currentContract.getJSONObject("secondParty").getString("id");
                    String subjectId = currentContract.getJSONObject("subject").getString("id");

                    // getting date details
                    LocalDateTime dateCreated = LocalDateTime.parse(currentContract.getString("dateCreated"), formatter);
                    LocalDateTime signDate = null;
                    if (!currentContract.get("dateSigned").equals(null)) {
                        LocalDateTime.parse(currentContract.getString("dateSigned"), formatter);
                    }

                    // getting info objects from current contract
                    JSONObject paymentInfo = currentContract.getJSONObject("paymentInfo");
                    JSONObject lessonInfo = currentContract.getJSONObject("lessonInfo");
                    JSONObject additionalInfo = currentContract.getJSONObject("additionalInfo");

                    // checking role of user and set the seen status for the user's role to true
                    // check if user is the tutor in the contract
                    if ((user.getId()).equals(currentContract.getJSONObject("firstParty").getString("id"))) {
                        additionalInfo.put("tutorSeen", true);
                    } else { // else user is the student
                        additionalInfo.put("studentSeen", true);
                    }

                    // make a copy of the contract with this updated detail
                    JSONObject contractCopy = makeContract(tutorId, studentId, subjectId, paymentInfo, lessonInfo, additionalInfo, tutorId, dateCreated);

                    //sign the copy
                    if (signDate != null) {
                        signContract(contractCopy.getString("id"), signDate);
                    }

                    //delete old contract
                    deleteContract(currentContract.getString("id"));
                }

                //change page to home page
                new HomePageController(view, user).changeView();
            }
        });
    }
}
