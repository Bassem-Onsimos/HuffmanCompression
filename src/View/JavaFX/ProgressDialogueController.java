
package View.JavaFX;

import Controller.Controller;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.Pair;

public class ProgressDialogueController implements Initializable{

    private Controller controller; 
        
    @FXML
    private Label inputTitle;
    @FXML
    private Label outputTitle;
    @FXML
    private Label inputFile;
    @FXML
    private Label outputFile;
    
    @FXML
    private TextArea log;
    
    private boolean detailsHidden = true;
    
    @FXML
    private Button done;
    @FXML
    private Button toggleDetails;
    
    @FXML
    private ProgressBar progressBar;
    
    private double prefHeight = 404;
    private double prefWidth = 664.0;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {        
        
    }
    
    public void init(int operation, String filePath, GUIController guiController) {
        controller = new Controller();
        
        Stage stage = (Stage) log.getScene().getWindow();
        
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            
            if(newVal.intValue() > prefHeight && detailsHidden) {
                detailsHidden = false;
                toggleDetails.setText("Hide Details");                
            }
            else if(newVal.intValue() <= prefHeight && !detailsHidden) {
                detailsHidden = true;
                toggleDetails.setText("Show Details");  
            }
                        
        });
                
        inputFile.setText(Paths.get(filePath).getFileName().toString());
        outputFile.setText("");
        
        done.setDisable(true);
        toggleDetails.setDisable(true);
                
        switch(operation) {
            case 1: {
                inputTitle.setText("Compressing Text File:");
                outputTitle.setText("Output File:");
                
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        controller.setProgressUpdate((workDone, totalWork) -> updateProgress(workDone, totalWork));
                        return controller.compressTextFileShowProgress(filePath);
                    }
                };
                
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Pair<String, String> output = (Pair<String, String>) task.getValue();
                        outputFile.setText(Paths.get(output.getKey()).getFileName().toString());
                        log.setText("Compresseing Text File:\n────────────\nFile Path: " + filePath + "\n\n" + output.getValue());
                        
                        done.setDisable(false);
                        toggleDetails.setDisable(false);
                    }
                });
                
                progressBar.setProgress(0);
                progressBar.progressProperty().bind(task.progressProperty());               
                
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                
                break;
            }
            case 2: {
                inputTitle.setText("Compressing Binary File:");
                outputTitle.setText("Output File:");
                
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        controller.setProgressUpdate((workDone, totalWork) -> updateProgress(workDone, totalWork));
                        return controller.compressBinaryFileShowProgress(filePath);
                    }
                };
                
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Pair<String, String> output = (Pair<String, String>) task.getValue();
                        outputFile.setText(Paths.get(output.getKey()).getFileName().toString());
                        log.setText("Compresseing Binary File:\n─────────────\nFile Path: " + filePath + "\n\n" + output.getValue());
                        
                        done.setDisable(false);
                        toggleDetails.setDisable(false);
                    }
                });
                
                progressBar.setProgress(0);
                progressBar.progressProperty().bind(task.progressProperty());               
                
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
               
                break;
            }
            case 3: {
                inputTitle.setText("Compressing Folder:");
                outputTitle.setText("Output File:");
                
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        controller.setProgressUpdate((workDone, totalWork) -> updateProgress(workDone, totalWork));
                        return controller.compressFolderShowProgress(filePath);
                    }
                };
                
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Pair<String, String> output = (Pair<String, String>) task.getValue();
                        outputFile.setText(Paths.get(output.getKey()).getFileName().toString());
                        log.setText("Compresseing Folder:\n───────────\nFolder Path: " + filePath + "\n\n" + output.getValue());
                        
                        done.setDisable(false);
                        toggleDetails.setDisable(false);
                    }
                });
                
                progressBar.setProgress(0);
                progressBar.progressProperty().bind(task.progressProperty());               
                
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                
                break;
            }
            case 4: {
                inputTitle.setText("Decompressing File:");
                outputTitle.setText("Output Text File:");
                
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        controller.setProgressUpdate((workDone, totalWork) -> updateProgress(workDone, totalWork));
                        return controller.decompressTextFileShowProgress(filePath);
                    }
                };
                
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Pair<String, String> output = (Pair<String, String>) task.getValue();
                        outputFile.setText(Paths.get(output.getKey()).getFileName().toString());
                        log.setText("Decompresseing Text File:\n─────────────\nFile Path: " + filePath + "\n\n" + output.getValue());
                        guiController.displayDecompressedText(output.getKey());
                        
                        done.setDisable(false);
                        toggleDetails.setDisable(false);
                    }
                });
                
                progressBar.setProgress(0);
                progressBar.progressProperty().bind(task.progressProperty());               
                
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                
                break;
            }
            case 5: {
                inputTitle.setText("Decompressing File:");
                outputTitle.setText("Output Binary File:");
                
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        controller.setProgressUpdate((workDone, totalWork) -> updateProgress(workDone, totalWork));
                        return controller.decompressBinaryFileShowProgress(filePath);
                    }
                };
                
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Pair<String, String> output = (Pair<String, String>) task.getValue();
                        outputFile.setText(Paths.get(output.getKey()).getFileName().toString());
                        log.setText("Decompresseing Binary File:\n──────────────\nFile Path: " + filePath + "\n\n" + output.getValue());
                        
                        done.setDisable(false);
                        toggleDetails.setDisable(false);
                    }
                });
                
                progressBar.setProgress(0);
                progressBar.progressProperty().bind(task.progressProperty());               
                
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                
                break;
            }
            case 6: {
                inputTitle.setText("Decompressing File:");
                outputTitle.setText("Output Folder:");
                
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        controller.setProgressUpdate((workDone, totalWork) -> updateProgress(workDone, totalWork));
                        return controller.decompressFolderShowProgress(filePath);
                    }
                };
                
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Pair<String, String> output = (Pair<String, String>) task.getValue();
                        outputFile.setText(Paths.get(output.getKey()).getFileName().toString());
                        log.setText("Deompresseing Folder:\n───────────\nFolder Path: " + filePath + "\n\n" + output.getValue());
                        
                        done.setDisable(false);
                        toggleDetails.setDisable(false);
                    }
                });
                
                progressBar.setProgress(0);
                progressBar.progressProperty().bind(task.progressProperty());               
                
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                
                break;
            }
        }
        
    }
    
    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) done.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void toggleDetails(ActionEvent event) {
        
        Stage stage = (Stage) done.getScene().getWindow();
        
        if(detailsHidden) {          
            stage.setWidth(1000);
            stage.setHeight(720);
            detailsHidden = false;
            toggleDetails.setText("Hide Details");
            //stage.setResizable(true);
        }
        else {
            stage.setWidth(prefWidth);
            stage.setHeight(prefHeight);
            detailsHidden = true;
            toggleDetails.setText("Show Details");
            //stage.setResizable(false);
        } 
        
        stage.centerOnScreen();
    }

}
