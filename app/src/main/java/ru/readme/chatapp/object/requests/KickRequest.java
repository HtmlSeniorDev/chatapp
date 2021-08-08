package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 15.12.16.
 */

public class KickRequest extends BaseRequest implements Serializable {

    private String room;
    private String kicked;

    public KickRequest() {
    }

    public KickRequest(String room, String kicked) {
        this.room = room;
        this.kicked = kicked;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setKicked(String kicked) {
        this.kicked = kicked;
    }

    public String getKicked() {
        return kicked;
    }

}