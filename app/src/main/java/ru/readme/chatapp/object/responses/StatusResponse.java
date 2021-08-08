/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 *
 * @author dima
 */
public class StatusResponse extends  BaseResponse implements Serializable{




    public StatusResponse(String status) {
        this.status = status;
    }

    public StatusResponse() {
    }
}
