package com.zeiss.quarkfx.logging;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A standard Logger to System.out or System.err
 */
public class FallBackLogger implements LoggerP {

    @Override
    public void logMessage(String tag, String message, @NotNull LogLevel level) {
        if (!level.isGreaterEqual(LogLevel.WARNING))
            System.out.println("[" + level.name() + "]\t<" + tag + ">\t" + message);
        else
            System.err.println("[" + level.name() + "]\t<" + tag + ">\t" + message);
    }

    @Override
    public void logException(String tag, String message, Throwable th) {
        if(th != null)
            logMessage(tag, message + "Message:\n" + th.getMessage() + "\nStacktrace:\n" + Arrays.toString(th.getStackTrace()) + "\nCause:\n" + th.getCause(), LogLevel.ERROR);
        else
            logMessage(tag, message, LogLevel.ERROR);
    }
}
