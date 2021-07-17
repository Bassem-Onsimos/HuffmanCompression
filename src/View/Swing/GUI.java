
package View.Swing;

import Controller.Controller;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.util.Pair;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame implements ActionListener, ItemListener, ChangeListener {
    
    private JFrame mainFrame;
    
    private final JPanel compressorCards;
    private final JPanel decompressorCards;
    
    private final JRadioButton textCompression;
    private final JRadioButton binaryCompression;
    private final JRadioButton folderCompression;
    
    private final JRadioButton textDecompression;
    private final JRadioButton binaryDecompression;
    private final JRadioButton folderDecompression;
    
    private final JMenuItem New;
    private final JMenuItem open;
    private final JMenuItem save;
    private final JMenuItem exit;
    private final JMenuItem copy;
    private final JMenuItem cut;
    private final JMenuItem paste;
            
    private final JTextArea compressTextFileEditor;
    private final JTextArea decompressTextFileEditor;
    
    private final JTextArea compressTextFilePath;
    private final JTextArea decompressTextFilePath;
    
    private final JTextArea compressFilePath;
    private final JTextArea decompressFilePath;
    
    private final JButton compressButton;
    
    private final JButton compressTextFileChooser;
    JButton decompressTextFileChooser;
    
    private final JButton compressFileChooser;
    JButton decompressFileChooser;
    
    private final JCheckBox debugCheckBox1;
    private final JCheckBox debugCheckBox2;
    
    private final JCheckBox printCheckBox1;
    private final JCheckBox printCheckBox2;
    
    private boolean debug = false;
    private boolean printFiles = false;

    
    private enum Mode{text, binary, folder};
    private Mode mode = Mode.text;
    private boolean compress = true;
    
    private final Controller controller;
    
    public GUI(Controller controller) {
        
        this.controller = controller;        
                
        JMenu fileMenu = new JMenu("File");
        
        New = new JMenuItem("New");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        exit = new JMenuItem("Exit"); 
        
        New.addActionListener(this);
        open.addActionListener(this);
        save.addActionListener(this);
        exit.addActionListener(this);
        
        fileMenu.add(New);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(exit);
        
        //
        
        JMenu editMenu = new JMenu("Edit");
        
        copy = new JMenuItem("Copy");
        cut = new JMenuItem("Cut");
        paste = new JMenuItem("Paste");
        
        copy.addActionListener(this);
        cut.addActionListener(this);
        paste.addActionListener(this);
        
        editMenu.add(copy);
        editMenu.add(cut);
        editMenu.add(paste);
        
        //
        
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        //
        
        compressTextFileEditor = new JTextArea(30, 62);
        compressTextFileEditor.setTabSize(3);
        
        JLabel compressLabel = new JLabel("File Path");
        JLabel compressTextFileLabel = new JLabel("File Path");
        
        compressFilePath = new JTextArea(1, 50);
        compressTextFilePath = new JTextArea(1, 50);
        
        compressFilePath.setEditable(false);
        compressTextFilePath.setEditable(false);
        
        compressFileChooser = new JButton("Browse");
        compressFileChooser.addActionListener(this);
        
        compressTextFileChooser = new JButton("Browse");
        compressTextFileChooser.addActionListener(this);
        
        compressButton = new JButton("Save & Compress");
        compressButton.addActionListener(this);
        
        //
        
        decompressTextFileEditor = new JTextArea(30, 62);
        decompressTextFileEditor.setEditable(false);
        decompressTextFileEditor.setTabSize(3);
        
        JLabel decompressLabel = new JLabel("File Path");
        JLabel decompressTextFileLabel = new JLabel("File Path");
        
        decompressFilePath = new JTextArea(1, 50);
        decompressTextFilePath = new JTextArea(1, 50);
        
        decompressFilePath.setEditable(false);
        decompressTextFilePath.setEditable(false);
        
        decompressFileChooser = new JButton("Browse");
        decompressFileChooser.addActionListener(this);
        
        decompressTextFileChooser = new JButton("Browse");
        decompressTextFileChooser.addActionListener(this);
        
        JButton decompressButton = new JButton("Decompress");
        decompressButton.addActionListener(this);
        
        //
        
        textCompression = new JRadioButton("Text File");
        binaryCompression = new JRadioButton("Binary File");
        folderCompression = new JRadioButton("Folder");
        
        textCompression.addActionListener(this);
        binaryCompression.addActionListener(this);
        folderCompression.addActionListener(this);
        
        ButtonGroup compressionButtonGroup = new ButtonGroup();
        compressionButtonGroup.add(textCompression);
        compressionButtonGroup.add(binaryCompression);
        compressionButtonGroup.add(folderCompression);
        
        textCompression.setSelected(true);
        
        //
        
        textDecompression = new JRadioButton("Text File");
        binaryDecompression = new JRadioButton("Binary File");
        folderDecompression = new JRadioButton("Folder");
        
        textDecompression.addActionListener(this);
        binaryDecompression.addActionListener(this);
        folderDecompression.addActionListener(this);
        
        ButtonGroup decompressionButtonGroup = new ButtonGroup();
        decompressionButtonGroup.add(textDecompression);
        decompressionButtonGroup.add(binaryDecompression);
        decompressionButtonGroup.add(folderDecompression);
        
        textDecompression.setSelected(true);
        
        //
        
        compressorCards = new JPanel(new CardLayout());
        decompressorCards = new JPanel(new CardLayout());
        
        //
        
        JPanel compressTextFileCard = new JPanel(new BorderLayout());       
        
        JPanel compressTextFilePanel = new JPanel();
        compressTextFilePanel.add(compressTextFileLabel);
        compressTextFilePanel.add(new JScrollPane(compressTextFilePath));
        compressTextFilePanel.add(compressTextFileChooser); 
                
        compressTextFileCard.add(compressTextFilePanel, BorderLayout.NORTH);
        compressTextFileCard.add(new JScrollPane(compressTextFileEditor), BorderLayout.SOUTH);
        
        //
        
        JPanel compressOtherCard = new JPanel();
        compressOtherCard.add(compressLabel);
        compressOtherCard.add(new JScrollPane(compressFilePath));
        compressOtherCard.add(compressFileChooser);       
        
        //
        
        compressorCards.add(compressTextFileCard, "text");
        compressorCards.add(compressOtherCard, "other");
        
        //
                
        JPanel decompressTextFileCard = new JPanel(new BorderLayout());       
        
        JPanel decompressTextFilePanel = new JPanel();
        decompressTextFilePanel.add(decompressTextFileLabel);
        decompressTextFilePanel.add(new JScrollPane(decompressTextFilePath));
        decompressTextFilePanel.add(decompressTextFileChooser); 
                
        decompressTextFileCard.add(decompressTextFilePanel, BorderLayout.NORTH);
        decompressTextFileCard.add(new JScrollPane(decompressTextFileEditor), BorderLayout.SOUTH);
        
        //
        
        JPanel decompressOtherCard = new JPanel();
        decompressOtherCard.add(decompressLabel);
        decompressOtherCard.add(new JScrollPane(decompressFilePath));
        decompressOtherCard.add(decompressFileChooser);       
        
        //
        
        decompressorCards.add(decompressTextFileCard, "text");
        decompressorCards.add(decompressOtherCard, "other");
        
        //
        
        JPanel compressorBottomPanel = new JPanel(new BorderLayout(0, 5));
        JPanel decompressorBottomPanel = new JPanel(new BorderLayout(0, 5));
        
        JPanel compressCheckBoxes = new JPanel(new BorderLayout(10, 0));
        JPanel decompressCheckBoxes = new JPanel(new BorderLayout(10, 0));
        
        debugCheckBox1 = new JCheckBox("Debugging Mode");
        debugCheckBox2 = new JCheckBox("Debugging Mode");
        
        printCheckBox1 = new JCheckBox("Print Files");
        printCheckBox2 = new JCheckBox("Print Files");
        
        debugCheckBox1.addItemListener(this);
        debugCheckBox2.addItemListener(this);
        
        printCheckBox1.addItemListener(this);
        printCheckBox2.addItemListener(this);
        
        debugCheckBox1.setSelected(false);
        debugCheckBox2.setSelected(false);
        
        printCheckBox1.setSelected(false);
        printCheckBox2.setSelected(false);
        
        printCheckBox1.setEnabled(false);
        printCheckBox2.setEnabled(false);
        
        compressCheckBoxes.add(debugCheckBox1, BorderLayout.WEST);
        compressCheckBoxes.add(printCheckBox1, BorderLayout.EAST);
        
        decompressCheckBoxes.add(debugCheckBox2, BorderLayout.WEST);
        decompressCheckBoxes.add(printCheckBox2, BorderLayout.EAST);
        
        compressorBottomPanel.add(compressCheckBoxes, BorderLayout.NORTH);
        compressorBottomPanel.add(compressButton, BorderLayout.SOUTH);
        
        decompressorBottomPanel.add(decompressCheckBoxes, BorderLayout.NORTH);
        decompressorBottomPanel.add(decompressButton,BorderLayout.SOUTH);
        
        //

        JPanel compressorPanel = new JPanel();
        compressorPanel.setOpaque(false);
        compressorPanel.add(textCompression);
        compressorPanel.add(binaryCompression);
        compressorPanel.add(folderCompression);
        compressorPanel.add(compressorCards);
        compressorPanel.add(compressorBottomPanel);
        
        //
        
        JPanel decompressorPanel = new JPanel();
        decompressorPanel.setOpaque(false);
        decompressorPanel.add(textDecompression);
        decompressorPanel.add(binaryDecompression);
        decompressorPanel.add(folderDecompression);
        decompressorPanel.add(decompressorCards);
        decompressorPanel.add(decompressorBottomPanel);

        //
        
        JTabbedPane tabs=new JTabbedPane();
        tabs.add("Compressor", compressorPanel);
        tabs.add("Decompressor", decompressorPanel);
        
        tabs.addChangeListener(this);
        
        //
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
        
                mainFrame = new JFrame("Huffman Compression");   

                mainFrame.setContentPane(tabs);
                mainFrame.setJMenuBar(menuBar);

                mainFrame.setSize(800, 715);
                mainFrame.setResizable(false);
                mainFrame.setLocationRelativeTo(null);
                                
                mainFrame.setVisible(true);

                mainFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) 
                    { 
                        System.exit(0); 
                    }
                });
            }
        });
        
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        String s = e.getActionCommand(); 
  
        switch (s) {
            case "Exit":
                System.exit(0);
            case "Cut":
                compressTextFileEditor.cut();
                return;
            case "Copy":
                compressTextFileEditor.copy();
                return;
            case "Paste":
                compressTextFileEditor.paste();
                return;
            case "Save":
            {
                // Create an object of JFileChooser class
                JFileChooser j = new JFileChooser("src/");
                
                // Invoke the showsSaveDialog function to show the save dialog
                int r = j.showSaveDialog(null);
                
                if (r == JFileChooser.APPROVE_OPTION) {
                    
                    String filePath = j.getSelectedFile().getAbsolutePath();
                    
                    if(!filePath.endsWith(".txt")) filePath += ".txt";
                    
                    compressTextFilePath.setText(filePath);
                    
                    try {
                        Files.write(Paths.get(filePath), compressTextFileEditor.getText().getBytes());
                    }
                    catch (Exception evt) {
                        evt.printStackTrace();
                    }
                }
                // If the user cancelled the operation
                else
                    JOptionPane.showMessageDialog(mainFrame, "The file was not saved!");
                
                return;
            }
            case "Open":
            {
                // Create an object of JFileChooser class
                JFileChooser j = new JFileChooser("src/");
                FileFilter filter = new FileNameExtensionFilter("Text Files", "txt");
                j.addChoosableFileFilter(filter);
                
                j.setAcceptAllFileFilterUsed(false);
                
                // Invoke the showsOpenDialog function to show the save dialog
                int r = j.showOpenDialog(null);
                
                // If the user selects a file
                if (r == JFileChooser.APPROVE_OPTION) {
                    
                    compressTextFilePath.setText(j.getSelectedFile().getAbsolutePath());
                    
                    try {
                        compressTextFileEditor.setText(new String(Files.readAllBytes(Paths.get(j.getSelectedFile().getAbsolutePath()))));
                    } catch (IOException ev) {
                        ev.printStackTrace();
                    }
                    
                }
                return;
            }
            case "New":
                compressTextFileEditor.setText("");
                compressTextFilePath.setText("");
                return;
            default:
                break;
        } 
        
        CardLayout cl = (CardLayout)(compressorCards.getLayout());
        CardLayout dl = (CardLayout)(decompressorCards.getLayout());

        if(e.getSource() == textCompression || e.getSource() == textDecompression) {
            if(mode != Mode.text) {
                cl.show(compressorCards, "text");
                dl.show(decompressorCards, "text");
                mode = Mode.text;
                textCompression.setSelected(true);
                textDecompression.setSelected(true);
                compressFilePath.setText("");
                decompressFilePath.setText("");
                compressButton.setText("Save & Compress");
                
                if(compress) setMenuItemsState(true);
            }
            return;
        }
        else if(e.getSource() == binaryCompression || e.getSource() == binaryDecompression){
            if(mode != Mode.binary) {
                cl.show(compressorCards, "other");
                dl.show(decompressorCards, "other");
                mode = Mode.binary;
                binaryCompression.setSelected(true);
                binaryDecompression.setSelected(true);
                compressFilePath.setText("");
                decompressFilePath.setText("");
                compressButton.setText("Compress");
                decompressTextFileEditor.setText("");
                
                setMenuItemsState(false);
            }
            return;
        }
        else if(e.getSource() == folderCompression || e.getSource() == folderDecompression){
            if(mode != Mode.folder) {
                cl.show(compressorCards, "other");
                dl.show(decompressorCards, "other");
                mode = Mode.folder;
                folderCompression.setSelected(true);
                folderDecompression.setSelected(true);
                compressFilePath.setText("");
                decompressFilePath.setText("");
                compressButton.setText("Compress");
                decompressTextFileEditor.setText("");
                
                setMenuItemsState(false);
            }
            return;
        }
        
        if(e.getSource() == compressTextFileChooser) {
            // Create an object of JFileChooser class 
            JFileChooser j = new JFileChooser("src/"); 
            FileFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            j.addChoosableFileFilter(filter);
            
            j.setAcceptAllFileFilterUsed(false);
  
            // Invoke the showsOpenDialog function to show the save dialog 
            int r = j.showOpenDialog(null); 
  
            // If the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) { 
                
                compressTextFilePath.setText(j.getSelectedFile().getAbsolutePath());
                
                File file = new File(j.getSelectedFile().getAbsolutePath()); 
                
                try { 
                    
                    String data = ""; 
                    data = new String(Files.readAllBytes(file.toPath()));
                    
                    // Set the text 
                    compressTextFileEditor.setText(data);
                    
                } 
                catch (Exception evt) { 
                    JOptionPane.showMessageDialog(mainFrame, evt.getMessage()); 
                } 
                 
            } 
            return;
        }
        else if(e.getSource() == compressFileChooser) {
            // Create an object of JFileChooser class 
            JFileChooser j = new JFileChooser("src/"); 
            
            if(mode == Mode.folder) {
                j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                j.setAcceptAllFileFilterUsed(false);
            }
            else {
                j.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
            
            // Invoke the showsOpenDialog function to show the save dialog 
            int r = j.showOpenDialog(null); 
  
            // If the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) { 
                compressFilePath.setText(j.getSelectedFile().getAbsolutePath()); 
            } 
            return;
        }
        else if(e.getSource() == decompressTextFileChooser) {
            // Create an object of JFileChooser class 
            JFileChooser j = new JFileChooser("src/"); 
            
            FileFilter filter = new FileNameExtensionFilter("Huffman Compressed Text Files", "HCT");
            j.addChoosableFileFilter(filter);
            
            j.setAcceptAllFileFilterUsed(false);
            
            // Invoke the showsOpenDialog function to show the save dialog 
            int r = j.showOpenDialog(null); 
  
            // If the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) { 
                // Set the label to the path of the selected directory 
                decompressTextFilePath.setText(j.getSelectedFile().getAbsolutePath());  
                decompressTextFileEditor.setText("");
            } 
            return;
        }
        else if(e.getSource() == decompressFileChooser) {
            // Create an object of JFileChooser class 
            JFileChooser j = new JFileChooser("src/"); 
            
            if(mode == Mode.folder) {
                FileFilter filter = new FileNameExtensionFilter("Huffman Compressed Folders", "HCF");
                j.addChoosableFileFilter(filter);

                j.setAcceptAllFileFilterUsed(false);
            }
            else {
                FileFilter filter = new FileNameExtensionFilter("Huffman Compressed Binary Files", "HCB");
                j.addChoosableFileFilter(filter);

                j.setAcceptAllFileFilterUsed(false);
            }
            
            // Invoke the showsOpenDialog function to show the save dialog 
            int r = j.showOpenDialog(null); 
  
            // If the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) { 
                // Set the label to the path of the selected directory 
                decompressFilePath.setText(j.getSelectedFile().getAbsolutePath()); 
                 
            } 
            return;
        }
        
        if(e.getActionCommand().equals("Save & Compress")) {
            if(compressTextFilePath.getText().isEmpty()) {
                
                JFileChooser j = new JFileChooser("src/"); 

                // Invoke the showsSaveDialog function to show the save dialog 
                int r = j.showSaveDialog(null); 

                if (r == JFileChooser.APPROVE_OPTION) { 
                    
                    String filePath = j.getSelectedFile().getAbsolutePath();
                    
                    if(!filePath.endsWith(".txt")) filePath += ".txt";

                    compressTextFilePath.setText(filePath);

                    try { 
                        Files.write(Paths.get(filePath), compressTextFileEditor.getText().getBytes());
                        
                        createLogWindow(1, filePath);
                    } 
                    catch (Exception evt) { 
                        evt.printStackTrace();
                    } 
                } 
                else
                    JOptionPane.showMessageDialog(mainFrame, "The file must be saved in order to be compressed!"); 
            
            }
            else{
  
                try {             
                    Files.write(Paths.get(compressTextFilePath.getText()), compressTextFileEditor.getText().getBytes());
                
                    createLogWindow(1, compressTextFilePath.getText());
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            
                return;
            }
        }
        else if(e.getActionCommand().equals("Compress")) {
            if(mode == Mode.binary) {
                if(!compressFilePath.getText().isEmpty()) {
                    createLogWindow(2, compressFilePath.getText());
                }
                else {
                    JOptionPane.showMessageDialog(mainFrame, "Please choose a file to compress!"); 
                }
            }
            else {
                if(!compressFilePath.getText().isEmpty()) {
                    createLogWindow(3, compressFilePath.getText());
                }
                else {
                    JOptionPane.showMessageDialog(mainFrame, "Please choose a folder to compress!"); 
                }
            }
            return;
        }
        else if(e.getActionCommand().equals("Decompress")) {
            
            if(mode == Mode.text) {
                if(!decompressTextFilePath.getText().isEmpty()) {
                    createLogWindow(4, decompressTextFilePath.getText());
                }
                else {
                    JOptionPane.showMessageDialog(mainFrame, "Please choose a file to decompress!"); 
                }
            }
            else if(mode == Mode.binary) {
                if(!decompressFilePath.getText().isEmpty()) {
                    createLogWindow(5, decompressFilePath.getText());
                }
                else {
                    JOptionPane.showMessageDialog(mainFrame, "Please choose a file to decompress!"); 
                }
            }
            else {
                if(!decompressFilePath.getText().isEmpty()) {
                    createLogWindow(6, decompressFilePath.getText());
                }
                else {
                    JOptionPane.showMessageDialog(mainFrame, "Please choose a folder to decompress!"); 
                }
            }
            
            
            return;
        }
    }
    
    private void createLogWindow(int operation, String filePath) {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame frame = new JFrame("Log");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                try 
                {
                   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                   e.printStackTrace();
                }
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setOpaque(true);
                
                JTextArea logTextArea = new JTextArea(35, 80);
                logTextArea.setWrapStyleWord(true);
                logTextArea.setEditable(false);
                logTextArea.setFont(Font.getFont(Font.SANS_SERIF));
                logTextArea.setTabSize(3);
                
                JScrollPane scroller = new JScrollPane(logTextArea);
                  
                panel.add(scroller);

                frame.getContentPane().add(BorderLayout.CENTER, panel);
                frame.pack();
                frame.setResizable(true);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                
                switch(operation) {
                    
                    case 1: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                logTextArea.setText("Compresseing Text File:\n────────────\nFile Path: " + filePath + "\n\n");
                                
                                String log = controller.compressTextFile(filePath, debug, printFiles).getValue();
                                
                                logTextArea.append(log);
                            }
                        }).start();                        
                        
                        return;
                    }
                    case 2: {
                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                logTextArea.setText("Compresseing Binary File:\n─────────────\nFile Path: " + filePath + "\n\n");
                                
                                String log = controller.compressBinaryFile(filePath, debug, printFiles).getValue();
                                
                                logTextArea.append(log);
                            }
                        }).start();
                        
                        return;
                    }
                    case 3: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                logTextArea.setText("Compresseing Folder:\n───────────\nFolder Path: " + filePath + "\n\n");
                                
                                String log = controller.compressFolder(filePath, debug, printFiles).getValue();
                                
                                logTextArea.append(log);
                            }
                        }).start();
                        
                        return;
                    }
                    case 4: {
                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                
                                logTextArea.setText("Decompresseing Text File:\n─────────────\nFile Path: " + filePath + "\n\n");
                                
                                Pair<String, String> output = controller.decompressTextFile(filePath, debug, printFiles);
                                
                                logTextArea.append(output.getValue());
                                
                                File file = new File(output.getKey()); 

                                try {                   
                                    String data = ""; 
                                    data = new String(Files.readAllBytes(file.toPath()));

                                    // Set the text 
                                    decompressTextFileEditor.setText(data);
                                } 
                                catch (Exception e) { 
                                    e.printStackTrace();
                                } 
                                
                            }
                        }).start();
                        
                        return;
                    }
                    case 5: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                logTextArea.setText("Decompresseing Binary File:\n──────────────\nFile Path: " + filePath + "\n\n");
                                
                                String log = controller.decompressBinaryFile(filePath, debug, printFiles).getValue();
                                
                                logTextArea.append(log);
                            }
                        }).start();
                        
                        return;
                    }
                    case 6: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                logTextArea.setText("Deompresseing Folder:\n───────────\nFolder Path: " + filePath + "\n\n");
                                
                                String log = controller.decompressFolder(filePath, debug, printFiles).getValue();
                                
                                logTextArea.append(log);
                            }
                        }).start();
                        
                        return;
                    }
                    
                }
                
            }
        });
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        
        if(e.getSource() == debugCheckBox1 || e.getSource() == debugCheckBox2) {
            
            if(e.getStateChange() == 1) {
                debug = true;  
                
                debugCheckBox1.setSelected(true);
                debugCheckBox2.setSelected(true);
                
                printCheckBox1.setEnabled(true);
                printCheckBox2.setEnabled(true);
            }
            else {
                debug = false;
                
                debugCheckBox1.setSelected(false);
                debugCheckBox2.setSelected(false);
                
                printCheckBox1.setSelected(false);
                printCheckBox2.setSelected(false);
                
                printCheckBox1.setEnabled(false);
                printCheckBox2.setEnabled(false);
            }
            
        }
        else if(e.getSource() == printCheckBox1 || e.getSource() == printCheckBox2) {
            
            if(e.getStateChange() == 1) {
                printFiles = true;
                
                printCheckBox1.setSelected(true);
                printCheckBox2.setSelected(true);
            }
            else {
                printFiles = false;
                
                printCheckBox1.setSelected(false);
                printCheckBox2.setSelected(false);
            }
            
        }
                
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        
        JTabbedPane p = (JTabbedPane)e.getSource();
        if(p.getSelectedIndex() == 1) {
            compress = false;
            setMenuItemsState(false);
        }
        else {
            compress = true;
            if(mode == Mode.text) setMenuItemsState(true);
        }
        
    }
    
    private void setMenuItemsState(boolean state) {
        copy.setEnabled(state);
        cut.setEnabled(state);
        paste.setEnabled(state);
        open.setEnabled(state);
        save.setEnabled(state);
        New.setEnabled(state);  
    }
}
