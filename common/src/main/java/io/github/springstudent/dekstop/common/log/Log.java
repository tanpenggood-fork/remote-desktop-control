package io.github.springstudent.dekstop.common.log;



import io.github.springstudent.dekstop.common.log.console.ConsoleAppender;
import io.github.springstudent.dekstop.common.log.file.FileAppender;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.lang.System.getProperty;

/**
 * Minimal logging interface - minimize the JAR size on the assisted side - at
 * the same time I need a very simple stuff on the assistant side - let's see
 * later (!)
 */
public final class Log {
    private static final boolean DEBUG = System.getProperty("remoteDesktopControl.debug") != null;

    private static LogAppender out;

    private Log() {
    }

    static {
        String mode = System.getProperty("remoteDesktopControl.log", "console");
        out = new ConsoleAppender();
        if (mode.equals("file")) {
            out = createFileAppender();
        }
    }

    private static LogAppender createFileAppender() {
        try {
            String logFile = getOrCreateLogFile().getAbsolutePath();
            info("Log logFile : " + logFile);
            return new FileAppender(logFile);
        } catch (IOException ex) {
            warn("Log file setup error (fallback to console)!", ex);
            return new ConsoleAppender();
        }
    }

    private static File getOrCreateLogFile() throws IOException {
        final File file = new File(getProperty("remoteDesktopControl.home"), getProperty("remoteDesktopControl.application.name") + ".log");
        if (file.isDirectory()) {
            throw new IOException(format("Error creating %s", file.getName()));
        }
        return file;
    }


    public static void debug(String message) {
        if (DEBUG) {
            out.append(LogLevel.DEBUG, message);
        }
    }

    public static void debug(String message, Supplier<String> messageSupplier) {
        if (DEBUG) {
            out.append(LogLevel.DEBUG, format(message, messageSupplier.get()));
        }
    }

    public static void debug(String message, Throwable error) {
        if (DEBUG) {
            out.append(LogLevel.DEBUG, message, error);
        }
    }

    public static void info(String message) {
        out.append(LogLevel.INFO, message);
    }

    public static void warn(String message) {
        out.append(LogLevel.WARN, message);
    }

    public static void warn(Throwable error) {
        out.append(LogLevel.WARN, error.getMessage(), error);
    }

    public static void warn(String message, Throwable error) {
        out.append(LogLevel.WARN, message, error);
    }

    public static void error(String message) {
        out.append(LogLevel.ERROR, message);
    }

    public static void error(String message, Throwable error) {
        out.append(LogLevel.ERROR, message, error);
    }

    public static void fatal(String message) {
        out.append(LogLevel.FATAL, message);
    }

    public static void fatal(String message, Throwable error) {
        out.append(LogLevel.FATAL, message, error);
    }

}
