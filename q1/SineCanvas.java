package q1;

import java.awt.*;
import java.lang.Math;

// adapted from https://www.javacodex.com/Graphics/Sine-Wave
public class SineCanvas extends Canvas {
    int N;
    int width;
    int height;

    double[] y;
    double[] ySum;
    Color[] colors;

    public void sinN(int n){
        for(int i = 0; i < width; i++){
            double radians = (Math.PI / (width/2)) * i;
            y[i] = (Math.sin(-n*radians)*(height/2))/8;
            ySum[i] = ySum[i] + y[i];
        }
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setColor(new Color(225,225,225));
        g.fillRect(50,50,width,height);
        g.setColor(Color.BLACK);
        g.drawRect(50,50,width,height);

        // x-axis
        g.drawLine(50,(height/2)+50,width+50,(height/2)+50);

        // draw indices
        drawIndices(g);

        // plotting all sinN functions
        for(int n = 1; n <= N; n++){
            // get sine points
            sinN(n);
            
            if(n < 11){
                g.setColor(colors[n-1]);
            }else{
                g.setColor(colors[n%10]);
            }
            
            // draw sines
            for(int i = 1; i < width; i++) {
                int x1 = i + 49 - 1;
                int x2 = i + 49;
                int y1 = (int) y[i - 1] + 250;
                int y2 = (int) y[i] + 250;
                g.drawLine(x1, y1, x2, y2);
            }
        }

        // plotting quantized sinN
        for(int i = 1; i <= 20; i++){
            int interval = height/(8*2);

            int x1 = (width*i/20) + 49;

            // sampled values
            int y1 = (int) y[(width*i/20)-1] + 250;
            
            // frequency represented by 2 bits 
            float y1Float = (float)(y1-200)/interval;
            int y1bits = (int) Math.round(y1Float);

            // converting bits to be drawn in right position 
            y1 = ((y1bits-2)*interval)+250;
            g.drawOval(x1-2,y1-2,5,5); // y1-2 to centre circle
            g.drawLine(x1-2,(height/2)+50,x1-2,y1-2);
        }

        // plotting sum of sinN functions
        g.setColor(colors[10]);
        if(N > 1){
            for(int i = 1; i < width; i++) {
                int x1 = i + 49 - 1;
                int x2 = i + 49;
                int y1 = (int) ySum[i - 1] + 250;
                int y2 = (int) ySum[i] + 250;
                g.drawLine(x1, y1, x2, y2);
            }
        }

    }

    public void drawIndices(Graphics g){
        // x-indices 
        g.drawLine(50,450,50,460);
        g.drawString("0",46,475);

        g.drawLine(250,450,250,460);
        g.drawString("π/2",237,475);

        g.drawLine(450,450,450,460);
        g.drawString("π",445,475);

        g.drawLine(650,450,650,460);
        g.drawString("3π/2",633,475);

        g.drawLine(850,450,850,460);
        g.drawString("2π",841,475);
        
        // y-indices
        g.drawLine(50,50,40,50);
        g.drawString("8",27,55);

        g.drawLine(50,150,40,150);
        g.drawString("4",27,155);

        g.drawLine(50,250,40,250);
        g.drawString("0",27,255);

        g.drawLine(50,350,40,350);
        g.drawString("-4",21,355);

        g.drawLine(50,450,40,450);
        g.drawString("-8",21,455);
        
    }

    public SineCanvas(int N, int width, int height){
        this.N = N;
        this.height = height;
        this.width = width;
        y = new double[width];
        ySum = new double[width];

        //Allocate the size of the array
        colors = new Color[11];
        colors[0] = Color.RED;
        colors[1] = new Color(0,128,0); //green
        colors[2] = Color.BLUE;
        colors[3] = Color.MAGENTA;
        colors[4] = Color.YELLOW;
        colors[5] = Color.ORANGE;
        colors[6] = new Color(186,85,211);
        colors[7] = Color.CYAN;
        colors[8] = new Color(250,128,114);
        colors[9] = Color.GREEN;
        colors[10] = new Color(255,0,127);
    }
}
