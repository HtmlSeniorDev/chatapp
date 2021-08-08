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
public class RemoveMessageRequest  extends BaseRequest implements Serializable {


    private String message;



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
