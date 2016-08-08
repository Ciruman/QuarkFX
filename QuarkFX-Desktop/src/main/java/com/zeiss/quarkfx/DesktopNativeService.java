package com.zeiss.quarkfx;

import com.gluonhq.charm.glisten.control.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zeiss.quarkfx.logging.Log;
import com.zeiss.quarkfx.platformindependant.IntentP;
import com.zeiss.quarkfx.platformindependant.NativeService;
import com.zeiss.quarkfx.util.IntentApplication;
import com.zeiss.quarkfx.util.IntentDB;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Class for handling desktop-intents
 */
public class DesktopNativeService implements NativeService {

    @NotNull
    private final IntentDB intentDB;

    /**
     * creates new intent database and refreshes it
     */
    public DesktopNativeService() {
        intentDB = new IntentDB();
        intentDB.refresh();
    }

    @Nullable
    @Override
    public IntentP getIntent(@NotNull String[] args) {
        if (args.length < 2 || args[0] == null || args[0].isEmpty() || args[1] == null || args[1].isEmpty()) {
            Log.error(DesktopNativeService.class.getSimpleName(), "Got Nothing Folks");
            return new IntentP();
        }
        String json = args[0];
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        IntentP intent = null;
        try {
            intent = gson.fromJson(json, IntentP.class);
        } catch (JsonSyntaxException e) {
            Log.exception(DesktopNativeService.class.getSimpleName(), "Could not parse Json", e);
        }
        IntentDB.alterCurrentWindow(args[1]);
        return intent;
    }

    @Override
    public void sendIntent(@NotNull IntentP intent) throws ClassNotFoundException {
        //TODO call every time? or make forceable
        intentDB.refresh();
        //TODO isCallable() seems inefficient
        if (intentDB.isCallable(intent)) {
            List<IntentApplication> apps = intentDB.getPossibleReceivers(intent);
            IntentApplication selected = selectApp(apps);
            if (selected == null) {
                Log.warning(DesktopNativeService.class.getSimpleName(), "No App was selected");
                return;
            }
            String main = selected.main;
            intentDB.launchIntent(intent, main);
        } else {
            throw new ClassNotFoundException("Cant find any application for this intent " + intent.getReceiverMainClass());
        }
    }

    private @Nullable IntentApplication selectApp(@NotNull List<IntentApplication> apps) {
        if (apps.isEmpty()) {
            return null;
        }
        if (apps.size() == 1) {
            return apps.get(0);
        }
        final Dialog<IntentApplication> d = new Dialog<>("Please select an app to be launched");
        VBox vbox = new VBox();
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(new Label("Click on the app you want to open"));
        for (final IntentApplication a : apps) {
            Button b = new Button(a.label);
            b.setOnAction(event -> {
                d.setResult(a);
                d.hide();
            });
            vbox.getChildren().add(b);
        }
        d.setContent(vbox);
        Optional<IntentApplication> selected = d.showAndWait();
        if (selected.isPresent()) {
            return selected.get();
        }
        return null;
    }

}
