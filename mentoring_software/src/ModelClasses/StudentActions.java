package ModelClasses;

import ControllerClasses.MakeBidController;
import ControllerClasses.ViewContractController;
import ControllerClasses.ViewExpiredContractsController;
import ControllerClasses.ViewOwnBidsController;
import ViewClasses.MakeBidUI;
import ViewClasses.View;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public interface StudentActions {
    /**
     * A getter of the list of executable actions by the ModelClasses.Student
     * @param student
     * @param myContracts existing contracts signed by the student
     * @param myActiveBids the student's active bids
     * @return
     */
    default ArrayList<Action> getStudentActions(View view, User student, JSONArray myContracts, JSONArray myActiveBids, JSONArray expiredContracts) {
        ArrayList<Action> studentActions = new ArrayList<>();

        // Allow student to make more bids if number of contracts and active bids are less than 5
        if (myActiveBids.length() + myContracts.length() < 5) {
            MakeBidUI makeBidUI = new MakeBidUI();
            studentActions.add(new Action("Make a Bid", new MakeBidController(student, view)));
        }

        // Add actions for student to view bids that they created and contracts that they have established
        studentActions.add(new Action("View My Bids", new ViewOwnBidsController(myActiveBids, student, view)));
        studentActions.add(new Action("View My Contracts", new ViewContractController(myContracts, true, student, view)));

        // Add action for students to view expired contracts
        if (expiredContracts.length() > 0) {
            studentActions.add(new Action("View Expired Contracts", new ViewExpiredContractsController(view, expiredContracts, student)));
        }

        return studentActions;
    }

    // method to get contracts made by users and the bid ids those contracts are associated with
    default JSONObject getMyContractsAndTheirBidIds(JSONArray contracts, String studentId){
        // initialise returnObject, myContracts and myContractBidIds
        JSONObject returnObject = new JSONObject();
        JSONArray myContracts = new JSONArray();
        JSONArray myContractBidIds = new JSONArray();
        JSONArray expiredContracts = new JSONArray();
        JSONArray aboutToExpireContracts = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        // for loop to go through all contracts
        for (int i = 0; i < contracts.length(); i++) {
            // get current contract according to index i
            JSONObject currentContract = contracts.getJSONObject(i);

            // check if user is the student in the contract. If so, add that contract to myContracts and its associated bid's id to myContractBids
            if (studentId.equals(currentContract.getJSONObject("secondParty").getString("id"))) {
                // getting expiry date of contract
                LocalDateTime contractExpiryDate = LocalDateTime.parse(currentContract.getString("expiryDate"), formatter);

                //check if the contract has expired or not.
                if (LocalDateTime.now().isBefore(contractExpiryDate)) {
                    // check if the contract is and offer contract
                    if (!currentContract.getJSONObject("additionalInfo").getBoolean("offer")) {
                        // check if any contracts will expire in the next month to add to aboutToExpireContracts if its not seen yet
                        if ((contractExpiryDate.getYear() == LocalDateTime.now().getYear()) && (contractExpiryDate.getMonthValue() - LocalDateTime.now().getMonthValue() <= 1) && !currentContract.getJSONObject("additionalInfo").getBoolean("studentSeen")) {
                            aboutToExpireContracts.put(currentContract);
                        }
                    }

                    myContracts.put(currentContract);
                } else {
                    expiredContracts.put(currentContract);
                }

                // adding the contract's bid ID into myContractBidIds
                myContractBidIds.put(currentContract.getJSONObject("additionalInfo").getString("bidId"));
            }
        }

        returnObject.put("myContracts", myContracts);
        returnObject.put("expiredContracts", expiredContracts);
        returnObject.put("aboutToExpireContracts", aboutToExpireContracts);
        returnObject.put("myContractBidIds", myContractBidIds);

        return returnObject;
    }

    // method to get the active bids of user and bids that have not been settled yet
    default JSONObject getMyActiveAndUnsettledBids(ArrayList<String> myContractBidIds, JSONArray bids, String studentId){
        // initialising returnObject, myActiveBids, justClosedDownBids and formatter
        JSONObject returnObject = new JSONObject();
        JSONArray myActiveBids = new JSONArray();
        JSONArray unsettledBids = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        // for loop to go through all bids
        for (int i = 0; i < bids.length(); i++) {
            // getting currentBid according to index i
            JSONObject currentBid = bids.getJSONObject(i);

            // check if bid is not already closed down
            if (currentBid.get("dateClosedDown").equals(null)) {
                // closing bids that are are expired
                // getting time of when this bid was created and initialising its deadline
                LocalDateTime bidTimeOfCreation = LocalDateTime.parse(currentBid.getString("dateCreated"), formatter);
                LocalDateTime bidDeadline = null;

                // checking whether the bid is an open or closed bid to determine it's deadline
                if (currentBid.getString("type").equals("open")) {
                    bidDeadline = bidTimeOfCreation.plusMinutes(30);
                } else {
                    bidDeadline = bidTimeOfCreation.plusWeeks(1);
                }

                // if the bid deadline is not yet passed, add it to myActiveBids. else consider those bids unsettled
                if (LocalDateTime.now().isBefore(bidDeadline)) {
                    currentBid.put("dateClosedDown", bidDeadline.toString());
                    myActiveBids.put(currentBid);
                } else {
                    // bids that have just expired
                    unsettledBids.put(currentBid);
                }
            } else {
                // if closed bid is not in contract, considered it not settled
                if (!myContractBidIds.contains(currentBid.getString("id"))) {
                    // getting messages of the bid
                    JSONArray messages = currentBid.getJSONArray("messages");
                    // checking if the latest message was not the closing message by the bid's poster
                    if (!studentId.equals(messages.getJSONObject(0).getJSONObject("poster").getString("id"))) {
                        unsettledBids.put(currentBid);
                    }
                }
            }
        }

        returnObject.put("myActiveBids", myActiveBids);
        returnObject.put("unsettledBids", unsettledBids);

        return returnObject;
    }
}
