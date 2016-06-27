package com.example.nasko.whisper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private Date date;
    private String from;
    private String text;
    private boolean seen;
    private int seq;
    private String chatId;

    public Message(JSONObject json) throws JSONException {
        this.from = (String) json.get("createdBy");
        this.seq = json.getInt("seq");
        this.text = (String) json.get("text");
        this.chatId = (String) json.get("chatId");
        String dateString = (String) json.get("createdAt");
        try {
            this.date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Message(Date date, String from, String text) {
        this.date = date;
        this.from = from;
        this.text = text;
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
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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
