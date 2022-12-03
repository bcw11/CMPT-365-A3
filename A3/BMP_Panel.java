package A3;

import javax.swing.*;
import org.w3c.dom.css.RGBColor;
import java.awt.image.*;
import java.io.IOException;
import java.awt.*;
import java.util.*;
import java.util.stream.*;
import java.util.HashMap;


class BMP_Panel extends JPanel {
    // image
    BufferedImage image;

    // number of bits
    int imgBits;
    int hufBits;
    int jpgBits;

    // panel width and height
    int width;
    int height;

    // current image set 
    int imgSet = 0;

    private static JLabel L1;
    private static JLabel L2;

    public BMP_Panel(BufferedImage image,int width,int height) throws IOException {
        // setting panel width and height
        this.width = width;
        this.height = height;
        this.image = image;
        
        // number of bits in original image 
        imgBits = image.getColorModel().getPixelSize()*(image.getWidth()*image.getHeight());

        // number of bits in huffman encoding
        hufBits = huffmanEncoding();

        // number of bits in jpeg ls encoding 
        jpgBits = jpegEncoding();


        // setting panel layout to null to use absolute positioning
        setLayout(null);

        // outputting huffman compression ratio 
        L1 = new JLabel("(1) Huffman Compression Ratio: "+Float.toString((float)imgBits/(float)hufBits));
        Dimension D1 = L1.getPreferredSize();
        L1.setBounds((width/2)+10,height/2,(int)D1.getWidth(),(int)D1.getHeight());
        add(L1);

        // outputting JPEG LS compression ratio 
        L2 = new JLabel("(2) JPEG + Huffman Compression Ratio: "+Float.toString((float)imgBits/(float)jpgBits));
        Dimension D2 = L2.getPreferredSize();
        L2.setBounds((width/2)+10,(height/2)+20,(int)D2.getWidth(),(int)D2.getHeight());
        add(L2);
    }


