package com.zeiss.quarkfx.viewhandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a collection of multiple view with the same content
 * but optimized for different orientations and/or sizes
 */
public interface IViewPackage {

    /**
     * The unique name of this collection of views
     * @return the id
     */
    @NotNull String getID();

    /**
     * a method that returns the right view for this orientation and size
     *
     * @param size the desired size
     * @param orientation the desired orientation
     *
     * @return an IView Class that can be instantiated when needed via reflection
     */
    @Nullable Class<? extends IView> getView(OrientationHandler.Size size, OrientationHandler.Orientation orientation);

    /**
     * @return default orientation
     */
    @NotNull OrientationHandler.Orientation getDefaultOrientation();

    /**
     *
     * @return default size
     */
    @NotNull OrientationHandler.Size getDefaultSize();
}
