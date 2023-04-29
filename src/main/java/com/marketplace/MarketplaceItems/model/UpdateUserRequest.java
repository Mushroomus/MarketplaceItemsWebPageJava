package com.marketplace.MarketplaceItems.model;

import com.marketplace.MarketplaceItems.entity.User;

public class UpdateUserRequest {
    private User user;
    private String changePassword;

    public UpdateUserRequest() { }

    public UpdateUserRequest(User user, String changePassword) {
        this.user = user;
        this.changePassword = changePassword;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }
}
