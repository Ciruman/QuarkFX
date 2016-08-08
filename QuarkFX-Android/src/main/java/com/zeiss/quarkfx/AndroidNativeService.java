package com.zeiss.quarkfx;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.zeiss.quarkfx.logging.Log;
import com.zeiss.quarkfx.platformindependant.NativeService;
import com.zeiss.quarkfx.platformindependant.IntentP;
import javafxports.android.FXActivity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndroidNativeService implements NativeService {


    @NotNull
    @Override
    public IntentP getIntent(String[] args) {
        Intent intent = FXActivity.getInstance().getIntent();
        IntentP platformIntent = new IntentP();
        platformIntent.setData(intent.getDataString());
        platformIntent.setAction(intent.getAction());
        return platformIntent;
    }

    @Override
    public void sendIntent(@NotNull IntentP intent) throws ClassNotFoundException {

        Intent intentNative = new Intent("android.intent.action." + intent.getAction());
        if (intent.hasSpecificReceiver()) {
            System.out.println("### set receiver");
            if (!intent.getReceiverMainClass().contains(".")) {
                Log.error(AndroidNativeService.class.getSimpleName(), "You have no package!!");
                return;
            }
            String[] arr = intent.getReceiverMainClass().split("\\.");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < arr.length - 1; i++) {
                String s = arr[i];
                builder.append(s);
                builder.append(".");
            }
            builder.deleteCharAt(builder.length() - 1);
            String pkg = builder.toString();
            System.out.println("**** PACKAGE: " + pkg);
            intentNative.setComponent(new ComponentName(pkg, "javafxports.android.FXActivity"));
            intentNative.setData(Uri.parse(intent.getData()));
        } else {
            intentNative.setDataAndType(Uri.parse(intent.getData()), intent.getMimeType());
        }
        for (String s : intent.getCategories()) {
            intentNative.addCategory("android.intent.category." + s);
        }


        System.out.println("### intent: " + intentNative.toString());

        if (isCallable(intentNative)) {
            FXActivity.getInstance().startActivity(intentNative);
        } else {
            throw new ClassNotFoundException("Cant find any application for this intent " + intent.getReceiverMainClass());
        }
    }


    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = FXActivity.getInstance().getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }
}
