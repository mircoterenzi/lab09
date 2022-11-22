package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final double WIDTH_RATIO = 0.2;
    private static final double HEIGHT_RATIO = 0.08;
    public final JLabel display = new JLabel();
    public final JButton up = new JButton("up");
    public final JButton down = new JButton("down");
    final JButton stop = new JButton("stop");
    final Agent counter = new Agent();
    

    public AnotherConcurrentGUI() {
        /* Building the interface */
        super();
        final JPanel canvas = new JPanel();
        canvas.setLayout(new FlowLayout());
        display.setText("0");
        canvas.add(display);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);

        /* Setting frame dimensions */
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth()*WIDTH_RATIO), (int) (screenSize.getHeight()*HEIGHT_RATIO));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setContentPane(canvas);
        this.setVisible(true);
        
        /* Threads */
        new Thread(counter).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stop();
            }
        }).start();

        /* Listeners */
        up.addActionListener((e)->counter.up());
        down.addActionListener((e)->counter.down());
        stop.addActionListener((e)->counter.stop());
    }

    private void stop() {
        counter.stop();
        try {
            SwingUtilities.invokeAndWait(() -> {
                stop.setEnabled(false);
                up.setEnabled(false);
                down.setEnabled(false);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
            while(!this.stop) {
                try {
                    this.counter += this.down ? -1 : +1;
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(counter)));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        public void stop() {
            this.stop = true;
        }

        public void up() {
            this.down = false;
        }

        public void down() {
            this.down = true;
        }

    }
}
