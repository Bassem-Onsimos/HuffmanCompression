
package Model;

import Model.Algorithm.HuffmanCoding;
import Controller.Controller;
import java.io.IOException;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.util.Pair;

public class Decompressor {
    
    private Controller controller;
    
    private final HuffmanCoding huffman;
    
    private final StringBuilder log;
    
    private final boolean isText;
    private final boolean debug;
    private final boolean printFiles;
    
    private final boolean showProgress;
    
    private boolean isFolder;
    
    private long startTime;
    
    public Decompressor(boolean isText, boolean debug, boolean printFiles) {
        this.log = new StringBuilder();
        this.huffman = new HuffmanCoding(log, debug);
        this.isText = isText;
        this.debug = debug;
        this.printFiles = printFiles;
        this.showProgress = false;
    }
    
    public Decompressor(Controller controller, boolean isText) {
        this.controller = controller;
        this.log = new StringBuilder();
        this.huffman = new HuffmanCoding(log, true);
        this.isText = isText;
        this.debug = true;
        this.printFiles = false;
        this.showProgress = true;
    }

    public Pair<String, String> decompressFile(String filePath) {
        
        isFolder = false;
        
        startTime = System.nanoTime();
        
        String outputFilePath = Paths.get(filePath).getParent().toString();
        
        StringBuilder inputBits = new StringBuilder(readCompressedFile(filePath));
        
        if(showProgress) controller.updateProgress(1, 5);
        
        //
        
        byte[] byteArray;
        
            int padding = Integer.parseInt(inputBits.substring(0, 3), 2);    
            inputBits.delete(0, 3);
            
            StringBuilder name = new StringBuilder("");
                        
            while(true) {
                char c = byteToChar(bitStringToByte(inputBits.substring(0, 8)));
                inputBits.delete(0, 8);
                                
                if(c == '\n') {
                    break;
                }
                
                name.append(c);
            }
            
            outputFilePath += name.toString();
            
        if(inputBits.length() - padding > 0) {

            HashMap<String, Byte> huffmanCodes = huffman.decodeHuffmanTree(inputBits.toString(), isText);

            inputBits.delete(0, 10 * huffmanCodes.size() - 1);

            StringBuilder buffer = new StringBuilder("");          
            ArrayList<Byte> output = new ArrayList<>();

            for(int i = 0; i < inputBits.length() - padding; i++) {

                buffer.append(inputBits.charAt(i));

                if(huffmanCodes.containsKey(buffer.toString())) {
                    output.add(huffmanCodes.get(buffer.toString()));
                    buffer.delete(0, buffer.length());
                }            
            }

            byteArray = new byte[output.size()];
            int i = 0;

            for(byte b : output) {
                byteArray[i++] = b;
            }
         
        }
        else {
            byteArray = new byte[0];
        }
        
        if(showProgress) controller.updateProgress(4, 5);
                    
        outputFilePath = writeDecompressedFile(byteArray, outputFilePath);
        
        double time = (System.nanoTime() - startTime)/1000000000.0;
        
        logLine("Running Time = " + time + " seconds");
        
        logLine("\n────────────────────────────────────────────────────────────");
        
        if(showProgress) controller.updateProgress(5, 5);
        
        return new Pair(outputFilePath, log.toString());
    }
    
