package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    private static final double WIDTH_RATIO = 0.2;
    private static final double HEIGHT_RATIO = 0.08;
    final JLabel display = new JLabel();
    final JButton up = new JButton("up");
    final JButton down = new JButton("down");

    public ConcurrentGUI() {
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
        new Thread(counter).start();

        /* Listeners */
        up.addActionListener((e)->counter.up());
        down.addActionListener((e)->counter.down());
        stop.addActionListener((e)->counter.stop());
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
            while(!this.stop) {
                try {
                    if(!this.down) {
                        counter++;
                    } else {
                        counter--;
                    }
                    ConcurrentGUI.this.display.setText(Integer.toString(counter));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        public void stop() {
            this.stop = true;
            ConcurrentGUI.this.up.setEnabled(false);
            ConcurrentGUI.this.down.setEnabled(false);
        }

        public void up() {
            this.down = false;
        }

        public void down() {
            this.down = true;
        }

    }
}
