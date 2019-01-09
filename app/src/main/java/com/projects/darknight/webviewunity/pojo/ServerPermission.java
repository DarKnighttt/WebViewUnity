package com.projects.darknight.webviewunity.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerPermission {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("permission")
    @Expose
    private Boolean permission;

    public ServerPermission(String name, Boolean permission) {
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPermission() {
        return permission;
    }

    public void setPermission(Boolean permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "Game ='" + name + '\'' +
                ", permission=" + permission;
    }
}
