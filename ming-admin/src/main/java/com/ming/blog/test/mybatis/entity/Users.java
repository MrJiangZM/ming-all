package com.ming.blog.test.mybatis.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatis-generator
 * @since 2021-08-11
 */
public class Users implements Serializable {

    private String username;

    private String password;

    private Boolean enabled;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Users{" +
        "username=" + username +
        ", password=" + password +
        ", enabled=" + enabled +
        "}";
    }
}
