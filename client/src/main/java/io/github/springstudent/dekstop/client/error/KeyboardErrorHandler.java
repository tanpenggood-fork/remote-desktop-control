package io.github.springstudent.dekstop.client.error;


import io.github.springstudent.dekstop.common.log.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.String.format;

public final class KeyboardErrorHandler {
    private static JFrame frame;

    private KeyboardErrorHandler() {
    }

    /**
     * Displays a self closing translated warning message
     */
    public static void warn(final String message) {

        if (frame != null) {
            final JLabel label = new JLabel();
            final Timer timer = getTimer(label);
            timer.start();

            label.setText(format("<html>%s<br/>%s<br/>%s</html>", "The program has encountered a keyboard error",
                    "Details : %s","You should use the same input language on both computers"));
            JOptionPane.showMessageDialog(frame, label,"Warning", JOptionPane.WARNING_MESSAGE);

        } else {
            Log.error("Unable to display error message " + message);
        }

    }

    private static Timer getTimer(JLabel label) {
        ActionListener ac = new ActionListener() {
            private int timeLeft = 4;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    --timeLeft;
                } else {
                    ((Timer) e.getSource()).stop();
                    if (SwingUtilities.getWindowAncestor(label) != null) {
                        SwingUtilities.getWindowAncestor(label).setVisible(false);
                    }
                }
            }
        };

        Timer timer = new Timer(1000, ac);
        timer.setInitialDelay(0);
        return timer;
    }

    public static void attachFrame(JFrame frame) {
        KeyboardErrorHandler.frame = frame;
    }
}
