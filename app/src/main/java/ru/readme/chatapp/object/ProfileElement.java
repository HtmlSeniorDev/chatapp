package ru.readme.chatapp.object;

public class ProfileElement {

    private String name = "";
    private String value = "";

    public ProfileElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ProfileElement(String name) {
        this.name = name;
    }

    public ProfileElement() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
