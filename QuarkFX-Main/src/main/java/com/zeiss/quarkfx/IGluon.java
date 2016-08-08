package com.zeiss.quarkfx;

import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;

import java.util.function.Supplier;

/**
 * A Gluon interface to make proper tests possible
 */
public interface IGluon {
    
    boolean isViewPresent(String name);

    void switchView(String name, ViewStackPolicy viewStackPolicy);

    void addViewFactory(String name, Supplier<View> supplier);
}
