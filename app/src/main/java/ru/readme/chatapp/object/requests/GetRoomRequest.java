/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 *
 * @author dima
 */
public class GetRoomRequest extends BaseRequest implements Serializable {


    private String room;
    private int messagesCount;



    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(int messagesCount) {
        this.messagesCount = messagesCount;
    }

}
