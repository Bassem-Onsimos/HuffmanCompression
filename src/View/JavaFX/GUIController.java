package View.JavaFX;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUIController implements Initializable {

    private enum Mode {
        text, binary, folder
    };
    private Mode mode = Mode.text;
    boolean compress = true;

    private boolean debug = false;
    private boolean printFiles = false;

    @FXML
    private Button textTab;
    @FXML
    private Button binaryTab;
    @FXML
    private Button folderTab;
    @FXML
    private Tab compressTab;
    @FXML
    private Tab decompressTab;

    @FXML
    private Button compressButton;

    @FXML
    private SwingNode compressSwingTextArea;
    @FXML
    private SwingNode decompressSwingTextArea;

    private JTextArea compressTextArea;
    private JTextArea decompressTextArea;

    @FXML
    private TextField compressFilePath;
    @FXML
    private TextField decompressFilePath;

    @FXML
    private MenuItem New;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem save;
    @FXML
    private MenuItem quit;
    @FXML
    private MenuItem copy;
    @FXML
    private MenuItem cut;
    @FXML
    private MenuItem paste;
    @FXML
    private MenuItem about;

    @FXML
    private void sideTabSelected(ActionEvent event) {
        if (event.getSource().equals(textTab)) {
            mode = Mode.text;
            textTab.getStyleClass().clear();
            binaryTab.getStyleClass().clear();
            folderTab.getStyleClass().clear();
            textTab.getStyleClass().add("selectedButton");
            binaryTab.getStyleClass().add("unselectedButton");
            folderTab.getStyleClass().add("unselectedButton");

            compressButton.setText("Save & Compress");
           
            compressSwingTextArea.setVisible(true);
            decompressSwingTextArea.setVisible(true);

            compressTextArea.setText("");
            decompressTextArea.setText("");

            compressFilePath.setText("");
            decompressFilePath.setText("");

            setMenuItemsDisabled(!compress);
            
        } else if (event.getSource().equals(binaryTab)) {
            mode = Mode.binary;
            textTab.getStyleClass().clear();
            binaryTab.getStyleClass().clear();
            folderTab.getStyleClass().clear();
            textTab.getStyleClass().add("unselectedButton");
            binaryTab.getStyleClass().add("selectedButton");
            folderTab.getStyleClass().add("unselectedButton");

            compressButton.setText("Compress");

            compressTextArea.setText("");
            decompressTextArea.setText("");

            compressFilePath.setText("");
            decompressFilePath.setText("");
            
            compressSwingTextArea.setVisible(false);
            decompressSwingTextArea.setVisible(false);

            setMenuItemsDisabled(true);
            
        } else if (event.getSource().equals(folderTab)) {
            mode = Mode.folder;
            textTab.getStyleClass().clear();
            binaryTab.getStyleClass().clear();
            folderTab.getStyleClass().clear();
            textTab.getStyleClass().add("unselectedButton");
            binaryTab.getStyleClass().add("unselectedButton");
            folderTab.getStyleClass().add("selectedButton");

            compressButton.setText("Compress");

            compressTextArea.setText("");
            decompressTextArea.setText("");

            compressFilePath.setText("");
            decompressFilePath.setText("");
            
            compressSwingTextArea.setVisible(false);
            decompressSwingTextArea.setVisible(false);

            setMenuItemsDisabled(true);
        }
    }

    @FXML
    private void topTabSelected(Event event) {
        if (event.getTarget().equals(compressTab)) {
            compress = true;
        } else if (event.getTarget().equals(decompressTab)) {
            compress = false;
        }

        if (mode == Mode.text) {
            setMenuItemsDisabled(!compress);
        }
    }

    @FXML
    private void menuHandler(ActionEvent event) {
        if (event.getSource().equals(New)) {
            compressTextArea.setText("");
            compressFilePath.setText("");
        } else if (event.getSource().equals(open)) {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files (.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(filter);

            File selectedFile = fileChooser.showOpenDialog(compressFilePath.getScene().getWindow());

            if (selectedFile != null) {
                compressFilePath.setText(selectedFile.getAbsolutePath());
                try {
                    compressTextArea.setText(new String(Files.readAllBytes(selectedFile.toPath())));
                } catch (IOException ex) {
                    printStackTrace(ex);
                }
            }
        } else if (event.getSource().equals(save)) {
            FileChooser fileChooser = new FileChooser();

            File selectedFile = fileChooser.showSaveDialog(compressFilePath.getScene().getWindow());

            if (selectedFile != null) {

                String path = selectedFile.getAbsolutePath();

                if (!path.endsWith(".txt")) {
                    path += ".txt";
                }

                compressFilePath.setText(path);
                try {
                    Files.write(Paths.get(path), compressTextArea.getText().getBytes());
                } catch (IOException ex) {
                    printStackTrace(ex);
                }
            } else {
                createWarningDialogue("File was not saved!", "");
            }
        } else if (event.getSource().equals(quit)) {
            System.exit(0);
        } else if (event.getSource().equals(copy)) {
            compressTextArea.copy();
        } else if (event.getSource().equals(cut)) {
            compressTextArea.cut();
        } else if (event.getSource().equals(paste)) {
            compressTextArea.paste();
        } else if (event.getSource().equals(about)) {

        }
    }

    @FXML
    private void selectCompressFile(ActionEvent event) {
        compressFilePath.setText("");
        switch (mode) {
            case text: {
                compressTextArea.setText("");
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files (.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {
                    compressFilePath.setText(selectedFile.getAbsolutePath());
                    try {
                        compressTextArea.setText(new String(Files.readAllBytes(selectedFile.toPath())));
                    } catch (IOException ex) {
                        printStackTrace(ex);
                    }
                }

                break;
            }
            case binary: {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {
                    compressFilePath.setText(selectedFile.getAbsolutePath());
                }

                break;
            }
            case folder: {
                DirectoryChooser directoryChooser = new DirectoryChooser();

                File selectedFile = directoryChooser.showDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {
                    compressFilePath.setText(selectedFile.getAbsolutePath());
                }

                break;
            }
        }
    }

    @FXML
    private void selectDecompressFile(ActionEvent event) {
        decompressFilePath.setText("");
        switch (mode) {
            case text: {
                decompressTextArea.setText("");

                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Huffman Compressd Text Files (.HCT)", "*.HCT");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {
                    decompressFilePath.setText(selectedFile.getAbsolutePath());
                }

                break;
            }
            case binary: {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Huffman Compressed Binary Files (.HCB)", "*.HCB");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {
                    decompressFilePath.setText(selectedFile.getAbsolutePath());
                }

                break;
            }
            case folder: {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Huffman Compressed Folders (.HCF)", "*.HCF");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {
                    decompressFilePath.setText(selectedFile.getAbsolutePath());
                }

                break;
            }
        }
    }

    @FXML
    private void compress(ActionEvent event) {

        if (compressFilePath.getText().isEmpty()) {

            if (mode == Mode.text) {

                FileChooser fileChooser = new FileChooser();

                File selectedFile = fileChooser.showSaveDialog(compressFilePath.getScene().getWindow());

                if (selectedFile != null) {

                    String path = selectedFile.getAbsolutePath();

                    if (!path.endsWith(".txt")) {
                        path += ".txt";
                    }

                    compressFilePath.setText(path);
                    try {
                        Files.write(Paths.get(path), compressTextArea.getText().getBytes());
                    } catch (IOException ex) {
                        printStackTrace(ex);
                    }

                    createProgressDialogue(1, path);
                } else {
                    createErrorDialogue("File was not saved!", "The file must be saved in order to be compressed.");
                }
            } else if (mode == Mode.binary) {
                createErrorDialogue("File path is empty!", "Please select a file to compress.");
            } else {
                createErrorDialogue("File path is empty!", "Please select a folder to compress.");
            }

        } else {
            switch (mode) {
                case text: {
                    try {
                        Files.write(Paths.get(compressFilePath.getText()), compressTextArea.getText().getBytes());
                    } catch (IOException ex) {
                        printStackTrace(ex);
                    }

                    createProgressDialogue(1, compressFilePath.getText());
                    break;
                }
                case binary: {
                    createProgressDialogue(2, compressFilePath.getText());
                    break;
                }
                case folder: {
                    createProgressDialogue(3, compressFilePath.getText());
                    break;
                }
            }
        }
    }

    @FXML
    private void decompress(ActionEvent event) {

        if (decompressFilePath.getText().isEmpty()) {
            createErrorDialogue("File path is empty!", "Please select a file to decompress.");
        } else {
            switch (mode) {
                case text: {
                    createProgressDialogue(4, decompressFilePath.getText());
                    break;
                }
                case binary: {
                    createProgressDialogue(5, decompressFilePath.getText());
                    break;
                }
                case folder: {
                    createProgressDialogue(6, decompressFilePath.getText());
                    break;
                }
            }
        }
    }

    private void setMenuItemsDisabled(boolean state) {
        New.setDisable(state);
        open.setDisable(state);
        save.setDisable(state);
        copy.setDisable(state);
        cut.setDisable(state);
        paste.setDisable(state);
    }

    private void createWarningDialogue(String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("css/GUI_Style.css").toExternalForm());

        alert.showAndWait();
    }

    private void createErrorDialogue(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("css/GUI_Style.css").toExternalForm());

        alert.showAndWait();
    }

    private void createProgressDialogue(int operation, String filePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressDialogue.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Log");
            stage.setScene(scene);

            ProgressDialogueController dialogueController = loader.getController();
            dialogueController.init(operation, filePath, this);

            stage.show();
        } catch (IOException e) {
            printStackTrace(e);
        }
    }

    public void displayDecompressedText(String filePath) {
        try {
            decompressTextArea.setText(new String(Files.readAllBytes(Paths.get(filePath))));
        } catch (IOException ex) {
            printStackTrace(ex);
        }
    }

    private void printStackTrace(Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("An exception was caught!");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        compressTextArea = new JTextArea();
        compressTextArea.setTabSize(3);
        compressTextArea.setFont(new Font("Arial", Font.PLAIN, 15));
        compressTextArea.setForeground(new Color(63, 43, 99));
        compressTextArea.setBackground(new Color(244, 244, 244));
        compressTextArea.setBorder(BorderFactory.createCompoundBorder(compressTextArea.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        compressTextArea.setDoubleBuffered(false);
        
        compressSwingTextArea.setContent(new JScrollPane(compressTextArea));

        decompressTextArea = new JTextArea();
        decompressTextArea.setTabSize(3);
        decompressTextArea.setFont(new Font("Arial", Font.PLAIN, 15));
        decompressTextArea.setForeground(new Color(63, 43, 99));
        decompressTextArea.setBackground(new Color(244, 244, 244));
        decompressTextArea.setBorder(BorderFactory.createCompoundBorder(decompressTextArea.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        decompressTextArea.setEditable(false);

        decompressSwingTextArea.setContent(new JScrollPane(decompressTextArea));
    }

}
