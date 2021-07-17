
package View.JavaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Initiallizer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
              
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
        
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Huffman Compression");
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
        
        stage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
