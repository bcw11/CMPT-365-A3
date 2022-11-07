package q2;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;

public class BmpDisplay{
    private static JFrame frame;
    private static JPanel panel;
    private static JFileChooser fileChooser;

    public static int width;
    public static int height;

    private static JMenuBar menu;
    private static JMenu fileMenu;
    private static JMenuItem openFileItem;
    private static JMenuItem exitItem;

    public static void main(String[] args){
        width = 750;
        height = 750;

        // creating new frame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width,height);
        frame.setLocationRelativeTo(null);
        frame.setTitle("CMPT365 A1 Q2");

        // new panel 
        panel = new JPanel();
        frame.add(panel);

        // creating menu bar and items 
        menu = new JMenuBar();
        frame.setJMenuBar(menu);

        fileMenu = new JMenu("File");
        menu.add(fileMenu);

        openFileItem = new JMenuItem("Open File");
        openFileItem.addActionListener(new openFile());
        fileMenu.add(openFileItem);

        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new exit());
        fileMenu.add(exitItem);


        frame.setVisible(true); 
    }

    static class openFile implements ActionListener {
        // code adapted from https://www.codejava.net/java-se/swing/show-simple-open-file-dialog-using-jfilechooser
        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            int result = fileChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {

                File file = fileChooser.getSelectedFile();

                BufferedImage image = null;
                try{
                    image = ImageIO.read(file);
                }catch (IOException ex){ }

                BmpPanel bmpPanel = new BmpPanel(image,width,height);
                frame.add(bmpPanel);
                frame.setVisible(true);
            }
        }
    }

    static class exit implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    
}




