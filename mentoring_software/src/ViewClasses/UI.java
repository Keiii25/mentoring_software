package ViewClasses;

import javax.swing.*;

/**
 * An abstract class for ViewClasses.UI window
 */
public abstract class UI {
    private JPanel rootPanel;
    //protected ViewClasses.View view;

    public void setRootPanel(JPanel rootPanel) {
        this.rootPanel = rootPanel;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
