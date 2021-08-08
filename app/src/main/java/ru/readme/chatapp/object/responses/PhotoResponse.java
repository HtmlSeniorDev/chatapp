package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author dima
 */
public class PhotoResponse extends BaseResponse implements Serializable {

    private String id;
    private Date createdAt;
    private String folder;
    private String description;
    private boolean privated;

    public PhotoResponse() {
    }

    public PhotoResponse(String status) {
        super(status);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {

        public Builder() {
            this.item = new PhotoResponse();
        }
        private PhotoResponse item;

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setCreatedAt(final Date createdAt) {
            this.item.createdAt = createdAt;
            return this;
        }

        public Builder setFolder(final String folder) {
            this.item.folder = folder;
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

        public PhotoResponse build() {
            return this.item;
        }
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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

}

