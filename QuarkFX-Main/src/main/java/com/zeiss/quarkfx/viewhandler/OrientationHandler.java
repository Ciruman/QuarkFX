package com.zeiss.quarkfx.viewhandler;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

//TODO make singleton

/**
 * Handler for orientation changes
 * managing orientation, size, dpi and resolution
 */
public class OrientationHandler {

    private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(Orientation.PORTRAIT);
    private final ObjectProperty<Size> size = new SimpleObjectProperty<>(Size.NORMAL);
    private final DoubleProperty dpiFactor = new SimpleDoubleProperty(1);
    private final ObjectProperty<Resolution> resolution = new SimpleObjectProperty<>(Resolution.MDPI);

    /**
     * calculates the DPI Factor and Resolution
     */
    void checkDPI(double dpiFac) {
        //FIXME as soon as FXports fixes it to Screen.getPrimary().getDPI();
        dpiFactor.set(dpiFac);
        if (dpiFac >= 3.1) {
            resolution.set(Resolution.XXXHDPI);
        } else if (dpiFac >= 2.1) {
            resolution.set(Resolution.XXHDPI);
        } else if (dpiFac >= 1.6) {
            resolution.set(Resolution.XHDPI);
        } else if (dpiFac >= 1.1) {
            resolution.set(Resolution.HDPI);
        } else if (dpiFac >= 0.76) {
            resolution.set(Resolution.MDPI);
        } else {
            resolution.set(Resolution.LDPI);
        }
    }

    /**
     * Checks whether application ist small, normal, large or xlarge
     */
    void checkSize(double x, double y) {
        double width = orientation.get() == Orientation.PORTRAIT ? x : y;
        double height = orientation.get() == Orientation.PORTRAIT ? y : x;

        if (height >= 960 && width >= 720) {
            size.set(Size.XLARGE);
        } else if (height >= 640 && width >= 480) {
            size.set(Size.LARGE);
        } else if (height >= 470 && width >= 320) {
            size.set(Size.NORMAL);
        } else if (height >= 426 && width >= 320) {
            size.set(Size.SMALL);
        } else
            size.set(Size.XSMALL);
    }

    /**
     * Updates the orientation property
     * <p>
     * On Desktop - updates size
     */
    void checkOrientation(double width, double height) {

        //TODO: is this even possible? And if yes: what should we do?
        /*if ((long) (width) == 0 || (long) (height) == 0) {
            return;
        }*/
        if(width < 0 || height < 0)
            return;

        if (width > height) {
            orientation.set(Orientation.LANDSCAPE);
        } else {
            orientation.set(Orientation.PORTRAIT);
        }
    }

    /**
     * get the current size of the specified region
     * @return current size
     */
    public ObjectProperty<Size> sizeProperty() {
        return size;
    }

    /**
     * get the current size of the specified region
     * @return current size
     */
    public Size getSize() {
        return size.get();
    }

    /**
     * get the current orientation of the specified region
     * @return current orientation
     */
    public ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    /**
     * get the current orientation of the specified region
     * @return current orientation
     */
    public Orientation getOrientation() {
        return orientation.get();
    }

    /**
     * get dpi factor of the device
     * @return dpi factor
     */
    public double getDpiFactor() {
        return dpiFactor.get();
    }

    /**
     * get dpi factor of the device
     * @return dpi factor
     */
    public DoubleProperty dpiFactorProperty() {
        return dpiFactor;
    }

    /**
     * get resolution of the device
     * @return resolution
     */
    public Resolution getResolution() {
        return resolution.get();
    }

    /**
     * get resolution of the device
     * @return resolution
     */
    public ObjectProperty<Resolution> resolutionProperty() {
        return resolution;
    }

    /**
     * enum with different orientation types
     */
    public enum Orientation {
        LANDSCAPE, PORTRAIT
    }

    /**
     * ordered enum with different size types
     */
    public enum Size {
        XSMALL,
        SMALL,
        NORMAL,
        LARGE,
        XLARGE
    }

    /**
     * ordered enum with different resolution types
     */
    public enum Resolution {
        LDPI, MDPI, HDPI, XHDPI, XXHDPI, XXXHDPI
    }
}
