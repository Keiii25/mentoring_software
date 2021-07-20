package ModelClasses;

import ViewClasses.View;
import WebService.GetInterfaces.GetBid;
import WebService.GetInterfaces.GetContracts;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.CloseDownBid;
import WebService.PostInterfaces.MakeContract;
import WebService.PostInterfaces.MakeMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Student extends User implements StudentActions, GetContracts, GetUser, GetBid, CloseDownBid, MakeMessage, MakeContract {

    public Student(String givenName, String familyName, String id, String userName, JSONArray competencies, JSONArray qualifications, View view){
        super(givenName, familyName, id, userName, true, false, competencies, qualifications, view);

        updateActionUI();
    }

    @Override
    public void updateActionUI() {
        // getting a JSONArray of user's contracts and the bid ids that are associated with them
        JSONObject myContractsAndTheirBidIds = getMyContractsAndTheirBidIds(getContracts(), super.getId());
        JSONArray myContracts = myContractsAndTheirBidIds.getJSONArray("myContracts");
        JSONArray expiredContracts = myContractsAndTheirBidIds.getJSONArray("expiredContracts");
        aboutToExpireContracts = myContractsAndTheirBidIds.getJSONArray("aboutToExpireContracts");
        JSONArray myContractBidIdsJSONArray = myContractsAndTheirBidIds.getJSONArray("myContractBidIds");

        // converting myContractBidIdsJSONArray into a ArrayList
        ArrayList<String> myContractBidIds = new ArrayList<>();
        for (int i = 0; i < myContractBidIdsJSONArray.length(); i++) {
            myContractBidIds.add(myContractBidIdsJSONArray.getString(i));
        }

        // getting my active bids and unsettled bids
        JSONArray myBidsNoMessage = getUser(super.getId(), 5).getJSONArray("initiatedBids");
        JSONArray myInitiatedBids = new JSONArray();
        for (int i = 0; i < myBidsNoMessage.length(); i++) {
            myInitiatedBids.put(getBid(myBidsNoMessage.getJSONObject(i).getString("id"), true));
        }

        JSONObject myActiveAndUnsettledBids = getMyActiveAndUnsettledBids(myContractBidIds, myInitiatedBids, super.getId());
        JSONArray myActiveBids = myActiveAndUnsettledBids.getJSONArray("myActiveBids");
        JSONArray unsettledBids = myActiveAndUnsettledBids.getJSONArray("unsettledBids");

        // closed down all unsettled bids if they are not already closed down
        JSONArray unsettledBidsUpdated = new JSONArray();

        // for loop to go through all unsettled bids
        for (int i = 0; i < unsettledBids.length(); i++) {
            JSONObject currentBid = unsettledBids.getJSONObject(i);

            // if current bid is not closed down yet, close it down. else add it to unsettledBidsUpdated
            if (currentBid.get("dateClosedDown").equals(null)) {
                closeDownBid(LocalDateTime.now().toString(), currentBid.getString("id"));
                unsettledBidsUpdated.put(getBid(currentBid.getString("id"), true));
            } else {
                unsettledBidsUpdated.put(currentBid);
            }
        }

        // for loop to go through all unsettled bids and make a contract with those of which that are open bids with offers
        for (int i = 0; i < unsettledBidsUpdated.length(); i++) {
            JSONObject currentExpiredBid = unsettledBidsUpdated.getJSONObject(i);

            // checks if the bid is open of close bid
            if (currentExpiredBid.getString("type").equals("open")) {
                // get it's messages
                JSONArray messages = currentExpiredBid.getJSONArray("messages");

                // check if there are any offers
                if (messages.length() > 0) {
                    // get last offer
                    JSONObject lastOffer = messages.getJSONObject(messages.length()-1);

                    // making paymentInfo for contract creation
                    JSONObject paymentInfo = new JSONObject();
                    paymentInfo.put("rate", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getInt("rate"));
                    paymentInfo.put("rateType", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getString("rateType"));

                    // making lessonInfo for contract creation
                    JSONObject lessonInfo = new JSONObject();
                    lessonInfo.put("hoursPerLesson", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getInt("hoursPerLesson"));
                    lessonInfo.put("sessionsPerWeek", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getInt("sessionsPerWeek"));

                    // making additionalInfo for contract creation
                    JSONObject additionalInfoContract = new JSONObject();
                    additionalInfoContract.put("oneFreeLesson", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getBoolean("oneFreeLesson"));
                    additionalInfoContract.put("bidId", currentExpiredBid.getString("id"));
                    additionalInfoContract.put("tutorQualifications", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getJSONArray("tutorQualifications"));
                    additionalInfoContract.put("contractDuration", lastOffer.getJSONObject("additionalInfo").getJSONObject("offers").getInt("contractDuration"));
                    additionalInfoContract.put("offer", false);
                    additionalInfoContract.put("studentSeen", false);
                    additionalInfoContract.put("tutorSeen", false);
                    additionalInfoContract.put("minimumCompetency", currentExpiredBid.getJSONObject("additionalInfo").getInt("minimumCompetency"));

                    // make a contract between student and tutor of last offer
                    makeContract(lastOffer.getJSONObject("poster").getString("id"), super.getId(), currentExpiredBid.getJSONObject("subject").getString("id"), paymentInfo, lessonInfo, additionalInfoContract, super.getId(), LocalDateTime.now());

                    // add message into this bid under the bid's poster to signify that the bid is closed
                    makeMessage(currentExpiredBid.getString("id"), super.getId(), "bid closed", new JSONObject());
                } else { // else close the bid
                    // add message into this bid under the bid's poster's id to signify that the bid is closed
                    makeMessage(currentExpiredBid.getString("id"), super.getId(), "bid closed", new JSONObject());
                }
            } else { //close the bid
                // add message into this bid under the bid's poster's id to signify that the bid is closed
                makeMessage(currentExpiredBid.getString("id"), super.getId(), "bid closed", new JSONObject());
            }
        }

        // getting student actions again
        ArrayList<Action> studentActions = getStudentActions(view, this, myContracts, myActiveBids, expiredContracts);

        // getting default user actions and adding them to student's actions
        ArrayList<Action> defaultActions = getDefaultActions();

        for (int i =0; i < defaultActions.size(); i++) {
            studentActions.add(defaultActions.get(i));
        }

        // set actions for user
        super.setActions(studentActions);
    }
}
