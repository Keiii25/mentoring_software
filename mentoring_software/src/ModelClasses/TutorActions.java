package ModelClasses;

import ControllerClasses.TutorDashboardController;
import ControllerClasses.ViewBidsController;
import ControllerClasses.ViewContractController;
import ControllerClasses.ViewOfferContractsController;
import ViewClasses.View;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public interface TutorActions {
    default ArrayList<Action> getTutorActions(View view, JSONArray validBids, JSONArray jobContracts, JSONArray offerContracts, User tutor) {
        ArrayList<Action> tutorActions = new ArrayList<>();
        tutorActions.add(new Action("View Bids Available", new ViewBidsController(validBids, tutor, view)));
        tutorActions.add(new Action("View Job Contracts", new ViewContractController(jobContracts, false, tutor, view)));
        tutorActions.add(new Action("View Dashboard", new TutorDashboardController(view, tutor, validBids)));

        if (offerContracts.length() > 0) {
            tutorActions.add(new Action("View Offer Contracts", new ViewOfferContractsController(view, offerContracts, tutor)));
        }

        return tutorActions;
    }

    // method to get contracts of user is apart of
    default JSONObject getJobContractsAndItsBidIds(JSONArray contracts, String tutorId){
        // initialise returnObject, jobContracts and jobContractBids
        JSONObject returnObject = new JSONObject();
        JSONArray jobContracts = new JSONArray();
        JSONArray jobContractBids = new JSONArray();
        JSONArray aboutToExpireContracts = new JSONArray();
        JSONArray offerContracts = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        // for loop to go through all contracts
        for (int i = 0; i < contracts.length(); i++) {
            // get current contract according to index i
            JSONObject currentContract = contracts.getJSONObject(i);

            // check if user is the tutor in the contract. If so, add that contract to jobContracts and its associated bid's id to jobContractBids
            if (tutorId.equals(currentContract.getJSONObject("firstParty").getString("id"))) {
                // getting expiry date of contract
                LocalDateTime contractExpiryDate = LocalDateTime.parse(currentContract.getString("expiryDate"), formatter);

                //check if the contract has expired or not.
                if (LocalDateTime.now().isBefore(contractExpiryDate)) {
                    if (currentContract.getJSONObject("additionalInfo").getBoolean("offer")) {
                        offerContracts.put(currentContract);
                    } else {
                        // check if any contracts will expire in the next month to add to aboutToExpireContracts if its not seen yet
                        if ((contractExpiryDate.getMonthValue() - LocalDateTime.now().getMonthValue() <= 1) && !currentContract.getJSONObject("additionalInfo").getBoolean("tutorSeen")) {
                            aboutToExpireContracts.put(currentContract);
                        }
                    }

                    jobContracts.put(currentContract);
                }

                // adding the contract's associated bid's id
                jobContractBids.put(currentContract.getJSONObject("additionalInfo").getString("bidId"));
            }
        }

        // adding jobContract and jobContractBids to returnObject
        returnObject.put("jobContracts", jobContracts);
        returnObject.put("jobContractBids", jobContractBids);
        returnObject.put("offerContracts", offerContracts);
        returnObject.put("aboutToExpireContracts", aboutToExpireContracts);

        return returnObject;
    }

    // function to get available bids that the tutor can make offers to
    default JSONArray getAvailableBids(ArrayList<String> jobContractBids, JSONArray bids, JSONArray tutorCompetencies, String tutorId) {
        // getting bids and initialising available bids and formatter
        JSONArray availableBids = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        // for loop to go through all bids
        for (int i = 0; i < bids.length(); i++) {
            // getting current bid according to index i and its messages
            JSONObject currentBid = bids.getJSONObject(i);
            JSONArray messages = currentBid.getJSONArray("messages");

            // checking if currentBid is not a job contract bid
            if (!jobContractBids.contains(currentBid.getString("id"))) {
                // if currentBid is not closed down
                if (currentBid.get("dateClosedDown").equals(null) && !currentBid.getJSONObject("initiator").getString("id").equals(tutorId)) {
                    // getting the time when the bid was created and initialising it's deadline
                    LocalDateTime bidTimeOfCreation = LocalDateTime.parse(currentBid.getString("dateCreated"), formatter);
                    LocalDateTime bidDeadline = null;

                    // checking whether the bid is an open or closed bid to determine it's deadline
                    if (currentBid.getString("type").equals("open")) {
                        bidDeadline = bidTimeOfCreation.plusMinutes(30);
                    } else {
                        bidDeadline = bidTimeOfCreation.plusWeeks(1);
                    }
                    // put dateClosedDown into currentBid
                    currentBid.put("dateClosedDown", bidDeadline.toString());

                    // checking if deadline has not been reached yet
                    if (LocalDateTime.now().isBefore(bidDeadline)) {
                        // check if tutor teaches the subject the bid request for and have the appropriate competency level
                        // extract currentBid's subject's id
                        String currentBidSubjectId = currentBid.getJSONObject("subject").getString("id");
                        int tutorCompetencyLevel = -1;

                        // using a while loop to find if tutor have any competency in the bid's chosen subject
                        int j = 0;
                        Boolean found = false;
                        while (!found && j < tutorCompetencies.length()) {
                            if (tutorCompetencies.getJSONObject(j).getJSONObject("subject").getString("id").equals(currentBidSubjectId)) {
                                tutorCompetencyLevel = tutorCompetencies.getJSONObject(j).getInt("level");
                            }
                            j++;
                        }

                        // check if tutor is competent in the bid's chosen subject
                        if (tutorCompetencyLevel > -1) {
                            int bidMinCompetencyLevel = currentBid.getJSONObject("additionalInfo").getInt("minimumCompetency");
                            if (tutorCompetencyLevel > bidMinCompetencyLevel) {
                                // Add that bid to available bids
                                availableBids.put(currentBid);
                            }
                        }
                    }
                }
            }
        }

        return availableBids;
    }
}
