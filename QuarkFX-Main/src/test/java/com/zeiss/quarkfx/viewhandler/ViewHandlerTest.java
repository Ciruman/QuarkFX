package com.zeiss.quarkfx.viewhandler;

import com.zeiss.quarkfx.QuarkFXApplication;
import com.zeiss.quarkfx.IGluon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ViewHandlerTest {

    //Rules
    @Rule
    public ExpectedException exceptionHandler = ExpectedException.none();

    //BeforeAfter

    /* STARTUP TEST */
    @Test
    public void defaultVariableValues() {
        //Setup
        QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);

        //Execute
        ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);

        //Validate
        assertNull("HomeView should be null after initialization", viewHandler.getHomeView());
        assertNull("CurrentViewType should be null after initialization", viewHandler.getCurrentViewType());
    }

    /* REGISTER TEST */
    @Test
    public void registerView() {
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);

        //Validate
        assertTrue(viewHandler.registerView(iViewPackage));
        assertTrue(viewHandler.getRegisteredView(iViewPackage.getID()).isPresent());
    }

    @Test
    public void registerDuplicateID() {
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);

        //Validate
        assertFalse(viewHandler.registerView(iViewPackage));
    }

    /* HOMEVIEW TEST */
    @Test
    public void setUnregisteredHomeView() {
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);

        //Validate
        assertFalse("Setting an unregistered HomeView worked 0.o", viewHandler.setHomeView("ID"));
    }

    @Test
    public void setHomeView() {
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage); //should not fail

        //Validate
        assertTrue("Failed setting registered HomeView", viewHandler.setHomeView("ID"));
        assertNotNull(viewHandler.getHomeView());
    }

    @Test
    public void switchViewWithoutStartup() {
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);

        exceptionHandler.expect(IllegalStateException.class);
        viewHandler.switchView("ID");
    }

    /* SWITCHVIEW TEST */

    @Test
    public void switchViewWithoutGluonViews() {
        //FIXME this shouldn't work without provided views
        //Setup
        final IGluon QuarkFXApplication = mock(IGluon.class);
        when(QuarkFXApplication.isViewPresent(Mockito.anyString())).thenReturn(false);

        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");
        when(iViewPackage.getDefaultOrientation()).thenReturn(OrientationHandler.Orientation.LANDSCAPE);
        when(iViewPackage.getDefaultSize()).thenReturn(OrientationHandler.Size.LARGE);

        OrientationHandler orientationHandler = mock(OrientationHandler.class);
        ObjectProperty<OrientationHandler.Size> size = new SimpleObjectProperty<>(OrientationHandler.Size.NORMAL);
        when(orientationHandler.sizeProperty()).thenReturn(size);
        ObjectProperty<OrientationHandler.Orientation> orientation = new SimpleObjectProperty<>(OrientationHandler.Orientation.PORTRAIT);
        when(orientationHandler.orientationProperty()).thenReturn(orientation);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);
        viewHandler.startUp(orientationHandler);

        assertNull(viewHandler.getCurrentViewType());
        exceptionHandler.expect(NullPointerException.class);
        exceptionHandler.expectMessage("Can not find any View to use");
        viewHandler.switchView(iViewPackage.getID());
        assertNull(iViewPackage.getID(), viewHandler.getCurrentViewType());
    }

    @Test
    public void switchView() {
        //Setup
        final IGluon QuarkFXApplication = mock(IGluon.class);
        when(QuarkFXApplication.isViewPresent(Mockito.anyString())).thenReturn(false);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");
        doReturn(IViewMock.class).when(iViewPackage).getView(Mockito.any(OrientationHandler.Size.class), Mockito.any(OrientationHandler.Orientation.class));
        OrientationHandler orientationHandler = mock(OrientationHandler.class);
        ObjectProperty<OrientationHandler.Size> size = new SimpleObjectProperty<>(OrientationHandler.Size.NORMAL);
        when(orientationHandler.sizeProperty()).thenReturn(size);
        ObjectProperty<OrientationHandler.Orientation> orientation = new SimpleObjectProperty<>(OrientationHandler.Orientation.PORTRAIT);
        when(orientationHandler.orientationProperty()).thenReturn(orientation);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);
        viewHandler.startUp(orientationHandler);

        //Validate
        assertNull(viewHandler.getCurrentViewType());
        viewHandler.switchView(iViewPackage.getID());
        assertEquals(iViewPackage.getID(), viewHandler.getCurrentViewType());
    }

    public void todo(){
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");

        OrientationHandler orientationHandler = mock(OrientationHandler.class);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);
        viewHandler.startUp(orientationHandler);

        assertNull(viewHandler.getCurrentViewType());
        viewHandler.switchView(iViewPackage.getID());
        assertEquals(iViewPackage.getID(), viewHandler.getCurrentViewType());
        //TODO check if current view is correct

        //switching view when ViewHandler is not initialized

        //switching to not existing view

        //switching to existing view

        //switching to homeview

        //switching to null

        //What if Orientationhandler has some funny values

        //switch when default values are not supplied

        //switching in specific size/orientation

        //switching automatically on size / orientation change

        //switching to same view
        fail("TODO"); //TODO implement
    }

    @Test
    public void switchViewAutomatic() {
        //Setup
        final IGluon QuarkFXApplication = mock(IGluon.class);
        when(QuarkFXApplication.isViewPresent(Mockito.anyString())).thenReturn(false);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");
        doReturn(IViewMock.class).when(iViewPackage).getView(Mockito.any(OrientationHandler.Size.class), Mockito.any(OrientationHandler.Orientation.class));
        OrientationHandler orientationHandler = mock(OrientationHandler.class);
        ObjectProperty<OrientationHandler.Size> size = new SimpleObjectProperty<>(OrientationHandler.Size.NORMAL);
        when(orientationHandler.sizeProperty()).thenReturn(size);
        ObjectProperty<OrientationHandler.Orientation> orientation = new SimpleObjectProperty<>(OrientationHandler.Orientation.PORTRAIT);
        when(orientationHandler.orientationProperty()).thenReturn(orientation);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);
        viewHandler.startUp(orientationHandler);
        viewHandler.switchView(iViewPackage.getID());

        //Validate
        assertEquals(iViewPackage.getID(), viewHandler.getCurrentViewType());
        size.setValue(OrientationHandler.Size.LARGE);
        String expectedName = ViewNameGenerator.generateViewName(iViewPackage.getID(), size.get(), orientation.get());
        Mockito.verify(QuarkFXApplication).isViewPresent(expectedName);
        orientation.setValue(OrientationHandler.Orientation.LANDSCAPE);
        String expectedName2 = ViewNameGenerator.generateViewName(iViewPackage.getID(), size.get(), orientation.get());
        Mockito.verify(QuarkFXApplication).isViewPresent(expectedName2);
        assertEquals(iViewPackage.getID(), viewHandler.getCurrentViewType());
    }

    @Test
    public void switchViewWithWrongDefaults() {
        //Setup
        final IGluon QuarkFXApplication = mock(IGluon.class);
        when(QuarkFXApplication.isViewPresent(Mockito.anyString())).thenReturn(false);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");
        when(iViewPackage.getDefaultOrientation()).thenReturn(OrientationHandler.Orientation.PORTRAIT);
        when(iViewPackage.getDefaultSize()).thenReturn(OrientationHandler.Size.NORMAL);
        doReturn(IViewMock.class).when(iViewPackage).getView(OrientationHandler.Size.NORMAL, OrientationHandler.Orientation.LANDSCAPE);
        OrientationHandler orientationHandler = mock(OrientationHandler.class);
        ObjectProperty<OrientationHandler.Size> size = new SimpleObjectProperty<>(OrientationHandler.Size.NORMAL);
        when(orientationHandler.sizeProperty()).thenReturn(size);
        ObjectProperty<OrientationHandler.Orientation> orientation = new SimpleObjectProperty<>(OrientationHandler.Orientation.PORTRAIT);
        when(orientationHandler.orientationProperty()).thenReturn(orientation);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);
        viewHandler.startUp(orientationHandler);

        //Validate
        exceptionHandler.expect(NullPointerException.class);
        exceptionHandler.expectMessage("Can not find any View to use");
        assertNull(viewHandler.getCurrentViewType());
        viewHandler.switchView(iViewPackage.getID());
        assertEquals(iViewPackage.getID(), viewHandler.getCurrentViewType());
    }

    @Test
    public void switchViewWithDefaults() {
        //Setup
        final IGluon QuarkFXApplication = mock(IGluon.class);
        when(QuarkFXApplication.isViewPresent(Mockito.anyString())).thenReturn(false);
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");
        when(iViewPackage.getDefaultOrientation()).thenReturn(OrientationHandler.Orientation.LANDSCAPE);
        when(iViewPackage.getDefaultSize()).thenReturn(OrientationHandler.Size.LARGE);
        //when(iViewPackage.getView(OrientationHandler.Size.LARGE, OrientationHandler.Orientation.LANDSCAPE)).thenReturn(IViewMock.class);
        doReturn(IViewMock.class).when(iViewPackage).getView(OrientationHandler.Size.LARGE, OrientationHandler.Orientation.LANDSCAPE);
        OrientationHandler orientationHandler = mock(OrientationHandler.class);
        ObjectProperty<OrientationHandler.Size> size = new SimpleObjectProperty<>(OrientationHandler.Size.NORMAL);
        when(orientationHandler.sizeProperty()).thenReturn(size);
        ObjectProperty<OrientationHandler.Orientation> orientation = new SimpleObjectProperty<>(OrientationHandler.Orientation.PORTRAIT);
        when(orientationHandler.orientationProperty()).thenReturn(orientation);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.registerView(iViewPackage);
        viewHandler.startUp(orientationHandler);

        //Validate
        assertNull(viewHandler.getCurrentViewType());
        viewHandler.switchView(iViewPackage.getID());
        assertEquals(iViewPackage.getID(), viewHandler.getCurrentViewType());
    }

    @Test
    public void switchViewWithNoViews() {
        //Setup
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);

        OrientationHandler orientationHandler = mock(OrientationHandler.class);
        ObjectProperty<OrientationHandler.Size> size = new SimpleObjectProperty<>(OrientationHandler.Size.NORMAL);
        when(orientationHandler.sizeProperty()).thenReturn(size);
        ObjectProperty<OrientationHandler.Orientation> orientation = new SimpleObjectProperty<>(OrientationHandler.Orientation.PORTRAIT);
        when(orientationHandler.orientationProperty()).thenReturn(orientation);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);
        viewHandler.startUp(orientationHandler);

        //Validate
        exceptionHandler.expect(NoSuchElementException.class);
        viewHandler.switchView("ID");
    }

    class IViewMock implements IView {
        @Override
        public @NotNull Parent getView() {
            return new Button("This should not have happened");
        }
    }

