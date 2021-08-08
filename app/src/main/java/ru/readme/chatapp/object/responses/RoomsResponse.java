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
public class RoomsResponse extends BaseResponse implements Serializable{
    public RoomsResponse() {
    }

    private List<CategoryResponse> categories = new ArrayList<CategoryResponse>();
    private List<RoomResponse> rooms = new ArrayList<RoomResponse>();


    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponse> categories) {
        this.categories = categories;
    }

    public List<RoomResponse> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomResponse> rooms) {
        this.rooms = rooms;
    }

    public static class Builder {

        public Builder() {
            this.item = new RoomsResponse();
        }
        private RoomsResponse item;

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public Builder setCategories(final List<CategoryResponse> categories) {
            this.item.categories = categories;
            return this;
        }

        public Builder setRooms(final List<RoomResponse> rooms) {
            this.item.rooms = rooms;
            return this;
        }

        public RoomsResponse build() {
            return this.item;
        }
    }

}
