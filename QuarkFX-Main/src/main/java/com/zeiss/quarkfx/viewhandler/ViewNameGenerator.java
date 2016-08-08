package com.zeiss.quarkfx.viewhandler;


import com.zeiss.quarkfx.QuarkFXApplication;
import org.jetbrains.annotations.NotNull;

/**
 * Generates names for views. These names are used to identify views
 */
class ViewNameGenerator {

    private ViewNameGenerator(){}


    /**
     * Generates a name for a specified view based on different properties of this view
     *
     * @param basename The ID of the viewPackage (IViewPackage) containing this view. Or any other unique string.
     * @param size The size property of the view
     * @param orientation The orientation property of the view
     * @return ID for this view
     */
    @NotNull
    public static String generateViewName(@NotNull String basename, @NotNull OrientationHandler.Size size, @NotNull OrientationHandler.Orientation orientation) {
        if (basename.equals(QuarkFXApplication.HOME_VIEW))
            return basename;
        return basename + " " + size.name() + " " + orientation.name() + " View";
    }

    /**
     * gets the ID of the package containing this view or more general its basename
     * @param completeName the full ID of this view
     * @return The basename of the view (mostly the IViewPackage-ID that contains the View)
     */
    @NotNull
    public static String getBaseName(@NotNull String completeName) {
        if (completeName.equals(QuarkFXApplication.HOME_VIEW))
            return completeName;
        String[] splits = completeName.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length - 3; i++) {
            sb.append(splits[i]).append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.trimToSize();
        return sb.toString();
    }

    /**
     * builds the identifying string for a default view
     * @param toBasename the name of he view package
     * @param orientation the orientation for a default view
     * @return the id
     */
    @NotNull
    public static String generateDefaultViewName(@NotNull String toBasename, @NotNull OrientationHandler.Orientation orientation) {
        return generateViewName(toBasename, OrientationHandler.Size.NORMAL, orientation);
    }
}
