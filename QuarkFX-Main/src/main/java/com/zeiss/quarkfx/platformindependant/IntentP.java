package com.zeiss.quarkfx.platformindependant;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * An Intent Object containing important information about receiver and data
 */
public class IntentP {
    @Expose
    private String data;
    @Expose
    private String action;
    @NotNull
    @Expose
    private Set<String> categories = new HashSet<>();
    @Expose
    private String receiverMainClass;

    //like in android you have to set it yourself
    private String mimetype = "*/*";

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @NotNull
    public Set<String> getCategories() {
        return categories;
    }

    /**
     * add a category like in android
     * @param category the category to add
     */
    public void addCategories(String category) {
        this.categories.add(category);
    }

    public String getReceiverMainClass() {
        return receiverMainClass;
    }

    public void setReceiverMainClass(String receiverMainClass) {
        this.receiverMainClass = receiverMainClass;
    }

    //TODO can there be intents with no data?
    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    /**
     * checks if receiving class is valid
     * @return boolean result
     */
    public boolean hasSpecificReceiver() {
        return receiverMainClass != null && !receiverMainClass.isEmpty();
    }

    public String getMimeType() {
        return mimetype;
    }

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }
}
