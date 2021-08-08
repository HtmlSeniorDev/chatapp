/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * @author dima
 */
public class MessagesRequest  extends BaseRequest implements Serializable {

    public final static int FIND_TYPE_BEFORE = 1;
    public final static int FIND_TYPE_AFTER = 2;

    private long lastDate;
    private String place;
    private int count;
    private int findType;



    public int getFindType() {
        return findType;
    }

    public void setFindType(int findType) {
        this.findType = findType;
    }


    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
