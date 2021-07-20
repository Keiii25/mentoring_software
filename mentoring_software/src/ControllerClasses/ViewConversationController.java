package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewConversationUI;
import WebService.DeleteInterfaces.DeleteMessage;
import WebService.GetInterfaces.GetBid;
import WebService.PostInterfaces.MakeMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewConversationController extends Controller implements MakeMessage, DeleteMessage, GetBid {

    private ViewConversationUI ui;
    private User user;
    private JSONObject offerMessage;
    private JSONObject bid;
    private JSONArray validBids;

    public ViewConversationController (User user, View view, JSONObject offerMessage, JSONObject bid, JSONArray validBids) {
        super(view);
        page = new ViewConversationUI();

        // initialise class attributes
        ui = (ViewConversationUI) page;
        this.user = user;
        this.offerMessage = offerMessage;
        this.bid = bid;
        this.validBids = validBids;

        // adding function to send button and back button
        ui.addSendMessageListener(new SendMessageListener());
        ui.addBackButtonListener(new BackButtonListener());

        // set up page's content
        setUpPage();
    }

    private void setUpPage() {
        JScrollPane pageScrollPane = ui.getScrollPane();
        JLabel nameLabel = ui.getNameLabel();
        JLabel rateType = ui.getRateType();
        JLabel hoursLesson = ui.getHoursLesson();
        JLabel rate = ui.getRate();
        JLabel freeLesson = ui.getFreeLesson();
        JLabel sessionWeek = ui.getSessionWeek();
        JLabel contractDuration = ui.getContractDuration();

        JSONArray messageLog = offerMessage.getJSONObject("additionalInfo").getJSONArray("conversation");
        JSONObject offerDetail = offerMessage.getJSONObject("additionalInfo").getJSONObject("offers");

        if (user.getId().equals(bid.getJSONObject("initiator").getString("id"))) {
            nameLabel.setText("Talking to: " + offerMessage.getJSONObject("poster").getString("givenName") + " " + offerMessage.getJSONObject("poster").getString("familyName"));
        } else {
            nameLabel.setText("Talking to: " + bid.getJSONObject("initiator").getString("givenName") + " " +bid.getJSONObject("initiator").getString("familyName") );
        }

        // display offer information on top half of screen
        rateType.setText(offerDetail.getString("rateType"));
        hoursLesson.setText(String.valueOf(offerDetail.getInt("hoursPerLesson")));
        rate.setText("$" + offerDetail.getInt("rate"));
        sessionWeek.setText(String.valueOf(offerDetail.getInt("sessionsPerWeek")));
        contractDuration.setText(String.valueOf(offerDetail.getInt("contractDuration")));

        if (offerDetail.getBoolean("oneFreeLesson")) {
            freeLesson.setText("Yes");
        } else {
            freeLesson.setText("No");
        }

        // initialising JPanel to hold conversation log
        JPanel conversation = new JPanel(new GridLayout(0, 1));
        String conversationString = "<html>";

        for (int i = 0; i < messageLog.length(); i++) {
            JSONObject currentMessage = messageLog.getJSONObject(i);
            conversationString += currentMessage.getString("sender") + ": " + currentMessage.getString("content") + "<br/>";
        }

        conversationString += "</html>";

        conversation.add(new JLabel(conversationString));
        pageScrollPane.getViewport().add(conversation);
    }

    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new ViewOfferController(user, view, bid, validBids).changeView();
        }
    }

    class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = ui.getMessage();
            if (!message.isEmpty()) {
                JSONObject newMessage = new JSONObject();
                newMessage.put("sender", user.getGivenName() + " " + user.getFamilyName());
                newMessage.put("content", message);

                JSONObject currentVersionOfBid = getBid(bid.getString("id"), true);
                JSONArray currentVersionMessages = currentVersionOfBid.getJSONArray("messages");
                JSONObject currentVersionOffer = null;

                // find current version of this offer
                for (int i = 0; i < currentVersionMessages.length(); i++) {
                    if (currentVersionMessages.getJSONObject(i).getJSONObject("poster").getString("id").equals(offerMessage.getJSONObject("poster").getString("id"))) {
                        currentVersionOffer = currentVersionMessages.getJSONObject(i);
                    }
                }

                JSONArray currentVersionConversation = currentVersionOffer.getJSONObject("additionalInfo").getJSONArray("conversation");
                currentVersionConversation.put(newMessage);

                // update offerMessage's additionalInfo
                JSONObject additionalInfo = new JSONObject();
                additionalInfo.put("offers", offerMessage.getJSONObject("additionalInfo").getJSONObject("offers"));
                additionalInfo.put("conversation", currentVersionConversation);

                // delete old offer message with current old conversation and make a new offer message with updated conversation
                deleteMessage(currentVersionOffer.getString("id"));
                makeMessage(bid.getString("id"), offerMessage.getJSONObject("poster").getString("id"), "updated offer's conversation", additionalInfo);

                // get current version of bid with offer with updated conversation
                currentVersionOfBid = getBid(bid.getString("id"), true);
                currentVersionMessages = currentVersionOfBid.getJSONArray("messages");
                currentVersionOffer = null;

                // find current version of this offer
                for (int i = 0; i < currentVersionMessages.length(); i++) {
                    if (currentVersionMessages.getJSONObject(i).getJSONObject("poster").getString("id").equals(offerMessage.getJSONObject("poster").getString("id"))) {
                        currentVersionOffer = currentVersionMessages.getJSONObject(i);
                    }
                }

                // refresh page
                new ViewConversationController(user, view, currentVersionOffer, currentVersionOfBid, validBids).changeView();
            }
        }
    }
}
