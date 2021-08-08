/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dima
 */
public class PMChatResponse extends BaseResponse implements Serializable {

    private String id;
    private List<UserResponse> users = new ArrayList<UserResponse>();
    private MessageResponse lastMessage;
    private long unreaded;

    public PMChatResponse(String status) {
        this.status = status;
    }

    public PMChatResponse() {
    }


    public long getUnreaded() {
        return unreaded;
    }

    public void setUnreaded(long unreaded) {
        this.unreaded = unreaded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }

    public MessageResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageResponse lastMessage) {
        this.lastMessage = lastMessage;
    }

    public static class Builder {

        public Builder() {
            this.item = new PMChatResponse();
        }
        private PMChatResponse item;

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setUsers(final List<UserResponse> users) {
            this.item.users = users;
            return this;
        }

        public Builder setLastMessage(final MessageResponse lastMessage) {
            this.item.lastMessage = lastMessage;
            return this;
        }

        public PMChatResponse build() {
            return this.item;
        }
    }

}
