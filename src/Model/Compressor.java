
package Model;

import Model.Algorithm.HuffmanCoding;
import Controller.Controller;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

public class Compressor {
    
    private Controller controller;
    
    private final HuffmanCoding huffman;
    
    private final StringBuilder log;
    
    private final boolean isText;
    private final boolean debug;
    private final boolean printFiles;
    
    private final boolean showProgress;
    
    private boolean isFolder;
    
    private long startTime;
    
    public Compressor(boolean isText, boolean debug, boolean printFiles) {       
        this.log = new StringBuilder();
        this.huffman = new HuffmanCoding(log, debug);      
        this.isText = isText;
        this.debug = debug;
        this.printFiles = printFiles;
        this.showProgress = false;
    }
    
    public Compressor(Controller controller, boolean isText) {       
        this.controller = controller;
        this.log = new StringBuilder();
        this.huffman = new HuffmanCoding(log, true);      
        this.isText = isText;
        this.debug = true;
        this.printFiles = false;
        this.showProgress = true;
    }
    
    public Pair<String, String> compressFile(String originalFilePath) {
        
        isFolder = false;
        
        startTime = System.nanoTime();
        
        byte[] originalData = readFile(originalFilePath);
        
        if(showProgress) controller.updateProgress(1, 5);
        
        if(originalData.length == 0) {
            return new Pair(writeCompressedFile(null, "", originalData, originalFilePath), log.toString());
        }
                
        HashMap<Byte, Integer> entries = computeFrequency(originalData);
        
        if(showProgress) controller.updateProgress(2, 5);
        
        HashMap<Byte, String> huffmanCodes = huffman.generateHuffmanCodes(entries, isText, printFiles);
                
        String encodedTree = huffman.getEncodedTreeString();
        
        if(showProgress) controller.updateProgress(3, 5);
        
        return new Pair(writeCompressedFile(huffmanCodes, encodedTree, originalData, originalFilePath), log.toString());
        
    }
    
