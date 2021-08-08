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
public class Status implements Serializable {

    public final static String STATUS_DONE = "done";
    public final static String STATUS_HAS = "has";
    public final static String STATUS_FAIL = "fail";
    public final static String STATUS_UNAUTORIZE = "unauthorize";
    public final static String STATUS_NOT_ACCESS = "not_access";
    public final static String STATUS_NOT_FOUND = "not_found";
    public final static String STATUS_LIMIT = "limit";
    public final static String STATUS_KICKED = "kicked";
    public final static String STATUS_VERSION = "version";

    public final static String STATUS_REG_LIMIT_DAY = "regday";
    public final static String STATUS_REG_LIMIT_ALL = "regall";
    public final static String STATUS_REG_BAN = "regban";
    public final static String STATUS_LOGIN_BAN = "loginban";
}
