package A3;

import javax.swing.*;
import java.awt.image.*;
import java.io.IOException;
import java.awt.*;


class BMP_Panel extends JPanel {
    // 4 different image types to save 
    BufferedImage image;
    BufferedImage grayImage;
    BufferedImage orderedDitheringImage;
    BufferedImage autoLevelImage;

    // left and right display images
    BufferedImage displayLeftImage;
    BufferedImage displayRightImage;

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
        
        // calculating images
        grayImage = getGrayImage(image);
        orderedDitheringImage = getOrderedDitheringImage(image);
        autoLevelImage = getAutoLevelImage(image);

        // left and right display image
        displayLeftImage = image;
        displayRightImage = grayImage;

        // setting panel layout to null to use absolute positioning
        setLayout(null);

        L1 = new JLabel("Press any key to refresh images");
        Dimension D1 = L1.getPreferredSize();
        L1.setBounds((width-(int)D1.getWidth())/2,15,(int)D1.getWidth(),(int)D1.getHeight());
        add(L1);

        L2 = new JLabel("1: Original | Grayscale");
        Dimension D2 = L2.getPreferredSize();
        L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
        add(L2);
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

    private BufferedImage getOrderedDitheringImage(BufferedImage img) {
        // creating a new buffered image 
        BufferedImage outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        // creating dither matrix 
        int N = 4;
        int[][] D = getDitherMatrix(N);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                // calculating luminance 
                Color c = new Color(img.getRGB(x, y));
                int blue = c.getBlue();
                int green = c.getGreen();
                int red = c.getRed();
                int L = (int) Math.round((red*0.299) + (green*0.587) + (blue*0.114));

                // calculating dither luminance
                L = (int)Math.round((float)L*Math.pow(N,2)/(256));
                
                // getting dither index 
                int i = x % N;
                int j = y % N;

                // determining when to place dot
                Color o;
                if(L > D[i][j]){
                    o = new Color(255,255,255);
                }
                else
                    o = new Color(0,0,0);
                outImg.setRGB(x, y, o.getRGB());
            }
        }
        return outImg;
    }
    private int[][] getDitherMatrix(int N){
        int[][] D = new int[N][N];

        // original dither matrix
        D[0][0] = 0; D[0][1] = 8; D[0][2] = 2; D[0][3] = 10;
        D[1][0] = 12; D[1][1] = 4; D[1][2] = 14; D[1][3] = 6;
        D[2][0] = 3; D[2][1] = 11; D[2][2] = 1; D[2][3] = 9;
        D[3][0] = 15; D[3][1] = 7; D[3][2] = 13; D[3][3] = 5;

        // spiral dither matrix 
        // D[0][0] = 0; D[0][1] = 1; D[0][2] = 2; D[0][3] = 3;
        // D[1][0] = 11; D[1][1] = 12; D[1][2] = 13; D[1][3] = 4; 
        // D[2][0] = 10; D[2][1] = 15; D[2][2] = 14; D[2][3] = 5;
        // D[3][0] = 9; D[3][1] = 8; D[3][2] = 7; D[3][3] = 6;

        return D;
    }

    // algorithm from https://en.wikipedia.org/wiki/Histogram_equalization
    private BufferedImage getAutoLevelImage(BufferedImage img) {
        // creating a new buffered image 
        BufferedImage outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        // ideal amount of pixels for each level
        float totalPixels = img.getWidth()*img.getHeight();

        // counting number of pixels on each level
        float[] countR = new float[256];
        float[] countG = new float[256];
        float[] countB = new float[256];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                countR[red]++;
                countG[green]++;
                countB[blue]++;
            }
        }

        // calculating pdf for each level
        float[] pdfR = new float[256];
        float[] pdfG = new float[256];
        float[] pdfB = new float[256];
        for(int i = 0; i < 256; i++){
            pdfR[i] = countR[i]/totalPixels;
            pdfG[i] = countG[i]/totalPixels;
            pdfB[i] = countB[i]/totalPixels;
        }

        // calculating cdf for each level
        float[] cdfR = new float[256];
        float[] cdfG = new float[256];
        float[] cdfB = new float[256];
        cdfR[0] = pdfR[0];
        cdfG[0] = pdfG[0];
        cdfB[0] = pdfB[0];
        for(int i = 1; i < 256; i++){
            cdfR[i] = cdfR[i-1] + pdfR[i];
            cdfG[i] = cdfG[i-1] + pdfG[i];
            cdfB[i] = cdfB[i-1] + pdfB[i];
            
            // dealing with floating point errors 
            cdfR[i] = (cdfR[i] > 1f) ? 1f : cdfR[i];
            cdfG[i] = (cdfG[i] > 1f) ? 1f : cdfG[i];
            cdfB[i] = (cdfB[i] > 1f) ? 1f : cdfB[i];
        }

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();

                // calculating shifted rgb values
                red = (int)Math.ceil(256*cdfR[red])-1;
                green = (int)Math.ceil(256*cdfG[green])-1;
                blue = (int)Math.ceil(256*cdfB[blue])-1;

                //  determining when to place dot
                Color o = new Color(red,green,blue);
                outImg.setRGB(x, y, o.getRGB());
            }
        }
    
        return outImg;
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // calculating height and width
        int iHeight = (height-image.getHeight())/2;
        int iWidthRight = (width - 2*image.getWidth())/2;
        int iWidthLeft = width/2;

        g.drawImage(displayLeftImage, iWidthRight, iHeight, this);
        g.drawImage(displayRightImage, iWidthLeft, iHeight, this);
    } 

    // painting the next image set 
    public void nextImageSet(){
        if(++imgSet == 3)
            imgSet = 0;

        if(imgSet == 0){
            L2.setText("1: Original | Grayscale");
            Dimension D2 = L2.getPreferredSize();
            L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
            displayLeftImage = image;
            displayRightImage = grayImage;
        }
        else if(imgSet == 1){
            L2.setText("2: Grayscale | Ordered Dithering");
            Dimension D2 = L2.getPreferredSize();
            L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
            displayLeftImage = grayImage;
            displayRightImage = orderedDitheringImage;
        }
        else{
            L2.setText("3: Original | Auto Leveled");
            Dimension D2 = L2.getPreferredSize();
            L2.setBounds((width-(int)D2.getWidth())/2,35,(int)D2.getWidth(),(int)D2.getHeight());
            displayLeftImage = image;
            displayRightImage = autoLevelImage;
        }
        repaint();
    }
    
}