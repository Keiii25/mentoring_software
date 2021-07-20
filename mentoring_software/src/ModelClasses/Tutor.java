package ModelClasses;

import ViewClasses.View;
import WebService.GetInterfaces.GetBid;
import WebService.GetInterfaces.GetBids;
import WebService.GetInterfaces.GetContracts;
import WebService.PostInterfaces.CloseDownBid;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tutor extends User implements TutorActions, GetBid, GetBids, GetContracts, CloseDownBid {

    public Tutor(String givenName, String familyName, String id, String userName, JSONArray competencies, JSONArray qualifications, View view) {
        super(givenName, familyName, id, userName, false, true, competencies, qualifications, view);

        updateActionUI();
    }

    @Override
    public void updateActionUI() {
        // getting a JSONArray of user's job contracts and it's associated bid ids
        JSONObject jobContractsAndItsBidIds = getJobContractsAndItsBidIds(getContracts(), super.getId());
        JSONArray jobContracts = jobContractsAndItsBidIds.getJSONArray("jobContracts");
        JSONArray offerContracts = jobContractsAndItsBidIds.getJSONArray("offerContracts");
        JSONArray jobContractBidsJSONArray = jobContractsAndItsBidIds.getJSONArray("jobContractBids");
        aboutToExpireContracts = jobContractsAndItsBidIds.getJSONArray("aboutToExpireContracts");

        // converting jobContractBidsJSONArray into an arraylist
        ArrayList<String> jobContractBids = new ArrayList<>();
        for (int i = 0; i < jobContractBidsJSONArray.length(); i++) {
            jobContractBids.add(jobContractBidsJSONArray.getString(i));
        }

        //get available bids
        JSONArray availableBids = getAvailableBids(jobContractBids, getBids(true), super.getCompetencies(), getId());

        // getting tutor's actions
        ArrayList<Action> tutorActions = getTutorActions(view, availableBids, jobContracts, offerContracts, this);

        // getting default user actions and adding them to tutor's actions
        ArrayList<Action> defaultActions = getDefaultActions();

        for (int i =0; i < defaultActions.size(); i++) {
            tutorActions.add(defaultActions.get(i));
        }

        // setting user's action as tutor's action
        super.setActions(tutorActions);
    }
}
