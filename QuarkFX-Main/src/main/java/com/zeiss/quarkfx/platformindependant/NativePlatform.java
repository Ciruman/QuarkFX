package com.zeiss.quarkfx.platformindependant;

import com.zeiss.quarkfx.logging.FallBackLogger;
import com.zeiss.quarkfx.logging.LoggerP;
import org.jetbrains.annotations.NotNull;

/**
 * class defining platform specific functionality
 */
public abstract class NativePlatform {

    public abstract NativeService getNativeService();

    @NotNull
    public LoggerP getLogger() {
        LoggerP logger = getNativeLogger();
        return logger == null ? new FallBackLogger() : logger;
    }

    protected abstract LoggerP getNativeLogger();
}
