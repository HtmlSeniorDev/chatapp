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
 *
 * @author dima
 */
public class MessagesResponse extends BaseResponse implements Serializable {

    private List<MessageResponse> messages;

    public MessagesResponse() {
    }

    public static class Builder {

        public Builder() {
            this.item = new MessagesResponse();
        }
        private MessagesResponse item;

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public Builder setMessages(final List<MessageResponse> messages) {
            this.item.messages = messages;
            return this;
        }

        public MessagesResponse build() {
            return this.item;
        }
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }


    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }


}
