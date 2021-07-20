package ControllerClasses;

import ModelClasses.Student;
import ModelClasses.StudentTutor;
import ModelClasses.Tutor;
import ModelClasses.User;
import ViewClasses.LoginUI;
import ViewClasses.View;
import WebService.GetInterfaces.GetUser;
import WebService.PostInterfaces.LoginJwt;
import WebService.DecodeJwt;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginController extends Controller implements GetUser, DecodeJwt, LoginJwt {
    private User currentUser;

    public LoginController(View view){
        super(view);
        page = new LoginUI();
        ((LoginUI) page).addLoginListener(new LoginListener());
    }


    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (validate()) {
                try {
                    JSONObject jwtToken = decodeJwt(loginJwt(((LoginUI)page).getUsername(), ((LoginUI)page).getPassword()).getString("jwt"));
                    successfulLogin(jwtToken);
                } catch (IOException ioException) {
                    // Message if wrong username or password is entered
                    System.out.println(ioException);
                    JOptionPane.showMessageDialog(null, "username and/or password entered are not valid");
                }
            }
        }

        private boolean validate() {
            Boolean valid = false;
            if (((LoginUI)page).getUsername().isEmpty() || ((LoginUI)page).getPassword().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter your username and password");
            } else {
                valid = true;
            }

            return valid;
        }

        private void successfulLogin(JSONObject loginJwt) {
            // extracting use details from userID from loginJwt with competencies
            JSONObject userDetails = getUser(loginJwt.getString("sub"), 3);
            // getting user qualifications
            JSONArray userQualifications = getUser(loginJwt.getString("sub"), 4).getJSONArray("qualifications");

            // make specific user classes depending on user details got from login
            if (userDetails.getBoolean("isStudent") && userDetails.getBoolean("isTutor")){
                currentUser = new StudentTutor(userDetails.getString("givenName"), userDetails.getString("familyName"), userDetails.getString("id"), userDetails.getString("userName"), userDetails.getJSONArray("competencies"), userQualifications, view);
            } else if (userDetails.getBoolean("isStudent") && !userDetails.getBoolean("isTutor")) {
                currentUser = new Student(userDetails.getString("givenName"), userDetails.getString("familyName"), userDetails.getString("id"), userDetails.getString("userName"), userDetails.getJSONArray("competencies"), userQualifications, view);
            } else if (!userDetails.getBoolean("isStudent") && userDetails.getBoolean("isTutor")) {
                currentUser = new Tutor(userDetails.getString("givenName"), userDetails.getString("familyName"), userDetails.getString("id"), userDetails.getString("userName"), userDetails.getJSONArray("competencies"), userQualifications, view);
            }

            if (currentUser.getAboutToExpireContracts().length() > 0) {
                new NotificationController(view, currentUser).changeView();
            } else {
                new HomePageController(view, currentUser).changeView();
            }
        }
    }


}
