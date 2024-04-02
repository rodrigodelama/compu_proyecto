package es.uc3m.microblog.model;

import java.util.Date;

public class Message {
    private Integer id;

    private String text;

    private User user;

    private Message responseTo;

    private Date timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(Message responseTo) {
        this.responseTo = responseTo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
