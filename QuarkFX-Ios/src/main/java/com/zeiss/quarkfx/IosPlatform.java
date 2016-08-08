package com.zeiss.quarkfx;

import com.zeiss.quarkfx.logging.FallBackLogger;
import com.zeiss.quarkfx.logging.LoggerP;
import com.zeiss.quarkfx.platformindependant.NativePlatform;
import com.zeiss.quarkfx.platformindependant.NativeService;
import org.jetbrains.annotations.NotNull;

/**
 * the IOS-specific Code implementation
 */
public class IosPlatform extends NativePlatform {

    private IosNativeService nativeService;

    @Override
    public NativeService getNativeService() {
        if (nativeService == null) {
            nativeService = new IosNativeService();
        }
        return nativeService;
    }

    @Override
    @NotNull
    public LoggerP getNativeLogger() {
        return new FallBackLogger();
    }

}
