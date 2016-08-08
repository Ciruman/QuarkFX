package com.zeiss.quarkfx.viewhandler;

import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import com.zeiss.quarkfx.IGluon;
import com.zeiss.quarkfx.exceptions.YouDidShitException;
import com.zeiss.quarkfx.logging.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class handles automated view switching.
 * You have to register a view package that defines the used view for each orientation and size
 * The ViewHandler also supports automated switching to the right view when the orientation or size of the device has changed
 */
public class ViewHandler {

    private final IGluon gluon;
    private final Set<IViewPackage> registeredViews = new HashSet<>();
    //FIXME: remove this list when gluon bug is fixed (instantiating the view multiple times when asking if it exists)
    private final Map<String, View> addedViews = new HashMap<>();

    @Nullable
    private OrientationHandler orientationHandler;
    @Nullable
    private String currentViewType;
    @Nullable
    private String homeView = null;


    /**
     * creating a viewhandler for this instance of IGluon
     *
     * @param gluon the backend for view switching
     */
    public ViewHandler(@NotNull IGluon gluon) {
        this.gluon = gluon;
        //default
        currentViewType = null;
    }

    @Nullable
    public String getCurrentViewType() {
        return currentViewType;
    }

    @Nullable
    public String getHomeView() {
        return homeView;
    }

    public boolean setHomeView(@NotNull String viewType) {
        if (getRegisteredView(viewType).isPresent()) {
            this.homeView = viewType;
            return true;
        }
        return false;
    }

    /**
     * starting the ViewHandler
     *
     * @param orientationHandler the orientation handler for reacting to orientation changes
     */
    public void startUp(@NotNull OrientationHandler orientationHandler) {
        //enable automatic view switching on size and orientation change
        this.orientationHandler = orientationHandler;
        orientationHandler.sizeProperty().addListener(new ChangeListener<OrientationHandler.Size>() {

            @Override
            public void changed(ObservableValue<? extends OrientationHandler.Size> observable, OrientationHandler.Size oldValue, OrientationHandler.Size newValue) {
                if (currentViewType != null) {
                    ViewHandler.this.switchView(currentViewType, ViewStackPolicy.SKIP);
                }
            }
        });
        orientationHandler.orientationProperty().addListener(new ChangeListener<OrientationHandler.Orientation>() {

            @Override
            public void changed(ObservableValue<? extends OrientationHandler.Orientation> observable, OrientationHandler.Orientation oldValue, OrientationHandler.Orientation newValue) {
                if (currentViewType != null) {
                    ViewHandler.this.switchView(currentViewType, ViewStackPolicy.SKIP);
                }
            }
        });
    }

    /**
     * registers a viewType. No duplicate IDs allowed
     *
     * @param viewType viewType to be registered
     * @return success or not success - that's the question
     */
    public boolean registerView(@NotNull IViewPackage viewType) {
        if (viewType.getID().isEmpty()) {
            throw new NullPointerException("IViewPackage or its ID is empty");
        }

        if (getRegisteredView(viewType.getID()).isPresent()) {
            return false;
        }
        registeredViews.add(viewType);
        return true;
    }

    /**
     * searches for a registered view
     *
     * @param viewType The ID of the view to be searched
     * @return return the view or an empty optional
     */
    public Optional<IViewPackage> getRegisteredView(@NotNull String viewType) {
        if (viewType.isEmpty())
            return Optional.empty();

        IViewPackage viewTypeView = null;
        for (IViewPackage fv : registeredViews) {//check if Set contains this ID
            if (viewType.equals(fv.getID())) {
                viewTypeView = fv;
                break;
            }
        }
        return Optional.ofNullable(viewTypeView);
    }

    /**
     * Abbreviation so that ViewStackPolicy doesn't have to be provided
     *
     * @param viewType the view to switch to
     */
    public void switchView(@NotNull String viewType) {
        switchView(viewType, ViewStackPolicy.USE);
    }

