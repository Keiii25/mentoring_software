package ModelClasses;

import ControllerClasses.Controller;

public class Action {
    private String actionName;
    private Controller controller;

    /**
     * Constructor for an action executable by the user
     * @param actionName name of the action
     * @param controller the controller class for the page that does the action
     */
    public Action(String actionName, Controller controller) {
        this.actionName = actionName;
        this.controller = controller;
    }

    //getter for action name
    public String getActionName() {
        return actionName;
    }

    //getter for Action's Controller
    public Controller getController() {
        return controller;
    }
 }
