package com.example.kudos.miniproject1;

import java.io.Serializable;

public class Place implements Serializable{
    private int id;
    private String avatar_name;
    private boolean avatar_internal;
    private String name;
    private String location;
    private String description;
    private String url;
    private String phone;

    public Place(int id, String avatar_name, boolean avatar_internal, String name, String location, String description, String url, String phone) {
        this.id = id;
        this.avatar_name = avatar_name;
        this.avatar_internal = avatar_internal;
        this.name = name;
        this.location = location;
        this.description = description;
        this.url = url;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar_name() {
        return avatar_name;
    }

    public void setAvatar_name(String avatar_name) {
        this.avatar_name = avatar_name;
    }

    public boolean isAvatar_internal() {
        return avatar_internal;
    }

    public void setAvatar_internal(boolean avatar_internal) {
        this.avatar_internal = avatar_internal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
