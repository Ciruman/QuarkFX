package com.zeiss.quarkfx.viewhandler;

import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

/**
 * interface created to enable users to use Afterburner or similar injectionframeworks
 */
public interface IView {
    /**
     *
     * @return has to return the class that is a VIEW _and_ a FXMLView
     */
    @NotNull Parent getView();
}
