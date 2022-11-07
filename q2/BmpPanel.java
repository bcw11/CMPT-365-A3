package q2;

import javax.swing.*;
import java.awt.image.*;
import java.awt.*;


class BmpPanel extends JPanel {
    BufferedImage image;
    int width;
    int height;

    public BmpPanel(BufferedImage image,int width,int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int imgW = image.getWidth();
        int imgH = image.getHeight();
        g.drawImage(image, (width-imgW)/2, (height-imgH)/2, this);
    } 
}