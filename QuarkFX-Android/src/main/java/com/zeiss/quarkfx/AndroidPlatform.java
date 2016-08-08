package com.zeiss.quarkfx;


import com.zeiss.quarkfx.logging.LoggerP;
import com.zeiss.quarkfx.platformindependant.NativePlatform;
import com.zeiss.quarkfx.platformindependant.NativeService;
import org.jetbrains.annotations.Nullable;

public class AndroidPlatform extends NativePlatform {

    private AndroidNativeService nativeService;

    @Override
    public NativeService getNativeService() {
        if (nativeService == null) {
            nativeService = new AndroidNativeService();
        }
        return nativeService;
    }

    @Override
    protected @Nullable LoggerP getNativeLogger() {
        return new LoggerAndroid();
    }
}
