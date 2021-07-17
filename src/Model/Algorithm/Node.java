
package Model.Algorithm;


public class Node{
        
    private byte data;
    private int frequency;
    
    private Node left;
    private Node right;
        
    public Node(byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }
    
    public Node(int frequency, Node left, Node right) {
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
    
    public Node(byte data) {
        this.data = data;
        this.left = null;
        this.right = null;
        this.frequency = 0;
    }
    

    public byte getData() {
        return data;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    
    public void incrementFrequency() {
        frequency++;
    }
    
    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
    
}
