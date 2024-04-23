package es.uc3m.musicfinder.model;

import java.util.Date;

public class Post {
    private Integer id;

    private String text;

    private User user;

    private Post responseTo;

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

    public Post getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(Post responseTo) {
        this.responseTo = responseTo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
