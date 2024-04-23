package es.uc3m.musicfinder.model;

import java.util.Date;


public class Event {
    private Integer id;

    private String text;

    private User user;

    private Event responseTo;

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

    public Event getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(Event responseTo) {
        this.responseTo = responseTo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
