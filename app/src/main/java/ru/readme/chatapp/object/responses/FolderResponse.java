package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dima on 03.01.17.
 */

public class FolderResponse extends BaseResponse implements Serializable {

    private String id;
    private String description;
    private boolean privated;
    private Date createdAt;
    private String name;
    private String parent;

    public FolderResponse() {
    }

    public FolderResponse(String status) {
        super(status);
    }

    public static class Builder {

        public Builder() {
            this.item = new FolderResponse();
        }
        private FolderResponse item;

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setDescription(final String description) {
            this.item.description = description;
            return this;
        }

        public Builder setPrivated(final boolean privated) {
            this.item.privated = privated;
            return this;
        }

        public Builder setCreatedAt(final Date createdAt) {
            this.item.createdAt = createdAt;
            return this;
        }

        public Builder setName(final String name) {
            this.item.name = name;
            return this;
        }

        public Builder setParent(final String parent) {
            this.item.parent = parent;
            return this;
        }

        public FolderResponse build() {
            return this.item;
        }
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
