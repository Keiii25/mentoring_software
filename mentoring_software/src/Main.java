import ControllerClasses.LoginController;
import ViewClasses.View;

public class Main {
    private View window;

    // constructor
    public Main() {
        // makes a new LoginUI and set window to LoginUI
        window = new View();
        new LoginController(window).changeView();
    }

    // public static main to run the app
    public static void main(String[] args) {
        new Main();
    }
}
