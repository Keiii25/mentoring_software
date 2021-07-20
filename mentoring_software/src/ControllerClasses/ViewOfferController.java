package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewOffersUI;
import WebService.PostInterfaces.CloseDownBid;
import WebService.PostInterfaces.MakeContract;
import WebService.PostInterfaces.MakeMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class ViewOfferController extends Controller implements MakeContract, CloseDownBid, MakeMessage {
    private ViewOffersUI ui;
    private User user;
    private JSONObject bid;
    private JSONArray validBids;
    private JSONArray messages;
    private JSONArray messagesWithOffers = new JSONArray();

    public ViewOfferController(User user, View view, JSONObject bid, JSONArray validBids) {
        super(view);
        page = new ViewOffersUI();
        ui = (ViewOffersUI) page;
        this.user = user;
        this.bid = bid;
        this.validBids = validBids;
        this.ui.addBackListener(new BackListener());
        setUpPage();
    }

    private void setUpPage(){
        // extracting messages from chosen bid
        messages = bid.getJSONArray("messages");

        // if student is seeing offers of their bid
        if (bid.getJSONObject("initiator").getString("id").equals(user.getId())) {
            // for loop to go through all messages
            for (int i = 0; i < messages.length(); i++) {
                // checking if messages contain a "offers" section to identify it as an open bidding offer
                if (messages.getJSONObject(i).getJSONObject("additionalInfo").has("offers")) {
                    // extracting the offer from message
                    JSONObject offer = messages.getJSONObject(i);

                    messagesWithOffers.put(offer);
                }
            }
        } else { // if tutor is seeing offer
            // if the bid is an open bid
            if (bid.getString("type").equals("open")) {
                // for loop to go through all messages
                for (int i = 0; i < messages.length(); i++) {
                    // checking if messages contain a "offers" section to identify it as an open bidding offer
                    if (messages.getJSONObject(i).getJSONObject("additionalInfo").has("offers")) {
                        // extracting the offer from message
                        JSONObject offer = messages.getJSONObject(i);

                        messagesWithOffers.put(offer);
                    }
                }
            } else { // if the bid is a closed bid
                // for loop to go through all messages
                for (int i = 0; i < messages.length(); i++) {
                    // checking if messages contain a "offers" section to identify it as an open bidding offer
                    if (messages.getJSONObject(i).getJSONObject("additionalInfo").has("offers")) {
                        if (messages.getJSONObject(i).getJSONObject("poster").getString("id").equals(user.getId())) {
                            // extracting the offer from message
                            JSONObject offer = messages.getJSONObject(i);

                            messagesWithOffers.put(offer);
                        }
                    }
                }
            }
        }

        // getting scrollPane of ViewClasses.UI class
        JScrollPane pageScrollPane = ui.getScrollPane();

        // check if there is any open offers
        if (messagesWithOffers.length() > 0) {
            // remove "no bids available message"
            pageScrollPane.remove(ui.getNoOffersMessage());
            pageScrollPane.revalidate();
            pageScrollPane.repaint();

            // making a JPanel to hold all offers of that bid
            JPanel content = new JPanel(new GridLayout(0, 1));

            for (int i = 0; i < messagesWithOffers.length(); i++) {
                // getting offer and poster details and poster's qualifications
                JSONObject message = messagesWithOffers.getJSONObject(i);
                JSONObject poster = message.getJSONObject("poster");
                JSONObject offer = message.getJSONObject("additionalInfo").getJSONObject("offers");
                JSONArray qualifications = offer.getJSONArray("tutorQualifications");

                // creating the body that will be the offer display message
                String contentString = "<html>";
                contentString += "Tutor Name: " + poster.getString("givenName") + " "  + poster.getString("familyName") + "<br/>";
                contentString += "Qualifications:<br/>";

                // checking if qualifications is not empty
                if (qualifications.length() > 0) {
                    // for loop to add verified qualifications
                    for (int j = 0; j < qualifications.length(); j++) {
                        if (qualifications.getJSONObject(j).getBoolean("verified")) {
                            contentString += (j+1) + ". Title: " + qualifications.getJSONObject(j).getString("title") + "<br/>";
                            contentString += "   Description: " + qualifications.getJSONObject(j).getString("description") + "<br/><br/>";
                        }
                    }
                } else {
                    contentString += "- None<br/><br/>";
                }

                contentString += "Rate: $" + offer.getInt("rate") + " " + offer.getString("rateType") + " (Original rate: $" + bid.getJSONObject("additionalInfo").getInt("rate") + " " + bid.getJSONObject("additionalInfo").getString("rateType") + ")<br/>";
                contentString += "Hours per Lesson: " + offer.getInt("hoursPerLesson") + " hours (Original hours per lesson: " + bid.getJSONObject("additionalInfo").getInt("hoursPerLesson") + " hours)<br/>";
                contentString += "Sessions per week: " + offer.getInt("sessionsPerWeek") + " (Original sessions per week: " + bid.getJSONObject("additionalInfo").getInt("sessionsPerWeek") + ")<br/>";
                contentString += "Duration of contract: " + offer.getInt("contractDuration") + " months (Original contract duration: " + bid.getJSONObject("additionalInfo").getInt("contractDuration")  + " months)<br/>";

                if (offer.getBoolean("oneFreeLesson")) {
                    contentString += "One free lesson: Yes <br/>";
                } else {
                    contentString += "One free lesson: No <br/>";
                }

                contentString += "</html>";

                // making a JPanel for that specific offer and adding that JPanel to content
                JPanel offerDisplay = new JPanel();
                offerDisplay.add(new JLabel(contentString));

                // initialise button display
                JPanel buttonDisplay = new JPanel();

                // if viewer is the student that made the bid
                if (bid.getJSONObject("initiator").getString("id").equals(user.getId())) {
                    // add a button to allow student to accept closed offer
                    buttonDisplay.add(getAcceptOfferButton(bid, view, validBids, user, message));
                }

                // checking if the bid is a closed bid to allow student to view messages
                if (!bid.getString("type").equals("open")) {
                    // add view message button
                    JButton viewConversation = new JButton("View Messages");
                    viewConversation.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new ViewConversationController(user, view, message, bid, validBids).changeView();
                        }
                    });

                    // add viewConversation button into button display and add buttonDisplay into offerDisplay
                    buttonDisplay.add(viewConversation);
                }

                offerDisplay.add(buttonDisplay);

                // add offerDisplay into content
                content.add(offerDisplay);
            }

            // adding content panel to scrollPane
            pageScrollPane.getViewport().add(content);
        }
    }

    private JButton getAcceptOfferButton(JSONObject bid, View view, JSONArray validBids, User student, JSONObject offerMessage){
        JButton button = new JButton("Accept Offer");
        JSONObject poster = bid.getJSONObject("initiator");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // making paymentInfo for contract creation
                JSONObject paymentInfo = new JSONObject();
                paymentInfo.put("rate", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getInt("rate"));
                paymentInfo.put("rateType", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getString("rateType"));

                // making lessonInfo for contract creation
                JSONObject lessonInfo = new JSONObject();
                lessonInfo.put("hoursPerLesson", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getInt("hoursPerLesson"));
                lessonInfo.put("sessionsPerWeek", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getInt("sessionsPerWeek"));

                // making additionalInfo for contract creation
                JSONObject additionalInfo = new JSONObject();
                additionalInfo.put("oneFreeLesson", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getBoolean("oneFreeLesson"));
                additionalInfo.put("bidId", bid.getString("id"));
                additionalInfo.put("tutorQualifications", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getJSONArray("tutorQualifications"));
                additionalInfo.put("contractDuration", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers").getInt("contractDuration"));
                additionalInfo.put("offer", false);
                additionalInfo.put("studentSeen", false);
                additionalInfo.put("tutorSeen", false);
                additionalInfo.put("minimumCompetency", bid.getJSONObject("additionalInfo").getInt("minimumCompetency"));


                // make a contract between student and tutor whose offer was selected
                makeContract(offerMessage.getJSONObject("poster").getString("id"), student.getId(), bid.getJSONObject("subject").getString("id"), paymentInfo, lessonInfo, additionalInfo, student.getId(), LocalDateTime.now());

                // add message into this bid under the bid's poster to signify that the bid is closed
                makeMessage(bid.getString("id"), poster.getString("id"), "bid closed", new JSONObject());

                // closing down the bid
                String bidId = bid.getString("id");
                closeDownBid(LocalDateTime.now().toString(), bidId);

                // going through validBids and find bought out bid to remove
                int i = 0;
                Boolean found = false;
                while (!found && i < validBids.length()) {
                    if (validBids.getJSONObject(i).getString("id").equals(bidId)) {
                        validBids.remove(i);
                        found = true;
                    }
                    i++;
                }

                // refresh page
                new ViewOwnBidsController(validBids, student, view).changeView();
            }
        });

        return button;
    }

    class BackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (bid.getJSONObject("initiator").getString("id").equals(user.getId())) {
                new ViewOwnBidsController(validBids, user, view).changeView();
            } else {
                new ViewBidsController(validBids, user, view).changeView();
            }
        }
    }
}
