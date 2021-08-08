/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.List;

/**
 * @author dima
 */
public class RoomResponse extends BaseResponse  implements Serializable {

    private String id;
    private String name;
    private int mask;
    private MessageResponse lastMessage;
    private List<MessageResponse> messages;
    private int peopleCount;

    public RoomResponse(String st) {
        status = st;
    }

    public RoomResponse() {
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }



    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }






    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageResponse lastMessage) {
        this.lastMessage = lastMessage;
    }

    public static class Builder {

        public Builder() {
            this.item = new RoomResponse();
        }
        private RoomResponse item;

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setName(final String name) {
            this.item.name = name;
            return this;
        }

        public Builder setMask(final int mask) {
            this.item.mask = mask;
            return this;
        }

        public Builder setLastMessage(final MessageResponse lastMessage) {
            this.item.lastMessage = lastMessage;
            return this;
        }

        public Builder setMessages(final List<MessageResponse> messages) {
            this.item.messages = messages;
            return this;
        }

        public Builder setPeopleCount(final int peopleCount) {
            this.item.peopleCount = peopleCount;
            return this;
        }

        public RoomResponse build() {
            return this.item;
        }
    }



}
