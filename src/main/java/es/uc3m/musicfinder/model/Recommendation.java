package es.uc3m.musicfinder.model;

import java.util.List;
import java.util.Date;

import es.uc3m.musicfinder.model.Event;
import es.uc3m.musicfinder.model.User;

import jakarta.persistence.Entity;

//revisar para que usar ID
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User who recommends the event can recommend multiple events
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommender_id", nullable = false)
    private User recommender;

    // User to whom the event is recommendedn can be recommended multiple events
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_to_id", nullable = false)
    private User recommendTo;

    // The recommended event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Timestamp of when the recommendation was sent
    @Column(nullable = false)
    private Date timestamp;


    // Getters & Setters --------------------------------------

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public User getRecommender() {
        return recommender;
    }
    public void setRecommender(User recommender) {
        this.recommender = recommender;
    }


    public User getRecommendTo() {
        return recommendTo;
    }
    public void setRecommendTo(User recommendTo) {
        this.recommendTo = recommendTo;
    }


    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }


    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