/*
    @Test
    public void startUp(){
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);

        //TODO test default zustand (null parameter)
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);

        exceptionHandler.expect(IllegalStateException.class);
        exceptionHandler.expectMessage("Didn't quite expect null here...");
        viewHandler.startUp(null);
    }

    @Test
    public void switchView() {
        //FIXME remove powermock since I only need empty Gluon methods
        //needs not empty gluon
        mockStatic(QuarkFXApplication.class);
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        //Logger
        LoggerP silentLogger = mock(LoggerP.class);
        when(Log.getLogger()).thenReturn(silentLogger);

        //needs not empty IViewPackage
        IViewPackage iViewPackage = mock(IViewPackage.class);
        when(iViewPackage.getID()).thenReturn("ID");

        //needs fake OrientationAndSizeListener
        OrientationHandler orientationListener = mock(OrientationHandler.class);

        //create Viewhandler
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);


        //needs registered IViewPackage
        viewHandler.registerView(iViewPackage);

        //TODO check if current view is correct

        //switching view when ViewHandler is not initialized

        //startup viewhandler
        viewHandler.startUp(orientationListener);

        //switching to not existing view

        //switching to existing view

        //switching to homeview

        //switching to null

        //What if Orientationhandler has some funny values

        //switching in specific size/orientation

        //switching automatically on size / orientation change

        //switching to same view
        fail("TODO"); //TODO implement
    }

    @Test
    public void NotregisterView() {
        //needs empty gluon
        mockStatic(QuarkFXApplication.class);
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        LoggerP silentLogger = mock(LoggerP.class);
        when(Log.getLogger()).thenReturn(silentLogger);
        //needs empty IViewPackage
        IViewPackage iViewPackage = mock(IViewPackage.class);

        //create Viewhandler
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);

        //get not registered view
        assertFalse(viewHandler.getRegisteredView("ID").isPresent());
    }


    @Test
    public void registerView() {
        //Setup
        mockStatic(QuarkFXApplication.class);
        final QuarkFXApplication QuarkFXApplication = mock(QuarkFXApplication.class);
        LoggerP silentLogger = mock(LoggerP.class);
        when(Log.getLogger()).thenReturn(silentLogger);
        IViewPackage iViewPackage = mock(IViewPackage.class);

        //Execute
        final ViewHandler viewHandler = new ViewHandler(QuarkFXApplication);


        viewHandler.registerView(iViewPackage);

        //register view
        when(iViewPackage.getID()).thenReturn("ID");
        assertTrue(viewHandler.registerView(iViewPackage));

        //register duplicate view (same id)
        assertFalse(viewHandler.registerView(iViewPackage));

        //register null view
        exceptionHandler.expect(NullPointerException.class);
        exceptionHandler.expectMessage("IViewPackage or its ID is null");
        viewHandler.registerView(null);

        //get registered view
        assertTrue(viewHandler.getRegisteredView("ID").isPresent());
    }*/
}