    public Pair<String, String> decompressFolder(String filePath) {
        
        isFolder = true;
        
        startTime = System.nanoTime();
        
        String outputFolderPath = Paths.get(filePath).getParent().toString();
        
        try {
            if(!Files.exists(Paths.get(outputFolderPath))) Files.createDirectory(Paths.get(outputFolderPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        StringBuilder inputBits = new StringBuilder(readCompressedFile(filePath));
        
        int totalWork = inputBits.length();
        int workDone = 0;
                
        int folderPadding = Integer.parseInt(inputBits.substring(0, 3), 2);    
        inputBits.delete(0, 3);
        workDone += 3;
        
        StringBuilder folderName = new StringBuilder("");

        while (true) {
            char c = byteToChar(bitStringToByte(inputBits.substring(0, 8)));
            inputBits.delete(0, 8);
            workDone += 8;

            if (c == '\n') {
                break;
            }

            folderName.append(c);
        }
        
        outputFolderPath += folderName.toString();
        String tempFolderPath = outputFolderPath;
        
        try {
            int i = 2;
            while(Files.exists(Paths.get(outputFolderPath))) {
                outputFolderPath = tempFolderPath + " (" + i + ")";
                i++;
            }
            
            
            Files.createDirectory(Paths.get(outputFolderPath));
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(showProgress) controller.updateProgress(workDone, totalWork);
        
        while(inputBits.length() - folderPadding > 0) {
         
            int sectionSize = Integer.parseInt(inputBits.substring(0, 5 * 8), 2);
            inputBits.delete(0, 5 * 8);
            
            logLine("────────────────────────────────────────────────────────────");
            
            if(debug) logLine("\n\nSection Size = " + sectionSize + " bytes");
            
            sectionSize *= 8;
            
            if(debug) logLine("\nSection Length = " + sectionSize + " bits");
            
            workDone += sectionSize;
            
            int padding = Integer.parseInt(inputBits.substring(0, 3), 2);    
            inputBits.delete(0, 3);
            sectionSize -=3;
            
            StringBuilder name = new StringBuilder("");
            
            boolean emptyFolder = false;
            
            while(true) {
                char c = byteToChar(bitStringToByte(inputBits.substring(0, 8)));
                inputBits.delete(0, 8);
                sectionSize -=8;
                                
                if(c == '\n') {
                    break;
                }
                else if (c == '\r') {
                    emptyFolder = true;
                    break;
                }
                
                name.append(c);
            }
            
            String fileName = name.toString();
            
            if(!debug) logLine("");
            
            if(emptyFolder) {
                                                
                String outputFilePath = outputFolderPath + fileName;

                String subFolderPath = outputFilePath.substring(0, outputFilePath.lastIndexOf("/"));
                
                logLine("\nFolder Name (empty) = " + fileName + "\n\n");
                
                try {
                    if(!Files.exists(Paths.get(subFolderPath))) Files.createDirectories(Paths.get(subFolderPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                logLine("Folder Created!");
                
                logLine("\nFolder Path: " + outputFilePath + "\n");
                
                inputBits.delete(0, sectionSize);
            }
            else {
            
                logLine("\nFile Name = " + fileName + "\n\n");

                String outputFilePath = outputFolderPath + fileName;

                String subFolderPath = outputFilePath.substring(0, outputFilePath.lastIndexOf("/"));

                try {
                    if(!Files.exists(Paths.get(subFolderPath))) Files.createDirectories(Paths.get(subFolderPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<Byte> output = new ArrayList<>();

                if(sectionSize - padding > 0) {
                    HashMap<String, Byte> huffmanCodes = huffman.decodeHuffmanTree(inputBits.toString(), false);

                    inputBits.delete(0, 10 * huffmanCodes.size() - 1);
                    sectionSize -= (10 * huffmanCodes.size() - 1);

                    StringBuilder buffer = new StringBuilder("");          

                    for(int i = 0; i < sectionSize - padding; i++) {

                        buffer.append(inputBits.charAt(i));

                        if(huffmanCodes.containsKey(buffer.toString())) {
                            output.add(huffmanCodes.get(buffer.toString()));
                            buffer.delete(0, buffer.length());
                        }            
                    }
                }

                inputBits.delete(0, sectionSize);

                byte[] byteArray = new byte[output.size()];
                int i = 0;

                for(byte b : output) {
                    byteArray[i++] = b;
                }

                writeDecompressedFile(byteArray, outputFilePath);
            
            }
            
            logLine("\n────────────────────────────────────────────────────────────");
            
            if(showProgress) controller.updateProgress(workDone, totalWork);
        }
        
        logLine("────────────────────────────────────────────────────────────");
        
        double time = (System.nanoTime() - startTime)/1000000000.0;
        
        logLine("\nRunning Time = " + time + " seconds");
        
        logLine("\n────────────────────────────────────────────────────────────");
        
        if(showProgress) controller.updateProgress(totalWork, totalWork);
        
        return new Pair(outputFolderPath, log.toString());
    }
    
    private String readCompressedFile(String filePath) {
        
        StringBuilder inputBits = new StringBuilder("");

        try {
            
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));

            StringBuilder input = new StringBuilder("");

            for(int i = 0; i < bytes.length; i++) {
                input.append(Byte.toString(bytes[i]));
                inputBits.append(byteToBitString(bytes[i]));
            }
            
            if(printFiles) {
                logLine("\n▪ Read Data:");
                logLine("──────");

                logLine(input.toString() + "\n");
                logLine(inputBits.toString());

                
            }
            logLine("────────────────────────────────────────────────────────────");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return inputBits.toString();
    }
  
    private String writeDecompressedFile(byte[] output, String outputFilePath) {
        
        try {
            String tempFilePath = outputFilePath.substring(0, outputFilePath.lastIndexOf("."));
            String extension = outputFilePath.substring(outputFilePath.lastIndexOf("."), outputFilePath.length());
            
            int i = 2;
            while(Files.exists(Paths.get(outputFilePath))) {
                outputFilePath = tempFilePath + " (" + i + ")" + extension;
                i++;
            }
                        
            Files.write(Paths.get(outputFilePath), output);         
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(printFiles) {        
            logLine("▪ Decompressed File:");
            logLine("──────────");
            if(isText) {
                log(new String(output));
            }
            else  {
                for(int i = 0; i < output.length; i++){
                    log(Byte.toString(output[i]));
                }         
            }            
        }
        
        if(isFolder) {
            if(printFiles) logLine("\n────────────────────────────────────────────────────────────\n\n");
            
            if(debug && !printFiles) logLine("\n");
            
            logLine("Done!");
            logLine("\nOutput File Path: " + outputFilePath + "\n");
        }
        else {
            if(printFiles) logLine("\n────────────────────────────────────────────────────────────");

            logLine("\nDone!");
            logLine("\nOutput File Path: " + outputFilePath + "\n");
        }
        
        return outputFilePath;
    }
    
    private String byteToBitString(byte b) {
        return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    }
    
    private byte bitStringToByte(String s) {
        Integer byteAsInt = Integer.parseUnsignedInt(s, 2);
        return byteAsInt.byteValue();
    }
    
    private char byteToChar(byte b) {
        return (char)b;
    }
    
    private void logLine(String s) {
        log.append(s);
        log.append("\n");
    } 
    
    private void log(String s) {
        log.append(s);
    } 
    
    /*
    private void logLine(String s) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    log.append(s + "\n");
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    
    private void log(String s) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    log.append(s);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    */
}
