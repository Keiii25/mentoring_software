package ControllerClasses;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface MakeChangeUIButton {
    // function that makes a button that changes the ViewClasses.UI
    default JButton makeChangeUIButton(String buttonText, Controller nextPage) {
        // making a button with given buttonText
        JButton button = new JButton(buttonText);
        // adding action to the button to go to given nextUI
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextPage.changeView();
            }
        });

        return button;
    }
}
