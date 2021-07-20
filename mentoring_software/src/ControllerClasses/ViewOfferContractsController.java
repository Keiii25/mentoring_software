package ControllerClasses;

import ModelClasses.User;
import ViewClasses.View;
import ViewClasses.ViewOfferContractsUI;
import WebService.DeleteInterfaces.DeleteContract;
import WebService.PostInterfaces.MakeContract;
import WebService.PostInterfaces.SignContract;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ViewOfferContractsController extends Controller implements DeleteContract, MakeContract, SignContract {
    private ViewOfferContractsUI ui;
    private JSONArray offerContracts;
    private User tutor;

    public ViewOfferContractsController(View view, JSONArray offerContracts, User tutor) {
        super(view);
        page = new ViewOfferContractsUI();
        ui = (ViewOfferContractsUI) page;
        this.tutor = tutor;
        this.offerContracts = offerContracts;
        setUpPage();
    }

    // function that sets up the ui page
    private void setUpPage() {
        // setting up back button to return to Home Page
        JButton backButton = ui.getBackButton();
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tutor.updateActionUI();
                new HomePageController(view, tutor).changeView();
            }
        });

        // checking if there are any offer contracts to display
        if (offerContracts.length() > 0) {
            // getting the page's scroll pane
            JScrollPane scrollPane = ui.getScrollPane();

            // removing "No offer contracts" message
            scrollPane.remove(ui.getNoOfferContractsMessage());
            scrollPane.revalidate();
            scrollPane.repaint();

            //initialising formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            // creating JPanel for page contents
            JPanel content = new JPanel(new GridLayout(0, 1));

            // for loop to go through each of the contracts
            for (int i = 0; i < offerContracts.length(); i++) {
                // getting the contract and its JSON Objects
                JSONObject currentContract = offerContracts.getJSONObject(i);
                JSONObject studentDetail = currentContract.getJSONObject("secondParty");
                JSONObject subject = currentContract.getJSONObject("subject");
                JSONObject paymentInfo = currentContract.getJSONObject("paymentInfo");
                JSONObject lessonInfo = currentContract.getJSONObject("lessonInfo");
                JSONObject additionalInfo = currentContract.getJSONObject("additionalInfo");

                // initialising contract string for JPanel
                String contractString = "<html><div><h2>Offer Contract " + (i+1) + "</h2></div>";

                // adding student details
                contractString += "Student name: " + studentDetail.getString("givenName") + " " + studentDetail.getString("familyName") + "<br/>";

                // adding subject details
                contractString += "Subject: " + subject.getString("name") + ": " + subject.getString("description") + "<br/>";

                // adding paymentInfo
                contractString += "Rate: $" + paymentInfo.getInt("rate") + " " + paymentInfo.getString("rateType") + "<br/>";

                // adding lessonInfo
                contractString += "Number of sessions per week: " + lessonInfo.getInt("sessionsPerWeek") + "<br/>";
                contractString += "Duration of each session (hours): " + lessonInfo.getInt("hoursPerLesson") + "<br/>";

                // adding additionalInfo
                contractString += "Contract Duration: " + additionalInfo.getInt("offerContractDuration") + " months<br/>";

                // closing contractString
                contractString += "</html>";

                // making a panel for the contract and adding the JLabel with contract details to it
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel(contractString));

                // making a JPanel for buttons
                JPanel buttonPanel = getButtonPanel(currentContract);

                // add contract JPanel and buttonPanel to content
                content.add(panel);
                content.add(buttonPanel);
            }

            scrollPane.getViewport().add(content);
        }
    }

    // function to make button panel for accept and decline button for each oofer contract
    private JPanel getButtonPanel(JSONObject offerContract) {
        // initialise button Panel
        JPanel buttonPanel = new JPanel();

        // create decline button
        JButton decline = new JButton("Decline");
        decline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // delete offer contract
                deleteContract(offerContract.getString("id"));

                // remove the offer contract from offerContracts
                int i = 0;
                Boolean found = false;

                while (i < offerContracts.length() && !found) {
                    if (offerContracts.getJSONObject(i).getString("id").equals(offerContract.getString("id"))) {
                        offerContracts.remove(i);
                        found = true;
                    }
                    i++;
                }

                // refresh page
                new ViewOfferContractsController(view, offerContracts, tutor).changeView();
            }
        });

        // adding decline button to buttonPanel
        buttonPanel.add(decline);

        // creating accept button
        JButton accept = new JButton("Accept");
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // getting details from offerContract
                String studentId = offerContract.getJSONObject("secondParty").getString("id");
                String subjectId = offerContract.getJSONObject("subject").getString("id");
                JSONObject paymentInfo = offerContract.getJSONObject("paymentInfo");
                JSONObject lessonInfo = offerContract.getJSONObject("lessonInfo");
                JSONObject additionalInfo = offerContract.getJSONObject("additionalInfo");

                // setting contractDuration to offerContractDuration and setting offer field to false
                additionalInfo.put("contractDuration", additionalInfo.getInt("offerContractDuration"));
                additionalInfo.put("offer", false);

                // making a contract from the offer contract's information that is not an offer
                JSONObject confirmedContract = makeContract(tutor.getId(), studentId, subjectId, paymentInfo, lessonInfo, additionalInfo, tutor.getId(), LocalDateTime.now());

                // signing the contract
                signContract(confirmedContract.getString("id"), LocalDateTime.now().plusSeconds(1));

                // delete offer contract
                deleteContract(offerContract.getString("id"));

                // remove the offer contract from offerContracts
                int i = 0;
                Boolean found = false;

                while (i < offerContracts.length() && !found) {
                    if (offerContracts.getJSONObject(i).getString("id").equals(offerContract.getString("id"))) {
                        offerContracts.remove(i);
                        found = true;
                    }
                    i++;
                }

                // refresh page
                new ViewOfferContractsController(view, offerContracts, tutor).changeView();
            }
        });

        // add accept button to buttonPanel
        buttonPanel.add(accept);

        return buttonPanel;
    }
}
