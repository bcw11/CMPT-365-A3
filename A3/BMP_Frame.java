package A3;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;

public class BMP_Frame{
    // application variables 
    private static JFrame frame;
    private static JPanel panel;
    private static JFileChooser fileChooser;

    // image variables
    public static BMP_Panel bmpPanel;
    public static int width;
    public static int height;

    // menu variables
    private static JMenuBar menu;
    private static JMenu fileMenu;
    private static JMenuItem openFileItem;
    private static JMenuItem exitItem;

    public static void main(String[] args){
        width = 1500;
        height = 750;

        // creating new frame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width,height);
        frame.setLocationRelativeTo(null);
        frame.setTitle("CMPT 365 A2");

        // new panel 
        panel = new JPanel();
        frame.add(panel);

        // adding keylistener to change images 
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(bmpPanel != null)
                    bmpPanel.nextImageSet();
            }
            // not used
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) { }
        });

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

            // creating dialog box
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            // opening dialog box 
            int result = fileChooser.showOpenDialog(panel);

            // if user presses 'ok'
            if (result == JFileChooser.APPROVE_OPTION) {
                
                // get file 
                File file = fileChooser.getSelectedFile();
                
                // read image 
                BufferedImage image = null;
                try{
                    image = ImageIO.read(file);
                }catch (IOException ex){ }

                // display image to BmpPanel
                try {
					bmpPanel = new BMP_Panel(image,width,height);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                
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




