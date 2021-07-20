package ControllerClasses;

import ModelClasses.User;
import ViewClasses.TutorDashboardUI;
import ViewClasses.UpdateOfferUI;
import ViewClasses.View;
import WebService.DeleteInterfaces.DeleteMessage;
import WebService.GetInterfaces.GetBid;
import WebService.PostInterfaces.MakeMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateOfferController extends Controller implements DeleteMessage, MakeMessage, GetBid {

    private UpdateOfferUI ui;
    private View view;
    private User user;
    private String bidId;
    private JSONObject oldOffer;
    private JSONArray validBids;

    public UpdateOfferController(View view, User user, String bidId, JSONObject oldOffer, JSONArray validBids) {
        super(view);
        page = new UpdateOfferUI();
        ui = (UpdateOfferUI) page;
        this.view = view;
        this.user = user;
        this.bidId = bidId;
        this.oldOffer = oldOffer;
        this.validBids = validBids;
        this.ui.addBackListener(new BackListener());
        this.ui.addUpdateListener(new UpdateOfferListener());
    }

    class UpdateOfferListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (validate()) {
                // deleting old offer
                deleteMessage(oldOffer.getString("id"));

                // creating a JSONObject that hold details about the updated offer
                JSONObject offer = new JSONObject();
                offer.put("rateType", ui.getRateType());
                offer.put("rate", Integer.parseInt(ui.getRate()));
                offer.put("hoursPerLesson", Integer.parseInt(ui.getDurationLesson()));
                offer.put("sessionsPerWeek", Integer.parseInt(ui.getNumSession()));
                offer.put("oneFreeLesson", ui.getFreeLesson());
                offer.put("tutorQualifications", user.getQualifications());
                offer.put("contractDuration", ui.getContractDuration());

                // creating additionalInfo JSONObject for offer and putting offer JSONObject in it under "offers"
                JSONObject additionalInfo = new JSONObject();
                additionalInfo.put("offers", offer);

                // if the offer that is going to be updated has conversation in it's additional information, bring the conversation over to the updated offer
                if (oldOffer.getJSONObject("additionalInfo").has("conversation")) {
                    additionalInfo.put("conversation", oldOffer.getJSONObject("additionalInfo").getJSONArray("conversation"));
                }

                // put the updated offer into the bid's message section
                makeMessage(bidId, user.getId(), "An offer has been made to your bid " + bidId, additionalInfo);
                // create pop up window to inform user of successful update of offer
                JOptionPane.showMessageDialog(null, "Your offer has been successfully updated");

                // update validBids with the bid with the newly updated offer
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

                if (view.getOldUI() instanceof TutorDashboardUI) {
                    new TutorDashboardController(view, user, validBids).changeView();
                } else {
                    new ViewBidsController(validBids, user, view).changeView();
                }
            }
        }

        // function to validate the input given by user
        private Boolean validate(){
            String rate = ui.getRate();
            String durationLesson = ui.getDurationLesson();
            String numSession = ui.getNumSession();
            // initialise valid
            Boolean valid = false;

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
                new TutorDashboardController(view, user, validBids).changeView();
            } else {
                new ViewBidsController(validBids, user, view).changeView();
            }
        }
    }
}
