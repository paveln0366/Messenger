package com.pavelpotapov.messenger;

public class User {
    private String name;
    private String email;
    private String id;
    private int avatarMockUp;

    public User() {
    }

    public User(String name, String email, String id, int avatarMockUp) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.avatarMockUp = avatarMockUp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAvatarMockUp() {
        return avatarMockUp;
    }

    public void setAvatarMockUp(int avatarMockUp) {
        this.avatarMockUp = avatarMockUp;
    }
}
