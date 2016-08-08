package com.zeiss.quarkfx.logging;

import org.jetbrains.annotations.NotNull;

/**
 * enumeration of all loglevels ordered by increasing importance
 */
public enum LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR;

    public boolean isGreaterEqual(@NotNull LogLevel level) {
        return this.ordinal() >= level.ordinal();
    }
}
