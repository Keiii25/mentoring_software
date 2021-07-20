package ControllerClasses;

import ViewClasses.UI;
import ViewClasses.View;

public abstract class Controller {
    protected UI page;
    protected View view;

    public Controller(View view) {
        this.view = view;
    }

    public void changeView() {
        view.updateUI(page);
    }
}
