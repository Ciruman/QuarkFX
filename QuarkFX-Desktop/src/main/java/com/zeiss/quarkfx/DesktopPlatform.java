package com.zeiss.quarkfx;

import com.zeiss.quarkfx.logging.FallBackLogger;
import com.zeiss.quarkfx.logging.LoggerP;
import com.zeiss.quarkfx.platformindependant.NativePlatform;
import com.zeiss.quarkfx.platformindependant.NativeService;
import org.jetbrains.annotations.NotNull;

public class DesktopPlatform extends NativePlatform {

    private DesktopNativeService nativeService;

    @Override
    public NativeService getNativeService() {
        if (nativeService == null) {
            nativeService = new DesktopNativeService();
        }
        return nativeService;
    }

    @Override
    public @NotNull LoggerP getNativeLogger() {
        return new FallBackLogger();
    }

}
