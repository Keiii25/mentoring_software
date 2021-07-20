package ModelClasses;

import ControllerClasses.LoginController;
import ViewClasses.View;
import WebService.GetInterfaces.GetUser;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Abstract class to be inherited by different type of users (ModelClasses.Student, ModelClasses.StudentTutor and ModelClasses.Tutor)
 */

public abstract class User implements GetUser {
    private String givenName;
    private String familyName;
    private String id;
    private String userName;
    private boolean isStudent;
    private boolean isTutor;
    protected View view;
    private ArrayList<Action> actions = new ArrayList<>();
    private ArrayList<Action> defaultActions = new ArrayList<>();
    private JSONArray competencies;
    private JSONArray qualifications;
    protected JSONArray aboutToExpireContracts;

    public User(String givenName, String familyName, String id, String userName, Boolean isStudent, Boolean isTutor, JSONArray competencies, JSONArray qualifications, View view) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.id = id;
        this.userName = userName;
        this.isStudent = isStudent;
        this.isTutor = isTutor;
        this.competencies = competencies;
        this.qualifications = qualifications;
        this.view = view;

        defaultActions.add(new Action("Log Out", new LoginController(view)));
    }

    // methods common to all user classes
    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public boolean isTutor() {
        return isTutor;
    }

    public ArrayList<Action> getActions() {return (ArrayList<Action>) actions.clone();}

    public void setActions(ArrayList<Action> newActions) { this.actions = newActions;}

    public JSONArray getCompetencies(){
        JSONArray competenciesCopy = new JSONArray();

        for (int i = 0; i < competencies.length(); i++) {
            competenciesCopy.put(competencies.getJSONObject(i));
        }

        return competenciesCopy;
    }

    public JSONArray getQualifications() {
        JSONArray qualificationsCopy = new JSONArray();

        for (int i = 0; i < qualifications.length(); i++) {
            qualificationsCopy.put(qualifications.getJSONObject(i));
        }

        return qualificationsCopy;
    }

    public JSONArray getAboutToExpireContracts() {
        JSONArray aboutToExpireContractsCopy = new JSONArray();

        for (int i = 0; i < aboutToExpireContracts.length(); i++) {
            aboutToExpireContractsCopy.put(aboutToExpireContracts.getJSONObject(i));
        }

        return aboutToExpireContractsCopy;
    }

    public ArrayList<Action> getDefaultActions() {
        return (ArrayList<Action>) defaultActions.clone();
    }

    public abstract void updateActionUI();
}
