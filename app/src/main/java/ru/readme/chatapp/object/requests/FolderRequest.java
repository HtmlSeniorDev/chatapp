package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 03.01.17.
 */

public class FolderRequest extends BaseRequest implements Serializable {

    private String id;
    private String description;
    private boolean privated;
    private String name;
    private String parent;

    public FolderRequest() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }


}
