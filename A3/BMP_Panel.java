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
        
        // calculating compression
        imgBits = image.getColorModel().getPixelSize()*(image.getWidth()*image.getHeight());
        hufBits = huffmanEncoding();
        jpgBits = jpegEncoding();
       
        // setting panel layout to null to use absolute positioning
        setLayout(null);

        L1 = new JLabel("(1) Huffman Compression Ratio: "+Float.toString((float)imgBits/(float)hufBits));
        Dimension D1 = L1.getPreferredSize();
        L1.setBounds((width/2)+10,height/2,(int)D1.getWidth(),(int)D1.getHeight());
        add(L1);

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
                int rgb = image.getRGB(x, y);
                Color c = new Color(rgb);
                String s = Integer.toString(c.getBlue())+Integer.toString(c.getGreen())+Integer.toString(c.getRed());

                if(!map.containsKey(s))
                    map.put(s,1);
                else{
                    map.put(s,map.get(s)+1);
                }
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


        // storing huffman code of symbols
        Map<String, Integer> huffmanCodes = new HashMap<>();  
        codeword(huffmanCodes, tree.peek(), "");  

        //creating an instance of the StringBuilder class   
        // StringBuilder sb = new StringBuilder(); 
        int bits = 0; 
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color c = new Color(rgb);
                String s = Integer.toString(c.getBlue())+Integer.toString(c.getGreen())+Integer.toString(c.getRed());

                // getting number of bits in code 
                bits += huffmanCodes.get(s);
                // sb.append(huffmanCode.get(s));
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


    private int jpegEncoding(){

        return 1;
    }


    // adapted from https://introcs.cs.princeton.edu/java/31datatype/Luminance.java.html
    private BufferedImage getGrayImage(BufferedImage img) {
        // creating a new buffered image 
        BufferedImage outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                // getting rgb colours
                Color c = new Color(img.getRGB(x, y));
                int blue = c.getBlue();
                int green = c.getGreen();
                int red = c.getRed();

                // calculating luminance 
                int L = (int) Math.round((red*0.299) + (green*0.587) + (blue*0.114));

                // colour with luminance is gray scaled image 
                Color o = new Color(L,L,L);
                outImg.setRGB(x, y, o.getRGB());
            }
        }
        return outImg;
    }

    // private BufferedImage getOrderedDitheringImage(BufferedImage img) {
    //     // creating a new buffered image 
    //     BufferedImage outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

    //     // creating dither matrix 
    //     int N = 4;
    //     int[][] D = getDitherMatrix(N);

    //     for (int x = 0; x < img.getWidth(); x++) {
    //         for (int y = 0; y < img.getHeight(); y++) {

    //             // calculating luminance 
    //             Color c = new Color(img.getRGB(x, y));
    //             int blue = c.getBlue();
    //             int green = c.getGreen();
    //             int red = c.getRed();
    //             int L = (int) Math.round((red*0.299) + (green*0.587) + (blue*0.114));

    //             // calculating dither luminance
    //             L = (int)Math.round((float)L*Math.pow(N,2)/(256));
                
    //             // getting dither index 
    //             int i = x % N;
    //             int j = y % N;

    //             // determining when to place dot
    //             Color o;
    //             if(L > D[i][j]){
    //                 o = new Color(255,255,255);
    //             }
    //             else
    //                 o = new Color(0,0,0);
    //             outImg.setRGB(x, y, o.getRGB());
    //         }
    //     }
    //     return outImg;
    // }
    // // algorithm from https://en.wikipedia.org/wiki/Histogram_equalization
    // private BufferedImage getAutoLevelImage(BufferedImage img) {
    //     // creating a new buffered image 
    //     BufferedImage outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

    //     // ideal amount of pixels for each level
    //     float totalPixels = img.getWidth()*img.getHeight();

    //     // counting number of pixels on each level
    //     float[] countR = new float[256];
    //     float[] countG = new float[256];
    //     float[] countB = new float[256];
    //     for (int x = 0; x < img.getWidth(); x++) {
    //         for (int y = 0; y < img.getHeight(); y++) {
    //             Color c = new Color(img.getRGB(x, y));
    //             int red = c.getRed();
    //             int green = c.getGreen();
    //             int blue = c.getBlue();
    //             countR[red]++;
    //             countG[green]++;
    //             countB[blue]++;
    //         }
    //     }

    //     // calculating pdf for each level
    //     float[] pdfR = new float[256];
    //     float[] pdfG = new float[256];
    //     float[] pdfB = new float[256];
    //     for(int i = 0; i < 256; i++){
    //         pdfR[i] = countR[i]/totalPixels;
    //         pdfG[i] = countG[i]/totalPixels;
    //         pdfB[i] = countB[i]/totalPixels;
    //     }

    //     // calculating cdf for each level
    //     float[] cdfR = new float[256];
    //     float[] cdfG = new float[256];
    //     float[] cdfB = new float[256];
    //     cdfR[0] = pdfR[0];
    //     cdfG[0] = pdfG[0];
    //     cdfB[0] = pdfB[0];
    //     for(int i = 1; i < 256; i++){
    //         cdfR[i] = cdfR[i-1] + pdfR[i];
    //         cdfG[i] = cdfG[i-1] + pdfG[i];
    //         cdfB[i] = cdfB[i-1] + pdfB[i];
            
    //         // dealing with floating point errors 
    //         cdfR[i] = (cdfR[i] > 1f) ? 1f : cdfR[i];
    //         cdfG[i] = (cdfG[i] > 1f) ? 1f : cdfG[i];
    //         cdfB[i] = (cdfB[i] > 1f) ? 1f : cdfB[i];
    //     }

    //     for (int x = 0; x < img.getWidth(); x++) {
    //         for (int y = 0; y < img.getHeight(); y++) {
    //             Color c = new Color(img.getRGB(x, y));
    //             int red = c.getRed();
    //             int green = c.getGreen();
    //             int blue = c.getBlue();

    //             // calculating shifted rgb values
    //             red = (int)Math.ceil(256*cdfR[red])-1;
    //             green = (int)Math.ceil(256*cdfG[green])-1;
    //             blue = (int)Math.ceil(256*cdfB[blue])-1;

    //             //  determining when to place dot
    //             Color o = new Color(red,green,blue);
    //             outImg.setRGB(x, y, o.getRGB());
    //         }
    //     }
    
    //     return outImg;
    // }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // calculating height and width
        int iHeight = (height-image.getHeight())/2;
        int iWidthRight = (width - 2*image.getWidth())/2;

        g.drawImage(image, iWidthRight, iHeight, this);
    } 

    // painting the next image set 
    // public void nextImageSet(){
    //     if(++imgSet == 3)
    //         imgSet = 0;
    //     if(imgSet == 0){
    //         L2.setText("1: Original | Grayscale");
    //         Dimension D2 = L2.getPreferredSize();
    //         L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
    //         displayLeftImage = image;
    //         displayRightImage = grayImage;
    //     }
    //     else if(imgSet == 1){
    //         L2.setText("2: Grayscale | Ordered Dithering");
    //         Dimension D2 = L2.getPreferredSize();
    //         L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
    //         displayLeftImage = grayImage;
    //         displayRightImage = orderedDitheringImage;
    //     }
    //     else{
    //         L2.setText("3: Original | Auto Leveled");
    //         Dimension D2 = L2.getPreferredSize();
    //         L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
    //         displayLeftImage = image;
    //         displayRightImage = autoLevelImage;
    //     }
    //     repaint();
    // }
    
}