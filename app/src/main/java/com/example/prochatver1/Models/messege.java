package com.example.prochatver1.Models;

public class messege {
    public String messageId;
    public String message;
    public String senderId;
    public String documentUrl;

    public Boolean getDownload() {
        return download;
    }

    public void setDownload(Boolean download) {
        this.download = download;
    }

    public Boolean download;

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String documentName;
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String documentType;

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String videoThumbnail;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String imageUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String videoUrl;
    private long time;
    private long feeling=-1;

    public messege() {

    }
    public messege(String message, String senderId, long time) {
        this.message = message;
        this.senderId = senderId;
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getFeeling() {
        return feeling;
    }

    public void setFeeling(long feeling) {
        this.feeling = feeling;
    }
}
