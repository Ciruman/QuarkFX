package com.zeiss.quarkfx;


import com.zeiss.quarkfx.logging.Log;
import com.zeiss.quarkfx.platformindependant.NativeService;
import com.zeiss.quarkfx.platformindependant.IntentP;
import org.jetbrains.annotations.NotNull;

/**
 * The Intent implementation for IOS
 */
public class IosNativeService implements NativeService {

    @NotNull
    @Override
    public IntentP getIntent(String[] args) {
        Log.error(IosNativeService.class.getSimpleName(), "Method <getIntent> not implemented yet");
        return new IntentP();
    }

    @Override
    public void sendIntent(IntentP intent) {
        Log.error(IosNativeService.class.getSimpleName(), "Method <sendIntent> not implemented yet");
    }

}
