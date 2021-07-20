package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewBidsUI;
import WebService.GetInterfaces.GetBid;
import WebService.PatchInterfaces.UpdateBid;
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
import java.time.format.DateTimeFormatter;

public class ViewBidsController extends Controller implements MakeChangeUIButton, MakeContract, MakeMessage, CloseDownBid, UpdateBid, GetBid {
    private ViewBidsUI ui;
    private User tutor;

    public ViewBidsController(JSONArray validBids, User tutor, View view) {
        super(view);
        page = new ViewBidsUI();
        this.ui = (ViewBidsUI) page;
        this.tutor = tutor;
        setUpPage(validBids);
    }

    private void setUpPage(JSONArray validBids) {
        // set up back button to return to Home Page
        JButton backButton = ui.getBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tutor.updateActionUI();
                new HomePageController(view, tutor).changeView();
            }
        });

        // set up content of page
        JScrollPane pageScrollPane = ui.getScrollPane();
        // check if there is any bids to display
        if (validBids.length() > 0) {
            // initialise formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            // remove "no bids available message"
            pageScrollPane.remove(ui.getNoBidMessage());
            pageScrollPane.revalidate();
            pageScrollPane.repaint();

            // create content JPanel to contain display for all validBids and unsettledBids
            JPanel content = new JPanel(new GridLayout(0, 1));

            // check if validBids is not empty
            if (validBids.length() > 0) {
                // for loop to go through all validBids
                for (int i = 0; i < validBids.length(); i++) {
                    // getting currentBid according to index i, its additionalInfo and deadline
                    JSONObject currentBid = validBids.getJSONObject(i);
                    JSONObject additionalInfo = currentBid.getJSONObject("additionalInfo");
                    LocalDateTime bidDeadline = LocalDateTime.parse(currentBid.getString("dateClosedDown") + "Z", formatter);


                    // formatting how the Bid will be displayed
                    String contentString = "<html>Subject: \"" + currentBid.getJSONObject("subject").getString("name") + ": " + currentBid.getJSONObject("subject").getString("description") +  "\"<br/>";
                    contentString += "Student: " + currentBid.getJSONObject("initiator").getString("givenName") + " "  + currentBid.getJSONObject("initiator").getString("familyName") + "<br/>";
                    contentString += "Deadline: " + bidDeadline.getHour() + ":" + bidDeadline.getMinute() + " (" +  bidDeadline.getDayOfMonth() + "/"  + bidDeadline.getMonthValue() + "/" + bidDeadline.getYear() + ")<br/>";
                    contentString += "Minimum Competency: Level " + additionalInfo.getInt("minimumCompetency") + "<br/>";
                    contentString += "Preferred hours per lesson: " + additionalInfo.getInt("hoursPerLesson") + " hours<br/>";
                    contentString += "number of sessions per week: " + additionalInfo.getInt("sessionsPerWeek") + " sessions/week<br/>";
                    contentString += "Rate: $" + additionalInfo.getInt("rate") + " " + additionalInfo.getString("rateType") + "<br/>";
                    contentString += "Bid Type: " + currentBid.getString("type") + "<br/>";
                    contentString += "Contract Duration: " + additionalInfo.getInt("contractDuration") + " months";
                    contentString += "<br/></html>";

                    // making a new JPanel to display this bid
                    JPanel bidDisplay = new JPanel(new GridLayout(0, 1));
                    bidDisplay.add(new JLabel(contentString));

                    // getting messages of currentBid
                    JSONArray messages = currentBid.getJSONArray("messages");

                    // initialise j, found and tutorId for while loop
                    int j = 0;
                    Boolean found = false;
                    JSONObject oldOffer = null;

                    // while loop to go through messages until a message that tutor has made is found
                    while (j < messages.length() && !found) {
                        // if current message is made my tutor, change found to true
                        if (tutor.getId().equals(messages.getJSONObject(j).getJSONObject("poster").getString("id"))) {
                            found = true;
                            oldOffer = messages.getJSONObject(j);
                        }
                        j++;
                    }

                    // adding buttons to each of the Bids to do actions relating to bids
                    JPanel buttonDisplay = new JPanel();
                    // if bid is an open bid, make buttons to allow tutor to make/update offers and view all already made offers only
                    if (currentBid.getString("type").equals("open")) {
                        // if tutor made an offer to this bid before, add an update offer button
                        if (found) {
                            JButton goToUpdateOffer = makeChangeUIButton("Update Offer", new UpdateOfferController(view, tutor, currentBid.getString("id"),  oldOffer, validBids));
                            buttonDisplay.add(goToUpdateOffer);
                        } else { // else add a make offer button
                            JButton goToMakeOffer = makeChangeUIButton("Make Offer", new MakeOfferController(tutor, view, currentBid.getString("id"), validBids, false));
                            buttonDisplay.add(goToMakeOffer);
                        }
                        // add view offers button
                        JButton goToViewOffers = makeChangeUIButton("View Offers", new ViewOfferController(tutor, view, currentBid, validBids));
                        buttonDisplay.add(goToViewOffers);
                        // add buyout button
                        JButton buyoutButton = getBuyoutButton(currentBid, view, validBids, tutor);
                        buttonDisplay.add(buyoutButton);

                        Boolean sub = false;
                        if (currentBid.getJSONObject("additionalInfo").has("subscribers")) {
                            JSONArray subs = currentBid.getJSONObject("additionalInfo").getJSONArray("subscribers");
                            for (int k = 0; k < subs.length(); k++) {
                                if (subs.getJSONObject(k).getString("id").equals(tutor.getId())) {
                                    sub = true;
                                    break;
                                }
                            }

                        } else {
                            sub = false;
                        }
                        if (!sub) {
                            JButton subscribeButton = new JButton("Subscribe");
                            subscribeButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JSONObject bid = getBid(currentBid.getString("id"), false);
                                    JSONArray subscribers = new JSONArray();
                                    if (bid.getJSONObject("additionalInfo").has("subscribers")) {
                                        subscribers = bid.getJSONObject("additionalInfo").getJSONArray("subscribers");
                                    }
                                    JSONObject userInfo = new JSONObject();
                                    userInfo.put("id", tutor.getId());
                                    subscribers.put(userInfo);
                                    //JSONObject additionalInfo = new JSONObject();
                                    //additionalInfo.put("subscribers", subscribers);
                                    JSONObject bidsInfo = bid.getJSONObject("additionalInfo");
                                    bidsInfo.put("subscribers", subscribers);
                                    updateBid(currentBid.getString("id"), bidsInfo);
                                }
                            });


                            buttonDisplay.add(subscribeButton);
                        }


                    } else if (currentBid.get("type").equals("close")) { // if bid is a closed bid, allow tutors to only make/update offers only
                        // if tutor made an offer to this bid before, add an update offer button
                        if (found) {
                            JButton goToUpdateOffer = makeChangeUIButton("Update Offer", new UpdateOfferController(view, tutor, currentBid.getString("id"),  oldOffer, validBids));
                            buttonDisplay.add(goToUpdateOffer);

                            // add view offer button to see tutor's own offer only
                            JButton goToViewOffers = makeChangeUIButton("View My Offer", new ViewOfferController(tutor, view, currentBid, validBids));
                            buttonDisplay.add(goToViewOffers);
                        } else { // else add a make offer button
                            JButton goToMakeOffer = makeChangeUIButton("Make Offer", new MakeOfferController(tutor, view, currentBid.getString("id"), validBids, true));
                            buttonDisplay.add(goToMakeOffer);
                        }
                    }

                    // add buttonDisplay to bid display then adding bidDisplay content
                    bidDisplay.add(buttonDisplay);
                    content.add(bidDisplay);
                }
            }

            // adding content to be displayed in scrollPane
            pageScrollPane.getViewport().add(content);
        }
    }

    private JButton getBuyoutButton(JSONObject bid, View view, JSONArray validBids, User tutor){
        JButton button = new JButton("Buyout");
        JSONObject poster = bid.getJSONObject("initiator");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // making paymentInfo for contract creation
                JSONObject paymentInfo = new JSONObject();
                paymentInfo.put("rate", bid.getJSONObject("additionalInfo").getInt("rate"));
                paymentInfo.put("rateType", bid.getJSONObject("additionalInfo").getString("rateType"));

                // making lessonInfo for contract creation
                JSONObject lessonInfo = new JSONObject();
                lessonInfo.put("hoursPerLesson", bid.getJSONObject("additionalInfo").getInt("hoursPerLesson"));
                lessonInfo.put("sessionsPerWeek", bid.getJSONObject("additionalInfo").getInt("sessionsPerWeek"));

                // making additionalInfo for contract creation
                JSONObject additionalInfo = new JSONObject();

                // pop up window to ask if tutor wants to include a free lesson
                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to include one free lesson for the student?",
                        "Include One Free Lesson",
                        JOptionPane.YES_NO_OPTION);

                // adding free lesson details into additionalInfo
                if (n == 0) {
                    additionalInfo.put("oneFreeLesson", true);
                } else {
                    additionalInfo.put("oneFreeLesson", false);
                }

                // adding bid ID, tutor's qualifications and contract duration into additionalInfo
                additionalInfo.put("bidId", bid.getString("id"));
                additionalInfo.put("tutorQualifications", tutor.getQualifications());
                additionalInfo.put("contractDuration", bid.getJSONObject("additionalInfo").getInt("contractDuration"));
                additionalInfo.put("offer", false);
                additionalInfo.put("studentSeen", false);
                additionalInfo.put("tutorSeen", false);
                additionalInfo.put("minimumCompetency", bid.getJSONObject("additionalInfo").getInt("minimumCompetency"));


                // make a contract between student and tutor whose offer was selected
                makeContract(tutor.getId(), bid.getJSONObject("initiator").getString("id"), bid.getJSONObject("subject").getString("id"), paymentInfo, lessonInfo, additionalInfo, tutor.getId(), LocalDateTime.now());

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
                new ViewBidsController(validBids, tutor, view).changeView();
            }
        });

        return button;
    }
}
