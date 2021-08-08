package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 29.11.16.
 */

public class UsersResponse extends BaseResponse implements Serializable {
    private List<UserResponse> users = new ArrayList<UserResponse>();

    public UsersResponse() {
    }

    public UsersResponse(String status) {
        this.status = status;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }


    public static class Builder {

        private UsersResponse item;

        public Builder() {
            this.item = new UsersResponse();
        }

        public Builder setUsers(final List<UserResponse> users) {
            this.item.users = users;
            return this;
        }

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public UsersResponse build() {
            return this.item;
        }
    }
}
