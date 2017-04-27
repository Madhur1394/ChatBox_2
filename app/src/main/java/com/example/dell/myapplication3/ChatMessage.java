package com.example.dell.myapplication3;

/**
 * Created by dell on 3/28/17.
 */

public class ChatMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChatMessage() {
    }
    public ChatMessage(String text, String name, String photoUrl,String id) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.id=id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
