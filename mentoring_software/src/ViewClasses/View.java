package ViewClasses;

import javax.swing.*;

public class View {
    private JFrame UIframe;
    private UI oldUI;
    private UI currentUI;

    // Constructor
    public View() {
        // initiating the app window and setting a way to close the app
        UIframe = new JFrame("App");
        UIframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setting the size of the window and its position on the screen to be center
        UIframe.setSize(750,500);
        UIframe.setLocationRelativeTo(null);
    }

    // method to change the UIFrame window content
    public void updateUI(UI newUI) {
        this.oldUI = currentUI;
        this.currentUI = newUI;
        UIframe.setContentPane(newUI.getRootPanel());
        UIframe.setVisible(true);
    }

    public UI getCurrentUI() {
        return currentUI;
    }

    public UI getOldUI() {
        return oldUI;
    }
}
