package io.github.springstudent.dekstop.client.error;

import io.github.springstudent.dekstop.common.log.Log;

import javax.swing.*;

public final class FatalErrorHandler {
    private static JFrame frame;

    private FatalErrorHandler() {
    }

    /**
     * Displays a translated error message and terminates
     */
    public static void bye(String message, Throwable error) {
        Log.fatal(message, error);
        Log.fatal("Bye!");
        if (frame != null) {
            JOptionPane.showMessageDialog(frame, message, "提示", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(-1);
    }

    public static void attachFrame(JFrame frame) {
        FatalErrorHandler.frame = frame;
    }
}
