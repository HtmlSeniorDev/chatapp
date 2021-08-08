package ru.readme.chatapp.object.responses;

/**
 * Created by dima on 04.01.17.
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dima
 */
public class GiftsResponse extends BaseResponse implements Serializable{

    private List<GiftResponse> gifts = new ArrayList<GiftResponse>();

    public GiftsResponse() {
    }

    public GiftsResponse(String status) {
        super(status);
    }

    public static class Builder {

        public Builder() {
            this.item = new GiftsResponse();
        }
        private GiftsResponse item;

        public Builder setGifts(final List<GiftResponse> gifts) {
            this.item.gifts = gifts;
            return this;
        }

        public GiftsResponse build() {
            return this.item;
        }
    }

    public List<GiftResponse> getGifts() {
        return gifts;
    }

    public void setGifts(List<GiftResponse> gifts) {
        this.gifts = gifts;
    }



}