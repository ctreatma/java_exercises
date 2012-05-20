package edu.upenn.cis511;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class SierpinskiGasket extends JApplet {
    private static final long serialVersionUID = 1L;
    private int iterations;
    
    public SierpinskiGasket(int iterations) {
        this.iterations = iterations;
    }
    
    public void init() {    
    }
    
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawGasket(0, 0, 512, g2, 1);
    }
    
    private void drawGasket(int x, int y, int size, Graphics2D g, int depth) {
        if (size >= 2 && depth <= iterations) {
            g.setPaint(Color.WHITE);
            g.fill(new Rectangle(x, y, size, size));

            size /= 2;
            g.setPaint(Color.BLACK);
            g.fill(new Rectangle(x, y, size, size));
            g.fill(new Rectangle(x, y + size, size, size));
            g.fill(new Rectangle(x + size, y + size, size, size));

            drawGasket(x, y, size, g, ++depth);
            drawGasket(x, y + size, size, g, depth);
            drawGasket(x + size, y + size, size, g, depth);
        }
    }
    
    public static void main(String[] args) {
        Integer iterations = null;
        try {
            if (args.length > 0) {
                iterations = Integer.parseInt(args[0]);
            }
        }
        catch (Exception ex) {
            // Do nothing.
        }
        if (iterations == null) {
            iterations = new Integer(9);
        }
        JFrame f = new JFrame("Sierpinski Gasket");
        f.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        });
        JApplet applet = new SierpinskiGasket(iterations);
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(600, 600));
        f.setVisible(true);
    }
}