    private int huffmanEncoding(){
        HashMap<String,Integer> map = new HashMap<String,Integer>();

        // initalizing map with frequency of symbols 
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));

                // rgb symbol 
                String s = Integer.toString(c.getBlue())+Integer.toString(c.getGreen())+Integer.toString(c.getRed());

                // putting key into dictionary and incrementing if exists
                if(!map.containsKey(s))
                    map.put(s,1);
                else
                    map.put(s,map.get(s)+1);
            }
        }
        
        // adapted from https://www.javatpoint.com/huffman-coding-java#:~:text=above%20two%20steps.-,Huffman%20Tree,may%20have%20the%20same%20frequency.
        // creating huffman tree with priority queue
        PriorityQueue<Node> tree = new PriorityQueue<>(Comparator.comparingInt(node -> node.freq));  
        for (String key: map.keySet()){  
            tree.add(new Node(key, map.get(key), null, null));  
        }  
        while(tree.size() > 1){  
            // removing 2 nodes with the lowest frequency 
            Node child1 = tree.poll();  
            Node child2 = tree.poll();  
            
            // creating parent node to child nodes
            tree.add(new Node(null, child1.freq + child2.freq, child1, child2));  
        }  

        // finding huffman code of symbols
        Map<String, Integer> huffmanCodes = new HashMap<>();  
        codeword(huffmanCodes, tree.peek(), "");  

        int bits = 0; 
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));

                // rgb symbol 
                String s = Integer.toString(c.getBlue())+Integer.toString(c.getGreen())+Integer.toString(c.getRed());

                // getting number of bits in code 
                bits += huffmanCodes.get(s);
            }
        }
   
        return bits;
    }

    private int jpegEncoding(){
        // holds predicted values 
        BufferedImage predictedValues = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        EncodedSymbol[][] encodedSymbols = new EncodedSymbol[image.getWidth()][image.getHeight()];

        // holds frequency of symbols 
        HashMap<String,Integer> map = new HashMap<String,Integer>();

        // choosing predictors 
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));
                int predictor = 0;

                // very first pixel 
                if((x-1) == -1 && (y-1) == -1){
                    // setting predictor P0 
                    predictor = 0;

                    // setting predicted value 
                    predictedValues.setRGB(x, y, c.getRGB());

                    // setting encoded symbol
                    encodedSymbols[x][y] = new EncodedSymbol(c.getRed(),c.getGreen(),c.getBlue());
                }
                // pixels in first row 
                else if((y-1) == -1){
                    // predictor P1 
                    predictor = 1;

                    Color P1 = new Color(predictedValues.getRGB(x-1, y));
                    predictedValues.setRGB(x, y, P1.getRGB());
                }
                // pixels in first column 
                else if((x-1) == -1){
                    // predictor P2 
                    predictor = 2;

                    Color P2 = new Color(predictedValues.getRGB(x, y-1));
                    predictedValues.setRGB(x, y, P2.getRGB());
                }
                // normal pixels 
                else{
                    Color[] P = new Color[7];
                    
                    // calculating predictors 
                    P[0] = new Color(predictedValues.getRGB(x-1, y));
                    P[1] = new Color(predictedValues.getRGB(x, y-1));
                    P[2] = new Color(predictedValues.getRGB(x-1, y-1));

                    P[3] = new Color(P[0].getRed()+P[1].getRed()-P[2].getRed(),
                                    P[0].getGreen()+P[1].getGreen()-P[2].getGreen(),
                                    P[0].getBlue()+P[1].getBlue()-P[2].getBlue());

                    P[4] = new Color(P[0].getRed()+(P[1].getRed()-P[2].getRed())/2,
                                    P[0].getGreen()+(P[1].getGreen()-P[2].getGreen())/2,
                                    P[0].getBlue()+(P[1].getBlue()-P[2].getBlue())/2);

                    P[5] = new Color(P[1].getRed()+(P[0].getRed()-P[2].getRed())/2,
                                    P[1].getGreen()+(P[0].getGreen()-P[2].getGreen())/2,
                                    P[1].getBlue()+(P[0].getBlue()-P[2].getBlue())/2);

                    P[6] = new Color((P[0].getRed()+P[1].getRed())/2,
                                    (P[0].getGreen()+P[1].getGreen())/2,
                                    (P[0].getBlue()+P[1].getBlue())/2);

                    // determining which predictor to use 
                    int lowest = Math.abs(P[0].getRed());
                    predictor = 10;
                    for(int i = 1; i < 7; i++){
                        if(Math.abs(P[i].getRed()) < lowest){
                            lowest = Math.abs(P[i].getRed());
                            predictor = i+10;
                        }
                    }
                    
                    // setting predicted value 
                    predictedValues.setRGB(x, y, P[10-predictor].getRGB());
                }

                // predictor 
                String p =  Integer.toString(predictor);

                // predictor + rgb symbol 
                String s = p + " " + Integer.toString(c.getBlue())+Integer.toString(c.getGreen())+Integer.toString(c.getRed());
                

                System.out.println(s);

                // putting key into dictionary and incrementing if exists
                if(!map.containsKey(s))
                    map.put(s,1);
                else
                    map.put(s,map.get(s)+1);
            }
        }


        // adapted from https://www.javatpoint.com/huffman-coding-java#:~:text=above%20two%20steps.-,Huffman%20Tree,may%20have%20the%20same%20frequency.
        // creating huffman tree with priority queue
        PriorityQueue<Node> tree = new PriorityQueue<>(Comparator.comparingInt(node -> node.freq));  
        for (String key: map.keySet()){  
            tree.add(new Node(key, map.get(key), null, null));  
        }  
        while(tree.size() > 1){  
            // removing 2 nodes with the lowest frequency 
            Node child1 = tree.poll();  
            Node child2 = tree.poll();  
            
            // creating parent node to child nodes
            tree.add(new Node(null, child1.freq + child2.freq, child1, child2));  
        }  

        // finding huffman code of symbols
        Map<String, Integer> huffmanCodes = new HashMap<>();  
        codeword(huffmanCodes, tree.peek(), "");  

        int bits = 0; 
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color c = new Color(image.getRGB(x, y));

                // rgb symbol 
                String s = Integer.toString(c.getBlue())+Integer.toString(c.getGreen())+Integer.toString(c.getRed());

                // getting number of bits in code 
                bits += huffmanCodes.get(s);
            }
        }
   
        return bits;
    }


    

    // finding huffman codeword length for each symbol
    public void codeword(Map<String, Integer> huffmanCode, Node node, String str)  {  

        if (node == null) {  return;  }  
    
        // when leaf put code length of each symbol
        if (node.left == null && node.right == null){  
            int codeLen = str.length();
            if(codeLen == 0)
                codeLen = 1;
            huffmanCode.put(node.key, codeLen);  
        }  

        // all left nodes are 0 
        codeword(huffmanCode, node.left, str + '0');  
        // all right nodes are 1
        codeword(huffmanCode, node.right, str + '1');  
    }  

    class Node {  
        String key;  
        Integer freq;  
        Node left;   
        Node right;   
      
        Node(String key, Integer freq, Node left, Node right)  {  
            this.key = key;  
            this.freq = freq;  
            this.left = left;  
            this.right = right;  
        }  
    }  

    class EncodedSymbol {  
        int r;
        int g;
        int b;
      
        EncodedSymbol(int r, int g, int b)  {  
            this.r = r;
            this.g = g;
            this.b = b;
        }  
    }  


    

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // calculating height and width
        int iHeight = (height-image.getHeight())/2;
        int iWidthRight = (width - 2*image.getWidth())/2;

        g.drawImage(image, iWidthRight, iHeight, this);
    }
    
}