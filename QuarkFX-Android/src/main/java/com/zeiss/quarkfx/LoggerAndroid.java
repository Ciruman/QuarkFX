package com.zeiss.quarkfx;

import com.zeiss.quarkfx.logging.LogLevel;
import com.zeiss.quarkfx.logging.LoggerP;
import org.jetbrains.annotations.NotNull;

/**
 * platform specific logger for android
 */
public class LoggerAndroid implements LoggerP {

    @Override
    public void logMessage(String tag, String message, @NotNull LogLevel level) {
        android.util.Log.println(parseOrdinal(level),tag, message);
    }

    @Override
    public void logException(String tag, String message, Throwable th) {
        android.util.Log.e(tag, message + "\n" + android.util.Log.getStackTraceString(th));
    }


    private int parseOrdinal(LogLevel level) {
        switch (level) {
            case VERBOSE:
                return 2;
            case DEBUG:
                return 3;
            case INFO:
                return 4;
            case WARNING:
                return 5;
            case ERROR:
                return 6;
            default:
                return 7;//assert
        }
    }
}