    private byte[] readFile(String filePath) {
        byte[] data = null;
                
        try {
            data = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return data;
    }
    
    //compined printing with computing frequency to optimize debug mode
    private HashMap<Byte, Integer> computeFrequency(byte[] data) {
        
        HashMap<Byte, Integer> entries = new HashMap<>();
        
        if(printFiles) {           
            logLine("▪ Original File:");
            logLine("───────");
            
            if(isText) {                
                log(new String(data));
                logLine("\n────────────────────────────────────────────────────────────");
                
                for(int i = 0; i < data.length; i++) {
                    if(entries.containsKey(data[i])) {
                        entries.replace(data[i], entries.get(data[i]) + 1);
                    }
                    else {
                        entries.put(data[i], 1);
                    }                    
                }
               
                printCharacterFrequency(entries);               
            }
            else {
                for(int i = 0; i < data.length; i++) {
                    if(entries.containsKey(data[i])) {
                        entries.replace(data[i], entries.get(data[i]) + 1);
                    }
                    else {
                        entries.put(data[i], 1);
                    }
                    
                    log(Byte.toString(data[i]));
                }
                
                logLine("\n────────────────────────────────────────────────────────────");
                printByteFrequency(entries);
            }
            
        }
        else {           
            for(int i = 0; i < data.length; i++) {
                if(entries.containsKey(data[i])) {
                    entries.replace(data[i], entries.get(data[i]) + 1);
                }
                else {
                    entries.put(data[i], 1);
                }
            }    
            
            if(debug) {
                if(isText) printCharacterFrequency(entries); 
                else printByteFrequency(entries);
            }
        }
        
        return entries;
    }

    private String writeCompressedFile(HashMap<Byte, String> huffmanCodes, String encodedTree, byte[] data, String originalFilePath) {
        
        String fileName = Paths.get(originalFilePath).getFileName().toString();
        
        fileName = originalFilePath.substring(originalFilePath.length() - fileName.length() - 1, originalFilePath.length());
                
        String tempFilePath = originalFilePath.substring(0, originalFilePath.lastIndexOf("."));
        String extension;
        
        if(isText) extension = ".HCT";
        else extension = ".HCB";
        
        String outputFilePath = tempFilePath + extension;
                    
        int i = 2;
        while(Files.exists(Paths.get(outputFilePath))) {
            outputFilePath = tempFilePath + " (" + i + ")" + extension;
            i++;
        }
               
        StringBuilder body = new StringBuilder("");
                
        for(i = 0; i < data.length; i++) {
            body.append(huffmanCodes.get(data[i]));           
        }
                
        String header = charStringToBitString(fileName + "\n") + encodedTree;
        
        if(showProgress) controller.updateProgress(4, 5);
        
        int nCompressedBytes = writeOutputFile(formatOutputFile(header, body.toString()), outputFilePath);
        
        String originalSize = computeSize(data.length);
        String compressedSize = computeSize(nCompressedBytes);
        
        double compressionRatio = (double)nCompressedBytes/data.length;
        
        logLine("\nOriginal File Size = " + originalSize);
        logLine("Compressed File Size = " + compressedSize);
        logLine("Compression Ratio (Original : Compressed) = 1 : " + Math.round(compressionRatio * 100.0) / 100.0 );
        
        logLine("\n\n────────────────────────────────────────────────────────────\n\n");
        
        if(showProgress) controller.updateProgress(5, 5);
        
        return outputFilePath;
    }
    
    private String formatOutputFile(String header, String body) {

        int originalLength = body.length() + header.length() + 3;

        while ((body.length() + header.length() + 3) % 8 != 0) {
            body += '0';
        }

        int paddedLength = body.length() + header.length() + 3;

        int padding = paddedLength - originalLength;

        header = formatZeros(Integer.toBinaryString(padding), 3) + header;

        if(printFiles) {
            logLine("Header:");
            logLine(header);

            logLine("\nBody:");
            logLine(body);
        }

        body = header + body;
        
        return body;
    }
    
    public Pair<String, String> compressFolder(String folderPath) {
        
        isFolder = true;
        
        startTime = System.nanoTime();
        
        String outputFilePath = folderPath + ".HCF";
        
        int i = 2;
        while(Files.exists(Paths.get(outputFilePath))) {
            outputFilePath = folderPath + " (" + i + ").HCF";
            i++;
        }
        
        String folderName = Paths.get(folderPath).getFileName().toString();
        
        folderName = folderPath.substring(folderPath.length() - folderName.length() - 1, folderPath.length());
        
        StringBuilder output = new StringBuilder(charStringToBitString(folderName + "\n"));
        
        int originalNumberOfBytes = 0;
        
        if(debug) logLine("\n────────────────────────────────────────────────────────────");
        
        int totalWork = 0;
        
        try {
            
            ArrayList<Path> files = new ArrayList<>();
            
            Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
               
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(!attrs.isDirectory()){
                         files.add(file);
                    }

                    return FileVisitResult.CONTINUE;
                } 
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if(Files.isDirectory(dir)) {
                        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                            if(!dirStream.iterator().hasNext()) {
                                files.add(dir);
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }  
            });
            
            totalWork = files.size() + 1;
            int workDone = 0;
            
            for (Path path : files) {
                    
                        String filePath = URLDecoder.decode(path.toUri().toURL().getPath(), "UTF-8");
                        String fileName = filePath.substring(folderPath.length());
                
                        if (!Files.isDirectory(path)) {
                        
                            if(!fileName.endsWith("/.DS_Store")) {        //for Mac/OSX

                                logLine("────────────────────────────────────────────────────────────\n");

                                if(debug) logLine("");
                                
                                logLine("File Name: " + fileName + "\n");

                                byte[] originalData = readFile(filePath);
                                
                                if(originalData.length == 0) {
                                    output.append(makeCompressedFolderItem(null, "", originalData, fileName + '\n'));
                                    continue;
                                }

                                HashMap<Byte, Integer> entries = computeFrequency(originalData);

                                HashMap<Byte, String> huffmanCodes = huffman.generateHuffmanCodes(entries, false, printFiles);

                                String encodedTree = huffman.getEncodedTreeString();

                                output.append(makeCompressedFolderItem(huffmanCodes, encodedTree, originalData, fileName + '\n'));

                                originalNumberOfBytes += originalData.length;
                            }

                        }
                        else {
                            logLine("────────────────────────────────────────────────────────────\n");

                            if(debug) logLine("");
                            
                            logLine("Folder Name (empty): " + fileName + "\n");
                            
                            byte[] empty = new byte[0];
                            output.append(makeCompressedFolderItem(null, "", empty, fileName + '\r'));
                        }
                        
                        if(showProgress) controller.updateProgress(++workDone, totalWork);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(printFiles) logLine("────────────────────────────────────────────────────────────\n");
        int nCompressedBytes = writeOutputFile(formatFolder(output.toString()), outputFilePath);
        
        
        String originalSize = computeSize(originalNumberOfBytes);
        String compressedSize = computeSize(nCompressedBytes);
        double compressionRatio = (double)nCompressedBytes/originalNumberOfBytes;
        
        logLine("\nOriginal File Size = " + originalSize);
        logLine("Compressed File Size = " + compressedSize);
        logLine("Compression Ratio (Original : Compressed) = 1 : " + Math.round(compressionRatio * 100.0) / 100.0 );
        
        logLine("\n\n────────────────────────────────────────────────────────────\n\n");
        
        if(showProgress) controller.updateProgress(totalWork, totalWork);
        
        return new Pair(outputFilePath, log.toString());
    }
    
    private String makeCompressedFolderItem(HashMap<Byte, String> huffmanCodes, String encodedTree, byte[] data, String itemName) {
                       
        StringBuilder body = new StringBuilder("");
            
        for(int i = 0; i < data.length; i++) {
            body.append(huffmanCodes.get(data[i]));           
        }
            
        String header = charStringToBitString(itemName) + encodedTree;
                        
        String output = formatOutputFile(header, body.toString());
        
        output = formatZeros(Integer.toBinaryString(output.length()/8), 5 * 8) + output;
        
        if(debug) {
            logLine("────────────────────────────────────────────────────────────");
            logLine("\nSection Size (Original) = " + computeSize(data.length) );
            logLine("\nSection Size (Compressed) = " + computeSize(output.length()/8) );
            logLine("\n\n────────────────────────────────────────────────────────────");
        }
                        
        return output;
    }
    
    private String formatFolder(String output) {
        int originalLength = output.length() + 3;

        while ((output.length() + 3) % 8 != 0) {
            output += '0';
        }

        int paddedLength = output.length() + 3;

        int padding = paddedLength - originalLength;

        output = formatZeros(Integer.toBinaryString(padding), 3) + output;
        
        return output;
    }
    
    
    private int writeOutputFile(String output, String outputFilePath) {
                
        byte[] bytes = new byte[output.length() / 8];
        StringBuilder bytesString = new StringBuilder("");
        int index = 0;
        
        if(debug) {
            for (int i = 0; i < output.length() / 8; i++) {
                bytes[i] = bitStringToByte(output.substring(index, index + 8));
                bytesString.append(Byte.toString(bytes[i]));
                index += 8;
            }
        }
        else {
            for (int i = 0; i < output.length() / 8; i++) {
                bytes[i] = bitStringToByte(output.substring(index, index + 8));
                index += 8;
            }
        }
        
        if(printFiles) {
            logLine("\nCompressed File:");
            logLine(output + "\n");

            logLine(bytesString.toString());

            logLine("\nTotal Length: " + output.length() + " bits");

        }
        
        try {
            Files.write(Paths.get(outputFilePath), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        long endTime = System.nanoTime();
        
        logLine("────────────────────────────────────────────────────────────\n\n");
        
        logLine("Done!");
        logLine("\nOutput File Path: " + outputFilePath);
        
        double runningTime = (endTime - startTime)/1000000000.0;
        
        logLine("\nRunning Time = " + runningTime + " seconds");
        
        return bytes.length;
    }
    
    private String computeSize(int nBytes) {

        double size = nBytes;

        if (size < 1024) {
            //bytes
            return (Math.round(size * 10.0) / 10.0) + " B";
        }

        size /= 1024.0;

        if (size < 1024) {
            //Kilo bytes
            return (Math.round(size * 10.0) / 10.0) + " KB";
        }

        size /= 1024.0;

        if (size < 1024) {
            //Mega bytes
            return (Math.round(size * 10.0) / 10.0) + " MB";
        }

        size /= 1024.0;

        if (size < 1024) {
            //Giga bytes
            return (Math.round(size * 10.0) / 10.0) + " GB";
        }

        size /= 1024.0;
        
        //Tera Bytes
        return (Math.round(size * 10.0) / 10.0) + " TB";
        
    }
    
    private void printCharacterFrequency(HashMap<Byte, Integer> entries) {
        if(!printFiles) logLine("────────────────────────────────────────────────────────────");
        logLine("▪ Character Frequency:");
        logLine("───────────");
        
        for(Byte b : entries.keySet()) {
            char c = (char)b.byteValue();
            
            if(c == '\n')
                logLine("\\n\t:  " + entries.get(b));
            else if(c == '\t')
                logLine("\\t\t:  " + entries.get(b));
            else if(c == '\r')
                logLine("\\r\t:  " + entries.get(b));
            else
                logLine(c + "\t:  " + entries.get(b));
        }      
        
        logLine("────────────────────────────────────────────────────────────");
    }
    
    private void printByteFrequency(HashMap<Byte, Integer> entries) {
        if(!printFiles && !isFolder) logLine("────────────────────────────────────────────────────────────");
        logLine("▪ Byte Frequency:");
        logLine("─────────");
        
        for(Byte b : entries.keySet()) {
            logLine(b + "\t:  " + entries.get(b));
        }       
        
        logLine("────────────────────────────────────────────────────────────");
    }
    

    private String formatZeros(String str, int requiredLength) {
        while(str.length() < requiredLength) str = "0" + str;
        
        return str;
    }
    
    private byte bitStringToByte(String s) {
        Integer byteAsInt = Integer.parseUnsignedInt(s, 2);
        return byteAsInt.byteValue();
    }
    
    private String byteToBitString(byte b) {
        return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    }
    
    private byte charToByte(char c) {
        Integer i = Integer.parseUnsignedInt(Integer.toBinaryString(c), 2);
        return i.byteValue();
    }
    
    private String charStringToBitString(String s) {
        
        String bitString = "";
        
        for(int i = 0; i < s.length(); i++) {
            bitString += formatZeros( byteToBitString(charToByte(s.charAt(i))) , 8);
        }
        
        return bitString;
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
