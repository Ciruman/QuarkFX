package com.zeiss.quarkfx;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.LifecycleEvent;
import com.gluonhq.charm.glisten.mvc.View;
import com.zeiss.quarkfx.exceptions.YouDidShitException;
import com.zeiss.quarkfx.logging.Log;
import com.zeiss.quarkfx.platformindependant.IntentP;
import com.zeiss.quarkfx.platformindependant.NativePlatform;
import com.zeiss.quarkfx.platformindependant.NativePlatformFactory;
import com.zeiss.quarkfx.viewhandler.OrientationEventDispatcher;
import com.zeiss.quarkfx.viewhandler.OrientationHandler;
import com.zeiss.quarkfx.viewhandler.ViewHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * The main class managing all Gluon and Javafx startup stuff as well as receiving intents
 */
public abstract class QuarkFXApplication extends MobileApplication implements IGluon {

    @NotNull
    private static String[] args = new String[0];
    private final List<StartupListener> startupListeners = new ArrayList<>();

    @Nullable
    private NativePlatform nativePlatform;//handles platform specific functionality
    private Scene scene;
    private boolean startupTrigger = false;

    public static @NotNull QuarkFXApplication getInstance() {
        if (MobileApplication.getInstance() == null) {
            throw new YouDidShitException("Please let me initialize first!");
        }
        return (QuarkFXApplication) MobileApplication.getInstance();
    }

    /**
     * The main method to be called when the app starts on desktop MUST BE CALLED FROM EXTENDER
     *
     * @param args
     */
    public static void main(@NotNull String[] args) {
        //TODO submit to javafx
        //TODO search for lowest extender with algorithm instead of just order
        Log.debug("initializing");
        Class<? extends Application> lastWorking = QuarkFXApplication.class;
        Field f;
        try {
            f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);

            //TODO: is suppress warning really necessary? Are there better ways?
            @SuppressWarnings("unchecked") List<Class> classes = (List<Class>) f.get(ClassLoader.getSystemClassLoader());
            for (Class c : classes) {
                if (!c.equals(IGluon.class) && !c.equals(MobileApplication.class) && !c.equals(Application.class)) {
                    Log.debug("checking class: " + c.getSimpleName());
                    if (QuarkFXApplication.class.isAssignableFrom(c)) {
                        Log.debug("Found one: " + c.getSimpleName());
                        lastWorking = c;
                    } else {
                        break;
                    }
                }
            }
        } catch (@NotNull NoSuchFieldException | IllegalAccessException e) {
            Log.exception(QuarkFXApplication.class.getSimpleName(), "Error finding extending Class", e);
        }

        Log.debug("final caller: " + lastWorking.getSimpleName());
        if (lastWorking.equals(QuarkFXApplication.class) || lastWorking.equals(MobileApplication.class) || lastWorking.equals(Application.class)) {
            throw new YouDidShitException("could not determine extending class, did you forget to do it?");
        }

        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            Application.launch(lastWorking);
            return;
        }
        QuarkFXApplication.args = args;
        Application.launch(lastWorking);
    }

    public Scene getScene() {
        return scene;
    }


    //TODO: sehr unschön, dass der extender diese Methode callen muss. Geht das anders? Und bitte in der README schreiben, wo diese Methode aufgerufen werden muss
    //TODO: description of @param args. What can you do with it?

    @Nullable
    public final NativePlatform getNativePlatform() {
        return nativePlatform;
    }

    @Override
    public final void init() {
        nativePlatform = NativePlatformFactory.getPlatform();
        Log.overrideDefaultLogger(nativePlatform.getLogger());

        final OrientationHandler orientationHandler = new OrientationHandler();
        final ViewHandler viewHandler = new ViewHandler(this);

        /*
         * Loading Screen
         */
        addViewFactory(HOME_VIEW, new Supplier<View>() {

                    @Override
                    public View get() {
                        View v = new View(HOME_VIEW);
                        final ImageView imageView = new ImageView(new Image(QuarkFXApplication.class.getResourceAsStream("/logo.jpg")));
                        imageView.setPreserveRatio(true);
                        imageView.setFitWidth(150);
                        v.setCenter(imageView);
                        v.setBottom(new Label("Loading ..."));
                        v.setOnShown(new EventHandler<LifecycleEvent>() {
                                       @Override
                                       public void handle(LifecycleEvent event) {
                                           QuarkFXApplication.this.getAppBar().setVisible(false);
                                           //TODO is this good?
                                           OrientationEventDispatcher.register(orientationHandler, QuarkFXApplication.this.getGlassPane());
                                           viewHandler.startUp(orientationHandler);
                                           QuarkFXApplication.this.triggerStartup();
                                           Log.d("### initial switch ###");

                                           final Timer timer = new Timer();
                                           timer.schedule(new TimerTask() {
                                               @Override
                                               public void run() {
                                                   Platform.runLater(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           viewHandler.switchToHome(ViewStackPolicy.SKIP);
                                                       }
                                                   });

                                                   //explicitly end timer: when you don't there could be a running thread on application stop that blocks other threads
                                                   timer.cancel();
                                                   timer.purge();
                                               }
                                           }, 1500);

                                       }
                                   }
                        );
                        return v;
                    }
                }

        );
        initApp(viewHandler);
    }

    @Override
    public final void postInit(Scene scene) {
        super.postInit(scene);
        this.scene = scene;
        IntentP intent = nativePlatform.getNativeService().getIntent(args);
        if (intent != null && !intent.isEmpty()) {
            handleIntent(intent);
        }
        postInitApp(scene);
    }


    @Override
    public final void stop() throws Exception {
        super.stop();
        stopApp();
    }

    private void startUp() {
        startApp();
    }

    /**
     * Every registered Listener will get notified as soon as Gluon has finished loading
     * @param listener listener to be notified
     */
    public void registerStartupListener(@NotNull StartupListener listener) {
        if (startupTrigger) {
            listener.onStartup();
            return;
        }
        startupListeners.add(listener);
    }

    private void triggerStartup() {
        if (startupTrigger) {
            return;
        }
        startupTrigger = true;

        for (StartupListener listener : startupListeners)
            listener.onStartup();
        startUp();
        startupListeners.clear();
    }

    /**
     * gets called as soon as Gluon sorted itself out (the loading screen showed up)
     */
    protected abstract void startApp();


//###########################################
//            ABSTRACT METHODS
//###########################################

    /**
     * @param scene The JavaFX Scene
     * @see javafx.application.Application
     */
    public abstract void postInitApp(Scene scene);

    /**
     * @see javafx.application.Application
     */
    public abstract void stopApp();

    /**
     * gets called when this app was launch via intent
     *
     * @param intent The intent received from another application
     */
    public abstract void handleIntent(IntentP intent);

    /**
     * @see javafx.application.Application
     *
     * @param viewHandler The ViewHandler in use to switch views
     */
    public abstract void initApp(ViewHandler viewHandler);

    /**
     * Simple notifier pattern
     */
    public interface StartupListener {
        /**
         * needed to get notified
         */
        void onStartup();
    }

}
