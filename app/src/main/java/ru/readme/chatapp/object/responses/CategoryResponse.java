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
public class CategoryResponse  extends  BaseResponse implements Serializable{

    private String name;
    private String id;
    private int mask;

    public CategoryResponse() {
    }

    public static class Builder {

        public Builder() {
            this.item = new CategoryResponse();
        }
        private CategoryResponse item;

        public Builder setName(final String name) {
            this.item.name = name;
            return this;
        }

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public CategoryResponse build() {
            return this.item;
        }
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
