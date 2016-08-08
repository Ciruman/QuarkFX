package com.zeiss.quarkfx.platformindependant;

import com.gluonhq.charm.down.common.JavaFXPlatform;
import com.zeiss.quarkfx.logging.Log;
import org.jetbrains.annotations.Nullable;

/**
 * Factory creating platform specific connections
 */
public class NativePlatformFactory {

    private NativePlatformFactory() {}

    @Nullable public static NativePlatform getPlatform() {
        try {
            return (NativePlatform) Class.forName(getPlatformClassName()).newInstance();
        } catch (Exception ex) {
            Log.ex(NativePlatformFactory.class.getSimpleName(), "Could not create platform", ex);
            return null;
        }

    }

    private static String getPlatformClassName() {
        switch (JavaFXPlatform.getCurrent()) {
            case ANDROID:
                return "com.zeiss.quarkfx.AndroidPlatform";
            case IOS:
                return "com.zeiss.quarkfx.IosPlatform";
            default:
                return "com.zeiss.quarkfx.DesktopPlatform";
        }
    }
}
