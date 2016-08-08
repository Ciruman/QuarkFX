package com.zeiss.quarkfx.viewhandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.zeiss.quarkfx.viewhandler.OrientationHandler.Size.XSMALL;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class OrientationHandlerTest {

    @Test
    public void testDefaultValues() {
        //Setup

        //Execute
        OrientationHandler handler = new OrientationHandler();

        //Validate
        assertEquals(OrientationHandler.Orientation.PORTRAIT, handler.getOrientation());
        assertEquals(OrientationHandler.Size.NORMAL, handler.getSize());
        assertEquals(1D, handler.getDpiFactor(), 0.001);
        assertEquals(OrientationHandler.Resolution.MDPI, handler.getResolution());
    }

    @Test
    public void testResolution() {
        //Setup

        //Execute
        OrientationHandler handler = new OrientationHandler();

        //Validate
        handler.checkDPI(3.1);
        assertEquals(OrientationHandler.Resolution.XXXHDPI, handler.getResolution());
        handler.checkDPI(2.1);
        assertEquals(OrientationHandler.Resolution.XXHDPI, handler.getResolution());
        handler.checkDPI(1.6);
        assertEquals(OrientationHandler.Resolution.XHDPI, handler.getResolution());
        handler.checkDPI(1.1);
        assertEquals(OrientationHandler.Resolution.HDPI, handler.getResolution());
        handler.checkDPI(0.76);
        assertEquals(OrientationHandler.Resolution.MDPI, handler.getResolution());
        handler.checkDPI(0.1);
        assertEquals(OrientationHandler.Resolution.LDPI, handler.getResolution());
    }

    @Test
    public void testSizeLandscape() {
        //Setup

        //Execute
        OrientationHandler handler = new OrientationHandler();
        handler.orientationProperty().setValue(OrientationHandler.Orientation.LANDSCAPE);

        //Validate
        handler.checkSize(960, 720);
        assertEquals(OrientationHandler.Size.XLARGE, handler.getSize());
        handler.checkSize(640, 480);
        assertEquals(OrientationHandler.Size.LARGE, handler.getSize());
        handler.checkSize(470, 320);
        assertEquals(OrientationHandler.Size.NORMAL, handler.getSize());
        handler.checkSize(426, 320);
        assertEquals(OrientationHandler.Size.SMALL, handler.getSize());
        handler.checkSize(960, 100);
        assertEquals(XSMALL, handler.getSize());
    }

    @Test
    public void testSizePortrait() {
        //Setup
        OrientationHandler handler = new OrientationHandler();
        handler.orientationProperty().setValue(OrientationHandler.Orientation.PORTRAIT);

        //Execute

        //Validate
        handler.checkSize(720, 960);
        assertEquals(OrientationHandler.Size.XLARGE, handler.getSize());
        handler.checkSize(480, 640);
        assertEquals(OrientationHandler.Size.LARGE, handler.getSize());
        handler.checkSize(320, 470);
        assertEquals(OrientationHandler.Size.NORMAL, handler.getSize());
        handler.checkSize(320, 426);
        assertEquals(OrientationHandler.Size.SMALL, handler.getSize());
        handler.checkSize(100, 960);
        assertEquals(XSMALL, handler.getSize());
    }

    @Test
    public void testGSOrientation() {
        //Setup
        OrientationHandler handler = new OrientationHandler();

        //Execute

        //Validate
        handler.orientationProperty().set(OrientationHandler.Orientation.LANDSCAPE);
        assertEquals(handler.orientationProperty().get(), handler.getOrientation());
        handler.orientationProperty().set(OrientationHandler.Orientation.PORTRAIT);
        assertEquals(handler.orientationProperty().get(), handler.getOrientation());
    }

    @Test
    public void testGSSize() {
        //Setup
        OrientationHandler handler = new OrientationHandler();

        //Execute

        //Validate
        for(OrientationHandler.Size s: OrientationHandler.Size.values()) {
            handler.sizeProperty().set(s);
            assertEquals(handler.sizeProperty().get(), handler.getSize());
        }
    }

    @Test
    public void testOrientationPortrait() {
        //Setup
        OrientationHandler handler = new OrientationHandler();

        //Execute
        handler.checkOrientation(100, 200);

        //Validate
        assertEquals(OrientationHandler.Orientation.PORTRAIT, handler.getOrientation());
    }

    @Test
    public void testOrientationLandscape() {
        //Setup
        OrientationHandler handler = new OrientationHandler();

        //Execute
        handler.checkOrientation(200, 100);

        //Validate
        assertEquals(OrientationHandler.Orientation.LANDSCAPE, handler.getOrientation());
    }

    @Test
    public void testOrientationNegative() {
        //Setup
        OrientationHandler handler = new OrientationHandler();
        OrientationHandler.Orientation or = handler.getOrientation();

        //Execute

        //Validate
        handler.checkOrientation(-100, -200);
        assertEquals(or, handler.getOrientation());//keep old value
        handler.checkOrientation(-200, -100);
        assertEquals(or, handler.getOrientation());//keep old value
    }

    @Test
    public void testOrientationEqual() {
        //Setup
        OrientationHandler handler = new OrientationHandler();

        //Execute
        handler.checkOrientation(100, 100);

        //Validate
        assertEquals(OrientationHandler.Orientation.PORTRAIT, handler.getOrientation());
    }

    @Test
    public void testOrientationZero() {
        //Setup
        OrientationHandler handler = new OrientationHandler();

        //Execute

        //Validate
        handler.checkOrientation(0, 100);
        assertEquals(OrientationHandler.Orientation.PORTRAIT, handler.getOrientation());
        handler.checkOrientation(100, 0);
        assertEquals(OrientationHandler.Orientation.LANDSCAPE, handler.getOrientation());
    }
}