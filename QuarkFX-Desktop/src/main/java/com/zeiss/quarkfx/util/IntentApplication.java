package com.zeiss.quarkfx.util;

import com.google.gson.annotations.Expose;
import com.zeiss.quarkfx.platformindependant.IntentP;

import java.util.Set;

/**
 * Raw Format for any other Application using QuarkFX so Desktop-Intents can be called
 */
public class IntentApplication {
    @Expose
    public String label;
    @Expose
    public String version;
    @Expose
    public String description;
    @Expose
    public String iconPath;
    @Expose
    public Set<IntentP> intentfilters;

    //get set by loader
    public String main;
    public String jarfilename;
}
