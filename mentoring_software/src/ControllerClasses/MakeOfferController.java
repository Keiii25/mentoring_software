package ControllerClasses;

import ModelClasses.User;
import ViewClasses.MakeOfferUI;
import ViewClasses.TutorDashboardUI;
import ViewClasses.View;
import WebService.GetInterfaces.GetBid;
import WebService.PostInterfaces.MakeMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MakeOfferController extends Controller implements MakeMessage, GetBid {
    private MakeOfferUI ui;
    private User tutor;
    private View view;
    private String bidId;
    private JSONArray validBids;
    private Boolean isPrivate;

    public MakeOfferController(User tutor, View view, String bidId, JSONArray validBids, Boolean isPrivate) {
        super(view);
        page = new MakeOfferUI();
        this.ui = (MakeOfferUI) page;
        this.tutor = tutor;
        this.view = view;
        this.bidId = bidId;
        this.validBids = validBids;
        this.isPrivate = isPrivate;
        this.ui.addMakeOfferListener(new MakeOfferListener());
        this.ui.addBackListener(new BackListener());
    }

    class MakeOfferListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // if inputs are valid
            if (validate()) {
                // offer elements
                String rateType = ui.getRateType();
                String rate = ui.getRate();
                String durationLesson = ui.getDurationLesson();
                String numSession = ui.getNumSession();
                int contractDuration = ui.getContractDuration();
                Boolean freeLesson = ui.getFreeLesson();
                // create a JSONObject offer which will hold the information of the tutor's offer
                JSONObject offer = new JSONObject();
                offer.put("rateType", rateType);
                offer.put("rate", Integer.parseInt(rate));
                offer.put("hoursPerLesson", Integer.parseInt(durationLesson));
                offer.put("sessionsPerWeek", Integer.parseInt(numSession));
                offer.put("contractDuration", contractDuration);
                offer.put("oneFreeLesson", freeLesson);
                offer.put("tutorQualifications", tutor.getQualifications());

                // create additionalInfo JSONObject for message creation
                JSONObject additionalInfo = new JSONObject();
                additionalInfo.put("offers", offer);

                // if the Bid is a closed bid, add a conversation JSONArray into additionalInfo
                if (isPrivate) {
                    additionalInfo.put("conversation", new JSONArray());
                }

                // post message into the bid and create a pop up window informing user about offer creation
                makeMessage(bidId, tutor.getId(), "An offer has been made to your bid " + bidId, additionalInfo);
                JOptionPane.showMessageDialog(null, "An offer has been successfully made");

                // update validBids with the bid with the newly created offer
                JSONObject updateBid = getBid(bidId, true);

                // initialise updatedValidBids
                JSONArray updatedValidBids = new JSONArray();

                // for loop to go through valid bids
                for (int i = 0; i < validBids.length(); i++) {
                    // checking is current bid at index i has the same bidId as the bid that just got the new offer
                    if (validBids.getJSONObject(i).getString("id").equals(bidId)) {
                        // get the bid's dateClosedDown and put it into updateBid. Then put updateBid into validBidCopy
                        String dateClosedDown = validBids.getJSONObject(i).getString("dateClosedDown");
                        updateBid.put("dateClosedDown", dateClosedDown);
                        updatedValidBids.put(updateBid);
                    } else {
                        // else add that bid into validBidCopy
                        updatedValidBids.put(validBids.getJSONObject(i));
                    }
                }

                // go back to the last page
                if (view.getOldUI() instanceof TutorDashboardUI) {
                    new TutorDashboardController(view, tutor, validBids).changeView();
                } else {
                    new ViewBidsController(validBids, tutor, view).changeView();
                }
            }
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

    class BackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (view.getOldUI() instanceof TutorDashboardUI) {
                new TutorDashboardController(view, tutor, validBids).changeView();
            } else {
                new ViewBidsController(validBids, tutor, view).changeView();
            }
        }
    }
}
