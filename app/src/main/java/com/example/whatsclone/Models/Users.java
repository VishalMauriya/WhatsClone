package com.example.whatsclone.Models;

import java.util.HashMap;
import java.util.Map;

public class Users {
    String profilePic, name, email, userId, lastMessage, status;
    String phoneNumber;

    public Users() {

    }

    public Users(String profilePic, String name, String email, String userId, String lastMessage, String status, String phoneNumber) {
        this.profilePic = profilePic;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
        this.phoneNumber = phoneNumber;
    }

    // SignUp Constructor
    public Users(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Map<String, Object> toMap() {
    Map<String, Object> user = new HashMap<>();
   // user.put("name",name);
   // user.put("email",email);
    user.put("phoneNumber",phoneNumber);
   // user.put("profilePic", profilePic);
    return user;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
