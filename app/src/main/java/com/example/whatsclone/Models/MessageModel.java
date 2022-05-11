package com.example.whatsclone.Models;

public class MessageModel {

    String uID, message, messageID;
    Long timeStamp;


    public MessageModel(String uID, String message, String messageID, Long timeStamp) {
        this.uID = uID;
        this.message = message;
        this.messageID = messageID;
        this.timeStamp = timeStamp;
    }

    public MessageModel(String uID, String message) {
        this.uID = uID;
        this.message = message;
    }

    public MessageModel(){

    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageID() { return messageID; }

    public void setMessageID(String messageID) { this.messageID = messageID; }
}
