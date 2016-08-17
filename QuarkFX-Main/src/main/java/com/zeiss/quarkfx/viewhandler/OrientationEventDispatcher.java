package com.zeiss.quarkfx.viewhandler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import javafx.stage.Screen;

/**
 * extraction of the listener to update the orientation handler
 */
public class OrientationEventDispatcher {

    private OrientationEventDispatcher() {}

    /**
     * register an orientationhandler to the given region
     * @param handler the orientation handler
     * @param region the region to be listened on
     */
    public static void register(final OrientationHandler handler, final Region region) {

        double dpiFac = Screen.getPrimary().getBounds().getHeight() / Screen.getPrimary().getVisualBounds().getHeight();

        handler.checkDPI(dpiFac);

        //If the height/width changes --> check if the orientation ist still correct
        region.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateHandler(handler, newValue.doubleValue(), region.getHeight());
            }
        });
        region.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateHandler(handler, region.getWidth(), newValue.doubleValue());
            }
        });

        updateHandler(handler, region.getWidth(), region.getHeight());
    }

    private static void updateHandler(OrientationHandler handler, final double width, final double height) {
        handler.checkOrientation(width, height);
        //TODO: we might need to update the size on desktop
        //if (JavaFXPlatform.isDesktop())
        //{
            handler.checkSize(width, height);
        //}
    }
}
