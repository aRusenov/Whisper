package com.example.nasko.whisper.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private Date date;
    private String authorId;
    private String authorName;
    private String text;
    private boolean seen;
    private int seq;
    private String chatId;
    private boolean isDummy;
    private String label;

    public Message(JSONObject json, String chatId) throws JSONException {
        this.authorId = (String) json.get("createdBy");
        this.seq = json.getInt("seq");
        this.text = (String) json.get("text");
        this.chatId = chatId;
        String dateString = (String) json.get("createdAt");
        try {
            this.date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Message(JSONObject json) throws JSONException {
        this(json, null);
        this.chatId = (String) json.get("chatId");
    }

    public Message(Date date, String authorId, String text) {
        this.date = date;
        this.authorId = authorId;
        this.text = text;
    }

    private Message(String label) {
        this.isDummy = true;
        this.label = label;
    }

    public static Message createDummy(String label) {
        return new Message(label);
    }

    public boolean isDummy() {
        return isDummy;
    }

    public String getLabel() {
        return label;
    }

    public int getSeq() {
        return seq;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFrom() {
        return authorId;
    }

    public String getAuthorName() { return authorName; }

    public void setFrom(String authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
