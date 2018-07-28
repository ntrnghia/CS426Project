package com.example.kudos.miniproject1;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Place implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String avatar_name;
    private String name;
    private String location;
    private String description;
    private String url;
    private String phone;

    public Place(String avatar_name, String name, String location, String description, String url, String phone) {
        this.avatar_name = avatar_name;
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

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() &&
                this.id == ((Place) obj).id &&
                this.avatar_name.equals(((Place) obj).avatar_name) &&
                this.name.equals(((Place) obj).name) &&
                this.location.equals(((Place) obj).location) &&
                this.description.equals(((Place) obj).description) &&
                this.url.equals(((Place) obj).url) &&
                this.phone.equals(((Place) obj).phone);
    }
}
