package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 03.01.17.
 */

public class PhotoRequest extends BaseRequest implements Serializable {

    private String name;
    private String description;
    private boolean privated;
    private String parent;

    public PhotoRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivated() {
        return privated;
    }

    public void setPrivated(boolean privated) {
        this.privated = privated;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }



}
