
package Controller;

import Model.Compressor;
import Model.Decompressor;
import java.util.function.BiConsumer;
import javafx.util.Pair;

public class Controller {
        
    public Controller() {
        
    }
    
    //functions for JavaFX GUI
    
    private BiConsumer<Integer, Integer> progressUpdate;
    
    public Pair<String, String> compressTextFileShowProgress(String filePath) {
        Compressor compressor = new Compressor(this, true);
        return compressor.compressFile(filePath);       
    }
    
    public Pair<String, String> compressBinaryFileShowProgress(String filePath) {
        Compressor compressor = new Compressor(this, false);
        return compressor.compressFile(filePath);
    }
    
    public Pair<String, String> compressFolderShowProgress(String folderPath) {
        Compressor compressor = new Compressor(this, false);
        return compressor.compressFolder(folderPath);
    }
    
    public Pair<String, String> decompressTextFileShowProgress(String filePath) {
        Decompressor decompressor = new Decompressor(this, true);
        return decompressor.decompressFile(filePath);
    }
    
    public Pair<String, String> decompressBinaryFileShowProgress(String filePath) {
        Decompressor decompressor = new Decompressor(this, false);
        return decompressor.decompressFile(filePath);
    }
    
    public Pair<String, String> decompressFolderShowProgress(String folderPath) {
        Decompressor decompressor = new Decompressor(this, false);
        return decompressor.decompressFolder(folderPath);
    }
    
    public void setProgressUpdate(BiConsumer<Integer, Integer> progressUpdate) {
        this.progressUpdate = progressUpdate;
    }
    
    public void updateProgress(int workDone, int totalWork) {
        progressUpdate.accept(workDone, totalWork);
    }

    //functions for Swing GUI
    
    public Pair<String, String> compressTextFile(String filePath, boolean debug, boolean printFiles) {
        Compressor compressor = new Compressor(true, debug, printFiles);
        return compressor.compressFile(filePath);       
    }
    
    public Pair<String, String> compressBinaryFile(String filePath, boolean debug, boolean printFiles) {
        Compressor compressor = new Compressor(false, debug, printFiles);
        return compressor.compressFile(filePath);
    }
    
    public Pair<String, String> compressFolder(String folderPath, boolean debug, boolean printFiles) {
        Compressor compressor = new Compressor(false, debug, printFiles);
        return compressor.compressFolder(folderPath);
    }
    
    public Pair<String, String> decompressTextFile(String filePath, boolean debug, boolean printFiles) {
        Decompressor decompressor = new Decompressor(true, debug, printFiles);
        return decompressor.decompressFile(filePath);
    }
    
    public Pair<String, String> decompressBinaryFile(String filePath, boolean debug, boolean printFiles) {
        Decompressor decompressor = new Decompressor(false, debug, printFiles);
        return decompressor.decompressFile(filePath);
    }
    
    public Pair<String, String> decompressFolder(String folderPath, boolean debug, boolean printFiles) {
        Decompressor decompressor = new Decompressor(false, debug, printFiles);
        return decompressor.decompressFolder(folderPath);
    }
    
    
}
