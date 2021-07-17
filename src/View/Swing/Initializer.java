
package View.Swing;

import Controller.Controller;

public class Initializer {

    public static void main(String[] args) {
        Controller controller = new Controller();               
        GUI gui = new GUI(controller); 
    }
}
