package ControllerClasses;

import ModelClasses.User;
import ViewClasses.TutorDashboardUI;
import ViewClasses.View;
import WebService.GetInterfaces.GetBids;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TutorDashboardController extends Controller implements GetBids, MakeChangeUIButton {

    private TutorDashboardUI ui;
    private User user;
    private Timer timer;
    private JSONArray validBids;

    public TutorDashboardController(View view, User user, JSONArray validBids) {
        super(view);
        page = new TutorDashboardUI();
        this.ui = (TutorDashboardUI) page;
        this.user = user;
        this.validBids = validBids;
        this.ui.addBackListener(new BackListener());
        setUpPage();
        setTimer();
    }

    void setUpPage() {
        JScrollPane scrollPane = ui.getScrollPane();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JPanel bidDisplay = new JPanel(new GridLayout(0, 1));

        //retrieve active subscribed bids
        JSONArray bids = getBids(true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        for (int i = 0; i < bids.length(); i++) {
            JSONObject currentBid = bids.getJSONObject(i);
            LocalDateTime bidTimeOfCreation = LocalDateTime.parse(currentBid.getString("dateCreated"), formatter);
            LocalDateTime bidDeadline = bidTimeOfCreation.plusMinutes(30);

            //if the bid has not expired
            if (currentBid.get("dateClosedDown").equals(null) && LocalDateTime.now().isBefore(bidDeadline)) {
                JSONObject additionalInfo = currentBid.getJSONObject("additionalInfo");
                if (additionalInfo.has("subscribers")) {
                    //retrieve the list of subscribers to the bid
                    //checks if the user is the subscriber of the bid
                    JSONArray subscribers = additionalInfo.getJSONArray("subscribers");
                    for (int j = 0; j < subscribers.length(); j++) {
                        if (subscribers.getJSONObject(j).getString("id").equals(user.getId())) {

                            // formatting how the Bid will be displayed
                            String contentString = "<html>Subject: \"" + currentBid.getJSONObject("subject").getString("name") + ": " + currentBid.getJSONObject("subject").getString("description") + "\"<br/>";
                            contentString += "ModelClasses.Student: " + currentBid.getJSONObject("initiator").getString("givenName") + " " + currentBid.getJSONObject("initiator").getString("familyName") + "<br/>";
                            //contentString += "Deadline: " + bidDeadline.getHour() + ":" + bidDeadline.getMinute() + " (" + bidDeadline.getDayOfMonth() + "/" + bidDeadline.getMonthValue() + "/" + bidDeadline.getYear() + ")<br/>";
                            contentString += "Minimum Competency: Level " + additionalInfo.getInt("minimumCompetency") + "<br/>";
                            contentString += "Preferred hours per lesson: " + additionalInfo.getInt("hoursPerLesson") + " hours<br/>";
                            contentString += "number of sessions per week: " + additionalInfo.getInt("sessionsPerWeek") + " sessions/week<br/>";
                            contentString += "Rate: $" + additionalInfo.getInt("rate") + " " + additionalInfo.getString("rateType") + "<br/>";
                            contentString += "Bid Type: " + currentBid.getString("type") + "<br/>";
                            contentString += "Contract Duration: " + additionalInfo.getInt("contractDuration") + " months";
                            contentString += "<br/></html>";

                            // making a new JPanel to display this bi
                            JLabel label = new JLabel("Bid Information");
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            bidDisplay.add(label);
                            bidDisplay.add(new JLabel(contentString));

                            //JPanel for the buttons
                            JPanel buttonDisplay = new JPanel();

                            //check if the user has made an offer to this bid before
                            Boolean found = false;
                            int l = 0;
                            JSONObject oldOffer = new JSONObject();
                            JSONArray messages = currentBid.getJSONArray("messages");
                            while (l < messages.length() && !found) {
                                // if current message is made my tutor, change found to true
                                if (user.getId().equals(messages.getJSONObject(l).getJSONObject("poster").getString("id"))) {
                                    found = true;
                                    oldOffer = messages.getJSONObject(l);
                                }
                                l++;
                            }

                            //if the user has not made an offer to the bid add a button to MakeOfferController to make offer
                            if (!found) {
                                // else add a make offer button
                                JButton goToMakeOffer = makeChangeUIButton("Make Offer", new MakeOfferController(user, view, currentBid.getString("id"), validBids, false));
                                buttonDisplay.setPreferredSize(new Dimension(50, 20));
                                buttonDisplay.add(goToMakeOffer);
                                bidDisplay.add(buttonDisplay);
                            }

                            //display the offers made to the bid
                            int count = 0;
                            if (messages.length() > 0) {
                                for (int k = 0; k < messages.length(); k++) {
                                    if (messages.getJSONObject(k).getJSONObject("additionalInfo").has("offers")) {
                                        JSONObject message = messages.getJSONObject(k);
                                        JSONObject poster = message.getJSONObject("poster");
                                        JSONObject offer = message.getJSONObject("additionalInfo").getJSONObject("offers");
                                        JSONArray qualifications = offer.getJSONArray("tutorQualifications");

                                        // creating the body that will be the offer display message
                                        String content = "<html>";
                                        content += "ModelClasses.Tutor Name: " + poster.getString("givenName") + " " + poster.getString("familyName") + "<br/>";
                                        content += "Qualifications:<br/>";

                                        // checking if qualifications is not empty
                                        if (qualifications.length() > 0) {
                                            // for loop to add verified qualifications
                                            for (int m = 0; m < qualifications.length(); m++) {
                                                if (qualifications.getJSONObject(m).getBoolean("verified")) {
                                                    content += (m + 1) + ". Title: " + qualifications.getJSONObject(m).getString("title") + "<br/>";
                                                    content += "   Description: " + qualifications.getJSONObject(m).getString("description") + "<br/><br/>";
                                                }
                                            }
                                        } else {
                                            contentString += "- None<br/><br/>";
                                        }

                                        content += "Rate: $" + offer.getInt("rate") + " " + offer.getString("rateType") + " (Original rate: $" + currentBid.getJSONObject("additionalInfo").getInt("rate") + " " + currentBid.getJSONObject("additionalInfo").getString("rateType") + ")<br/>";
                                        content += "Hours per Lesson: " + offer.getInt("hoursPerLesson") + " hours (Original hours per lesson: " + currentBid.getJSONObject("additionalInfo").getInt("hoursPerLesson") + " hours)<br/>";
                                        content += "Sessions per week: " + offer.getInt("sessionsPerWeek") + " (Original sessions per week: " + currentBid.getJSONObject("additionalInfo").getInt("sessionsPerWeek") + ")<br/>";
                                        content += "Duration of contract: " + offer.getInt("contractDuration") + " months (Original contract duration: " + currentBid.getJSONObject("additionalInfo").getInt("contractDuration") + " months)<br/>";

                                        if (offer.getBoolean("oneFreeLesson")) {
                                            content += "One free lesson: Yes <br/>";
                                        } else {
                                            content += "One free lesson: No <br/>";
                                        }

                                        content += "</html>";
                                        count += 1;
                                        JLabel offerLabel = new JLabel("Offer " + count);
                                        offerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                        bidDisplay.add(offerLabel);
                                        bidDisplay.add(new JLabel(content));
                                        JPanel buttonDisplay1 = new JPanel();

                                        //if the user has made an offer to this bid, add a button to allow user to update offer
                                        if (poster.getString("id").equals(user.getId())) {
                                            JButton goToUpdateOffer = makeChangeUIButton("Update Offer", new UpdateOfferController(view, user, currentBid.getString("id"), oldOffer, validBids));
                                            buttonDisplay1.setPreferredSize(new Dimension(50, 20));
                                            buttonDisplay1.add(goToUpdateOffer);
                                            bidDisplay.add(buttonDisplay1);
                                        }
                                    }
                                }
                            }
                            //panel.add(bidDisplay);
                        }
                    }
                }
            }
        }
        panel.add(bidDisplay);
        scrollPane.getViewport().add(panel);
    }


        class BackListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer != null) {
                    timer.stop();
                }
                user.updateActionUI();
                new HomePageController(view, user).changeView();
            }
        }

        //initialise the timer and set the actionListener for the timer
        private void setTimer() {
            timer = new Timer(10000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    timer.restart();
                    update();
                }
            });
            timer.start();
            timer.setRepeats(true);
        }

        //notify the observer every 10 seconds
        private void update() { ;
            if (view.getCurrentUI() instanceof TutorDashboardUI) {
                setUpPage();
            }
        }
    }