    /**
     * switches to a new View. Rotation and Size is updated automatically
     *
     * @param viewType        the new Type of view (ID)
     * @param viewStackPolicy how to switch to the new view
     */
    public void switchView(@NotNull String viewType, @NotNull ViewStackPolicy viewStackPolicy) {
        if (orientationHandler == null) {
            throw new IllegalStateException("ViewHandler has not yet been initialized! - can't switch view yet");
        }
        if (viewType.isEmpty()) {
            throw new YouDidShitException("cant switch to null view");
        }
        //noinspection Since15
        Optional<IViewPackage> optional = getRegisteredView(viewType);
        //noinspection Since15
        if (optional.isPresent()) {
            String name = ViewNameGenerator.generateViewName(viewType, orientationHandler.sizeProperty().get(), orientationHandler.orientationProperty().get());
            //switch to view if view is already loaded
            if (gluon.isViewPresent(name)) {
                gluon.switchView(name, viewStackPolicy);
            } else {
                //noinspection Since15
                name = getView(optional.get(), orientationHandler.sizeProperty().get(), orientationHandler.orientationProperty().get());
                //noinspection Since15
                gluonSwitch(optional.get(), name,viewStackPolicy);
            }
            currentViewType = viewType;
        } else {
            throw new NoSuchElementException("This is the initial View or this ViewID does not exist");
        }
    }

    private void gluonSwitch(IViewPackage iViewPackage, String name, ViewStackPolicy viewStackPolicy){
        if (name != null) {
            gluon.switchView(name, viewStackPolicy);
        } else {
            name = getDefaultView(iViewPackage);
            if (!gluon.isViewPresent(name)) {
                name = getView(iViewPackage, iViewPackage.getDefaultSize(), iViewPackage.getDefaultOrientation());
            }
            if(name == null || name.isEmpty()){
                throw new NullPointerException("Can not find any View to use");
            }
            gluonSwitch(iViewPackage, name, viewStackPolicy);
        }
    }

    @Nullable
    private String getDefaultView(IViewPackage iViewPackage) {
        if(iViewPackage.getDefaultOrientation() == null || iViewPackage.getDefaultSize() == null)
            throw new YouDidShitException("no default Views specified");
        return ViewNameGenerator.generateViewName(iViewPackage.getID(), iViewPackage.getDefaultSize(), iViewPackage.getDefaultOrientation());
        // return getView(iViewPackage, iViewPackage.getDefaultSize(), iViewPackage.getDefaultOrientation());
    }

    /**
     * Gets a view from a viewPackage for the corresponding orientation and size
     * Loads the view and creates the view only if needed.
     *
     * @param viewType    The viewType which contains the view. The viewType has to return the class that is a VIEW _and_ a FXMLView
     * @param size        The size for the new view
     * @param orientation The orientation for the new view
     * @return The unique identifier of the new view
     */
    @Nullable
    private String getView(@NotNull IViewPackage viewType, OrientationHandler.Size size, OrientationHandler.Orientation orientation) {
        final Class<? extends IView> viewClass = viewType.getView(size, orientation);

        if (viewClass != null) {
            final String name = ViewNameGenerator.generateViewName(viewType.getID(), size, orientation);

            //disable intellij code inspection for optionals/suppliers etc
            //noinspection Since15
            gluon.addViewFactory(name, new Supplier<View>() {
                @Override
                public View get() {
                    if (!addedViews.containsKey(name)) {
                        try {
                            Constructor<? extends IView> con = null;
                            for (Constructor c : viewClass.getConstructors()) {
                                if (c.getParameterTypes().length == 1 && c.getParameterTypes()[0].equals(String.class)) {
                                    con = c;
                                }
                            }
                            Parent parent;
                            if (con == null) {
                                parent = viewClass.newInstance().getView();
                            } else {
                                parent = con.newInstance(name).getView();
                            }
                            if (!(parent instanceof View)) {
                                throw new YouDidShitException("You supplied an FXMLView but it also has to be a View or else Gluon can't handle it!");
                            }
                            View view = (View) parent;
                            if (con == null) {
                                view.setName(name);
                            }
                            addedViews.put(name, view);
                            return view;
                        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | YouDidShitException e) {
                            Log.ex(ViewHandler.class.getSimpleName(), "could not instantiate the specified view class", e);
                        }
                    }
                    return addedViews.get(name);
                }
            });
            return name;
        } else {
            return null;
        }
    }

    /**
     * switches to the previously specified homeView
     *
     * @param skip The ViewStackPolicy that should be used
     */
    public void switchToHome(@NotNull ViewStackPolicy skip) {
        String homeID = getHomeView();
        if (homeID != null) {
            switchView(homeID, skip);
        }
    }


}
