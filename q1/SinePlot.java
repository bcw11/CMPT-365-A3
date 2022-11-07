package q1;

import javax.swing.*;
import java.awt.event.*;


public class SinePlot{ 

    private static JFrame frame;
    private static JPanel panel;
    private static JLabel nLabel;
    private static JTextField nText;
    private static JButton button;
    public static void main(String[] args){
        // creating frame and panel 
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(245,245);
        frame.setTitle("CMPT365 A1 Q1");
        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        
        // creating text field for N 
        nLabel = new JLabel("N: ");
        nLabel.setBounds(20,20,80,25);
        panel.add(nLabel);
        nText = new JTextField(20);
        nText.setBounds(60,20,165,25);
        panel.add(nText);

        // creating button to get N input
        button = new JButton("enter"); 
        button.setBounds(125,50,100, 25); // x axis, y axis, width, height  
        button.addActionListener(new SineButton());
        frame.getRootPane().setDefaultButton(button);
        panel.add(button);


        frame.setLocationRelativeTo(null);
        frame.setVisible(true); 
    }

    static class SineButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int N;
    
            // getting N and turning into int
            String nStr = nText.getText();
            if(isNumeric(nStr)){
                N = Integer.parseInt(nStr);
            } else{ return; }
    
            // creating new frame
            JFrame paintFrame = new JFrame();
            paintFrame.setSize(900,525);
            
            // painting sin function(s)
            SineCanvas p = new SineCanvas(N,800,400);
            paintFrame.add(p);
    
            // paintFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            paintFrame.setLocationRelativeTo(null);
            paintFrame.setVisible(true); 
            // System.out.println(N);
        }
    }
    
    
    

    // adapted from https://www.freecodecamp.org/news/java-string-to-int-how-to-convert-a-string-to-an-integer/
    private static boolean isNumeric(String str){
        return str != null && str.matches("[0-9.]+");
    }

}
    