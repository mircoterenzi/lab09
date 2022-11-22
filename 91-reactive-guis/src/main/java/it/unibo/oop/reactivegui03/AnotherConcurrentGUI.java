package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final double WIDTH_RATIO = 0.2;
    private static final double HEIGHT_RATIO = 0.08;
    private volatile boolean stop;
    public final JLabel display = new JLabel();
    public final JButton up = new JButton("up");
    public final JButton down = new JButton("down");
    

    public AnotherConcurrentGUI() {
        /* Building the interface */
        super();
        final JPanel canvas = new JPanel();
        canvas.setLayout(new FlowLayout());
        display.setText("0");
        canvas.add(display);
        canvas.add(up);
        canvas.add(down);
        final JButton stop = new JButton("stop");
        canvas.add(stop);

        /* Setting frame dimensions */
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth()*WIDTH_RATIO), (int) (screenSize.getHeight()*HEIGHT_RATIO));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setContentPane(canvas);
        this.setVisible(true);
        
        /* Threads */
        final Agent counter = new Agent();
        final secondAgent stopper = new secondAgent();
        new Thread(counter).start();
        new Thread(stopper).start();

        /* Listeners */
        up.addActionListener((e)->counter.up());
        down.addActionListener((e)->counter.down());
        stop.addActionListener((e)->counter.stop());
    }

    private class Agent implements Runnable {
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
            while(!AnotherConcurrentGUI.this.stop) {
                try {
                    this.counter += this.down ? -1 : +1;
                    AnotherConcurrentGUI.this.display.setText(Integer.toString(counter));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        public void stop() {
            AnotherConcurrentGUI.this.stop = true;
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
        }

        public void up() {
            this.down = false;
        }

        public void down() {
            this.down = true;
        }

    }

    private class secondAgent implements Runnable {
        private int counter;

        @Override
        public void run() {
            while(this.counter<100) {
                try {
                    counter++;
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnotherConcurrentGUI.this.stop = true;
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
            
        }
        
    }
}
