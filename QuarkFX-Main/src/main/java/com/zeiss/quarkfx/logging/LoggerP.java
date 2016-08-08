package com.zeiss.quarkfx.logging;

import org.jetbrains.annotations.NotNull;

/**
 * interface specifying logger functionality
 */
public interface LoggerP {

    /**
     * log message at specific loglevel
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     * @param level importance (level) of this message
     */
    void logMessage(String tag, String message, @NotNull LogLevel level);

    /**
     * log an exception
     * @param tag add a tag to the message in order to group them (see android)
     * @param message message to be logged
     * @param th exception to be logged
     */
    void logException(String tag, String message, Throwable th);
}