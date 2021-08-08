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
public class PMChatsResponse extends  BaseResponse implements Serializable{


    private List<PMChatResponse> chats = new ArrayList();

    public PMChatsResponse() {
    }

    public PMChatsResponse(String status) {
        this.status = status;
    }



    public List<PMChatResponse> getChats() {
        return chats;
    }

    public void setChats(List<PMChatResponse> chats) {
        this.chats = chats;
    }

    public static class Builder {

        public Builder() {
            this.item = new PMChatsResponse();
        }
        private PMChatsResponse item;

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public Builder setChats(final List<PMChatResponse> chats) {
            this.item.chats = chats;
            return this;
        }

        public PMChatsResponse build() {
            return this.item;
        }
    }

}
