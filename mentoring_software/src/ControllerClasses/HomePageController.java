package ControllerClasses;

import ModelClasses.Action;
import ModelClasses.User;
import ViewClasses.HomePageUI;
import ViewClasses.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class HomePageController extends Controller {
    private HomePageUI ui;
    private User user;

    public HomePageController(View view, User user) {
        super(view);
        this.user = user;
        page = new HomePageUI();
        ui = (HomePageUI) page;
        setUpPage();
    }

    private void setUpPage() {
        // set up content of ui page
        JScrollPane pageScrollPane = ui.getScrollPane();

        // making a content panel for all the page's content to be displayed on
        JPanel content = new JPanel(new GridLayout(0, 1));
        //an arraylist of buttons that the user can interact with
        ArrayList<JButton> buttons = createButtons(user);

        // making the JPanel that will hold all the buttons the user can interact with
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

        // adding the user action buttons to the panel
        for (int i = 0; i < buttons.size(); i++) {
            buttonPanel.add(buttons.get(i));
        }

        // adding button panel to content
        content.add(buttonPanel);

        // adding the content panel to the ViewClasses.UI
        pageScrollPane.getViewport().add(content);
    }

    // function that makes button for each action the user can do
    private ArrayList<JButton> createButtons(User user) {
        // initialising an array list of buttons and extracting the action arraylist from user
        ArrayList<JButton> buttons = new ArrayList<>();
        ArrayList<Action> actions = user.getActions();

        // for loop to go through each action the user can do and set their respective action listener for each button
        for (int i = 0; i < actions.size(); i++) {
            int index = i;
            JButton button = new JButton(actions.get(index).getActionName());
            // set action listener to change ViewClasses.UI page to the appropriate action page to do chosen action
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actions.get(index).getController().changeView();
                }
            });

            buttons.add(button);
        }

        return buttons;
    }
}
