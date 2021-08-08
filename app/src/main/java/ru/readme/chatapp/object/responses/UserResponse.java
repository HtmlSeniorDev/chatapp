/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dima
 */
public class UserResponse extends BaseResponse implements Serializable {

    private String id;
    private String nic;
    private int color;
    private String avatarLink;
    private String firstName;
    private String lastName;
    private int balance;
    private String city;
    private String email;
    private int sex;

    private Date bday;
    private String about;
    private int type;
    private Date registrationDate;
    private long unreadedMessages;
    private String photo;
    private Date lastVisit;
    private List<GiftResponse> gifts = new ArrayList<GiftResponse>();
    private List<NoticeResponse> notices = new ArrayList<NoticeResponse>();
    private UserResponse zags;
    private List<UserResponse> zagsRequest = new ArrayList<UserResponse>();

    public UserResponse() {
    }

    public UserResponse(String status) {
        this.status = status;
    }

    public int getBalance() {
        return balance;
    }

    public String getPhoto() {
        return photo;
    }

    public UserResponse getZags() {
        return zags;
    }

    public void setZags(UserResponse zags) {
        this.zags = zags;
    }

    public List<UserResponse> getZagsRequest() {
        return zagsRequest;
    }

    public void setZagsRequest(List<UserResponse> zagsRequest) {
        this.zagsRequest = zagsRequest;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }

    public List<GiftResponse> getGifts() {
        return gifts;
    }

    public void setGifts(List<GiftResponse> gifts) {
        this.gifts = gifts;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<NoticeResponse> getNotices() {
        return notices;
    }

    public void setNotices(List<NoticeResponse> notices) {
        this.notices = notices;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }


    public long getUnreadedMessages() {
        return unreadedMessages;
    }

    public void setUnreadedMessages(long unreadedMessages) {
        this.unreadedMessages = unreadedMessages;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getBday() {
        return bday;
    }

    public void setBday(Date bday) {
        this.bday = bday;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public static class Builder {

        public Builder() {
            this.item = new UserResponse();
        }
        private UserResponse item;

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setNic(final String nic) {
            this.item.nic = nic;
            return this;
        }
        public Builder setEmail(final String email) {
            this.item.email = email;
            return this;
        }

        public Builder setColor(final int color) {
            this.item.color = color;
            return this;
        }

        public Builder setAvatarLink(final String avatarLink) {
            this.item.avatarLink = avatarLink;
            return this;
        }

        public Builder setFirstName(final String firstName) {
            this.item.firstName = firstName;
            return this;
        }

        public Builder setLastName(final String lastName) {
            this.item.lastName = lastName;
            return this;
        }

        public Builder setBalance(final int balance) {
            this.item.balance = balance;
            return this;
        }

        public Builder setCity(final String city) {
            this.item.city = city;
            return this;
        }

        public Builder setSex(final int sex) {
            this.item.sex = sex;
            return this;
        }

        public Builder setBday(final Date bday) {
            this.item.bday = bday;
            return this;
        }

        public Builder setAbout(final String about) {
            this.item.about = about;
            return this;
        }

        public Builder setType(final int type) {
            this.item.type = type;
            return this;
        }

        public Builder setRegistrationDate(final Date registrationDate) {
            this.item.registrationDate = registrationDate;
            return this;
        }

        public Builder setUnreadedMessages(final long unreadedMessages) {
            this.item.unreadedMessages = unreadedMessages;
            return this;
        }

        public Builder setPhoto(final String photo) {
            this.item.photo = photo;
            return this;
        }

        public Builder setLastVisit(final Date lastVisit) {
            this.item.lastVisit = lastVisit;
            return this;
        }

        public Builder setGifts(final List<GiftResponse> gifts) {
            this.item.gifts = gifts;
            return this;
        }

        public Builder setNotices(final List<NoticeResponse> notices) {
            this.item.notices = notices;
            return this;
        }

        public Builder setZags(final UserResponse zags) {
            this.item.zags = zags;
            return this;
        }

        public Builder setZagsRequest(final List<UserResponse> zagsRequest) {
            this.item.zagsRequest = zagsRequest;
            return this;
        }

        public UserResponse build() {
            return this.item;
        }
    }



}
