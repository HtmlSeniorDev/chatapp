/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dima
 */
public class MessageResponse extends BaseResponse implements Serializable {

    private String id;
    private String message;
    private boolean hideNick = false;
    private boolean system = false;

    private UserResponse user;
    private Date createdAt;
    private List<AttachmentResponse> attachments = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public boolean isHideNick() {return hideNick;}

    public void setHideNick(boolean hideNick) {
        this.hideNick = hideNick;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<AttachmentResponse> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentResponse> attachments) {
        this.attachments = attachments;
    }


    public static class Builder {

        private MessageResponse item;

        public Builder() {
            this.item = new MessageResponse();
        }

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setMessage(final String message) {
            this.item.message = message;
            return this;
        }

        public Builder setHideNick(final boolean hideNick) {
            this.item.hideNick = hideNick;
            return this;
        }

        public Builder setSystem(final boolean system) {
            this.item.system = system;
            return this;
        }

        public Builder setUser(final UserResponse user) {
            this.item.user = user;
            return this;
        }

        public Builder setCreatedAt(final Date createdAt) {
            this.item.createdAt = createdAt;
            return this;
        }

        public Builder setAttachments(final List<AttachmentResponse> attachments) {
            this.item.attachments = attachments;
            return this;
        }

        public MessageResponse build() {
            return this.item;
        }
    }


}
