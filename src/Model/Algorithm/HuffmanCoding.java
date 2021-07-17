
package Model.Algorithm;

import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanCoding {

    private HashMap<Byte, String> generatedHuffmanCodes;
    
    private StringBuilder encodedTreeString;
    
    private HashMap<String, Byte> decodedHuffmanCodes;
    
    private final StringBuilder log;
    
    private final boolean debug;
        
    public HuffmanCoding(StringBuilder log, Boolean debug) {
        this.log = log;
        this.debug = debug;
    }
    
    public HashMap<Byte, String> generateHuffmanCodes(HashMap<Byte, Integer> entries, boolean isText, boolean printEncodedTree) {
        
        Node root = buildHuffmanTree(entries);
        
        encodedTreeString = new StringBuilder("");
        generatedHuffmanCodes = new HashMap<>(); 
        
        traverseGeneratedTree(root, "", 8);
        
        if(debug) {
            if(isText) printCharacterCodes(printEncodedTree);
            else printByteCodes(printEncodedTree);
        }
        
        return generatedHuffmanCodes;
    }
    
    private Node buildHuffmanTree(HashMap<Byte, Integer> entries) {
        
        PriorityQueue<Node> minHeap = new PriorityQueue<>((Node n1, Node n2) -> n1.getFrequency() - n2.getFrequency());
        
        for(Byte b : entries.keySet()) {
            minHeap.add(new Node(b, entries.get(b)));
        }
        
        Node root = null;
        
        while(minHeap.size() > 1) {
            Node left = minHeap.poll();
            Node right = minHeap.poll();
            
            Node newNode = new Node(left.getFrequency() + right.getFrequency(), left, right);
            
            root = newNode;
            
            minHeap.add(newNode);
        }
        
        return root;
    }
    
    private void traverseGeneratedTree(Node node, String code, int codeLength) {
        
        if(node.getLeft() == null) {            
            generatedHuffmanCodes.put(node.getData(), code);
            encodedTreeString.append('1');
            encodedTreeString.append(formatZeros(byteToBitString(node.getData()), codeLength));
        }
        else {
            encodedTreeString.append('0');
            traverseGeneratedTree(node.getLeft(), code + "0", codeLength);
            traverseGeneratedTree(node.getRight(), code + "1", codeLength);
        }
        
    }
    
    public HashMap<String, Byte> decodeHuffmanTree(String encodedTree, boolean isText) {
        this.encodedTreeString = new StringBuilder(encodedTree);
        
        decodedHuffmanCodes = new HashMap<>();
        
        decodeNode(8, "");
        
        if(debug) {
            logLine("Decoded Huffman tree:");
            logLine("───────────");
            
            if(isText) {
                for(String code : decodedHuffmanCodes.keySet()) {
                    char c = (char)decodedHuffmanCodes.get(code).byteValue();
                    
                    if(c == '\n')
                        logLine("\\n\t:  " + code);
                    else if(c == '\t')
                        logLine("\\t\t:  " + code);
                    else if(c == '\r')
                        logLine("\\r\t:  " + code);
                    else
                        logLine(c + "\t:  " + code);
                }
            }
            else {
                for(String code : decodedHuffmanCodes.keySet()) {
                    logLine(decodedHuffmanCodes.get(code) + "\t:  " + code);
                }          
            }
            logLine("────────────────────────────────────────────────────────────");
        }
        
        return decodedHuffmanCodes;
    }
    
    private void decodeNode(int codeLength, String code) {
        char c = encodedTreeString.charAt(0);
        encodedTreeString.deleteCharAt(0);
                
        if(c == '1') {
            byte data = bitStringToByte(encodedTreeString.substring(0, codeLength));
            encodedTreeString.delete(0, codeLength);
            decodedHuffmanCodes.put(code, data);
        }
        else {
            decodeNode(codeLength, code + "0");     //left
            decodeNode(codeLength, code + "1");     //right
        }     
    }
    
    
    private void printCharacterCodes(boolean printEncodedTree) {
        logLine("▪ Huffman Codes:");
        logLine("─────────");
        
        for(Byte b : generatedHuffmanCodes.keySet()) {
            char c = (char)b.byteValue();
            if(c == '\n')
                logLine("\\n\t:  " + generatedHuffmanCodes.get(b));
            else if(c == '\t')
                logLine("\\t\t:  " + generatedHuffmanCodes.get(b));
            else if(c == '\r')
                logLine("\\r\t:  " + generatedHuffmanCodes.get(b));
            else
                logLine(c + "\t:  " + generatedHuffmanCodes.get(b));
        }
        
        if(printEncodedTree) {
            logLine("\nEncoded Tree:");
            logLine(encodedTreeString.toString());
            
            logLine("────────────────────────────────────────────────────────────");
        }
              
    }
    
    private void printByteCodes(boolean printEncodedTree) {
        logLine("▪ Huffman Codes:");
        logLine("─────────");
        
        for(Byte b : generatedHuffmanCodes.keySet()) {
            logLine(b + "\t:  " + generatedHuffmanCodes.get(b));
        }
        
        if(printEncodedTree) {
            logLine("\nEncoded Tree:");
            logLine(encodedTreeString.toString());
            
            logLine("────────────────────────────────────────────────────────────");
        }
        
    }

    public String getEncodedTreeString() {
        return encodedTreeString.toString();
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
