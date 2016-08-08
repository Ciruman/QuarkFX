package com.zeiss.quarkfx.logging;

import org.jetbrains.annotations.Nullable;

/**
 * a static logger - pretty fancy
 */
public class Log {


    //TODO make threadsafe?
    private static LoggerP logger = new FallBackLogger();
    private static LogLevel minimumLogLevel = LogLevel.WARNING;

    private Log(){}

    /**
     * returns the minimum current loglevel that will be logged
     * @return minimum loglevel
     */
    public static LogLevel getMinimumLogLevel() {
        return minimumLogLevel;
    }

    /**
     * sets the minimum current loglevel that will be logged
     * @param level new minimum loglevel
     */
    public static void setMinimumLogLevel(LogLevel level) {
        minimumLogLevel = level;
    }

    /**
     * replaces default logger with platform specific logger
     * @param logger platform specific logger
     */
    public static void overrideDefaultLogger(@Nullable LoggerP logger) {
        if (logger == null) {
            return;
        }
        Log.logger = logger;
    }

    /**
     * log message at specific loglevel
     * @param message message to be logged
     * @param level importance (level) of this message
     */
    public static void log(String message, LogLevel level) {
        //TODO check if necessary with given loglevel
        final StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        String classname = stackTraceElement.getClassName();
        String methodname = stackTraceElement.getMethodName();
        log(classname.substring(classname.lastIndexOf('.') + 1) + "::" + methodname, message, level);
    }

    /**
     * log message at specific loglevel
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     * @param level importance (level) of this message
     */
    public static void log(String tag, String message, LogLevel level) {
        if (level.isGreaterEqual(minimumLogLevel)) {
            logger.logMessage(tag, message, level);
        }
    }

    /**
     * log message at level 'verbose'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void verbose(String tag, String message) {
        log(tag, message, LogLevel.VERBOSE);
    }

    /**
     * log message at level 'verbose'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void v(String tag, String message) {
        verbose(tag, message);
    }

    /**
     * log message at level 'debug'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void debug(String tag, String message) {
        log(tag, message, LogLevel.DEBUG);
    }

    /**
     * log message at level 'debug'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void d(String tag, String message) {
        debug(tag, message);
    }

    /**
     * log message at level 'debug'
     * @param message message to be logged
     */
    public static void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    /**
     * log message at level 'debug'
     * @param message message to be logged
     */
    public static void d(String message) {
        debug(message);
    }

    /**
     * log message at level 'info'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void info(String tag, String message) {
        log(tag, message, LogLevel.INFO);
    }

    /**
     * log message at level 'info'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void i(String tag, String message) {
        info(tag, message);
    }

    /**
     * log message at level 'warning'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void warning(String tag, String message) {
        log(tag, message, LogLevel.WARNING);
    }

    /**
     * log message at level 'warning'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void w(String tag, String message) {
        warning(tag, message);
    }

    /**
     * log message at level 'error'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void error(String tag, String message) {
        log(tag, message, LogLevel.ERROR);
    }

    /**
     * log message at level 'error'
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     */
    public static void e(String tag, String message) {
        error(tag, message);
    }

    /**
     * log an exception
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     * @param th exception to be logged
     */
    public static void exception(String tag, String message, Throwable th) {
        logger.logException(tag, message, th);
    }

    /**
     * log an exception
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     * @param th exception to be logged
     */
    public static void ex(String tag, String message, Throwable th) {
        exception(tag, message, th);
    }
}
