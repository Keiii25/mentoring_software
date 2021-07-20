package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewOwnBidsUI;
import WebService.DeleteInterfaces.DeleteBid;
import WebService.DeleteInterfaces.DeleteMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViewOwnBidsController extends Controller implements DeleteBid, DeleteMessage, MakeChangeUIButton {

    private ViewOwnBidsUI ui;
    private User student;

    public ViewOwnBidsController(JSONArray validBids,  User student, View view) {
        super(view);
        page = new ViewOwnBidsUI();
        this.ui = (ViewOwnBidsUI) page;
        this.student = student;
        setUpPage(validBids);
    }

    private void setUpPage(JSONArray validBids) {
        // set up back button to go back to home page
        JButton backButton = ui.getBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                student.updateActionUI();
                new HomePageController(view, student).changeView();
            }
        });

        JScrollPane pageScrollPane = ui.getScrollPane();
        // check if there is anything to display on screen
        if (validBids.length() > 0) {
            // initialise formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            // remove "no bids available" message
            pageScrollPane.remove(ui.getNoBidMessage());
            pageScrollPane.revalidate();
            pageScrollPane.repaint();

            // create content panel to store all unsettled bids and my active bids that will be displayed
            JPanel content = new JPanel(new GridLayout(0, 1));

            // checking if validBids is not empty
            if (validBids.length() > 0) {
                // for loop to go through all valid bids
                for (int i = 0; i < validBids.length(); i++) {
                    // getting currentBid according to index i and its additional information and deadline
                    JSONObject currentBid = validBids.getJSONObject(i);
                    JSONObject additionalInfo = currentBid.getJSONObject("additionalInfo");
                    LocalDateTime bidDeadline = LocalDateTime.parse(currentBid.getString("dateClosedDown") + "Z", formatter);

                    // formatting how the bid's information is to be displayed
                    String contentString = "<html>Subject: \"" + currentBid.getJSONObject("subject").getString("name") + ": " + currentBid.getJSONObject("subject").getString("description") +  "\"<br/>";
                    contentString += "Student: " + currentBid.getJSONObject("initiator").getString("givenName") + " "  + currentBid.getJSONObject("initiator").getString("familyName") + "<br/>";
                    contentString += "Bid Type: " + currentBid.getString("type") + "<br/>";
                    contentString += "Deadline: " + bidDeadline.getHour() + ":" + bidDeadline.getMinute() + " (" +  bidDeadline.getDayOfMonth() + "/"  + bidDeadline.getMonthValue() + "/" + bidDeadline.getYear() + ")<br/>";
                    contentString += "Minimum Competency: Level " + additionalInfo.getInt("minimumCompetency") + "<br/>";
                    contentString += "Preferred hours per lesson: " + additionalInfo.getInt("hoursPerLesson") + " hours<br/>";
                    contentString += "number of sessions per week: " + additionalInfo.getInt("sessionsPerWeek") + " sessions/week<br/>";
                    contentString += "Rate: $" + additionalInfo.getInt("rate") + " " + additionalInfo.getString("rateType") + "<br/>";;
                    contentString += "Contract Duration: " + additionalInfo.getInt("contractDuration") + " months";
                    contentString += "<br/></html>";

                    // making a JPanel for this particular bid and adding the JLabel to it with contentString
                    JPanel bidDisplay = new JPanel(new GridLayout(0, 1));
                    bidDisplay.add(new JLabel(contentString));

                    // adding buttons to each of the Bids to do actions relating to bids
                    JPanel buttonDisplay = new JPanel();

                    JButton goToViewOffers = makeChangeUIButton("View Offers", new ViewOfferController(student, view, currentBid, validBids));
                    buttonDisplay.add(goToViewOffers);

                    // making a button to delete bid and add it ot buttonDisplay
                    JButton deleteButton = new JButton("Delete");
                    int index = i;
                    deleteButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // remove associated messages of the bid
                            JSONArray messages = currentBid.getJSONArray("messages");
                            for (int j = 0; j < messages.length(); j++) {
                                deleteMessage(messages.getJSONObject(j).getString("id"));
                            }

                            // delete the bid
                            deleteBid(currentBid.getString("id"));
                            validBids.remove(index);

                            // refresh the ViewClasses.UI
                            new ViewOwnBidsController(validBids, student, view).changeView();
                        }
                    });

                    // adding delete button to buttonDisplay
                    buttonDisplay.add(deleteButton);

                    // adding the buttons to bid display and adding bid display to content
                    bidDisplay.add(buttonDisplay);
                    content.add(bidDisplay);
                }
            }

            // adding content to scrollPane
            pageScrollPane.getViewport().add(content);
        }
    }
}
