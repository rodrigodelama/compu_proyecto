package es.uc3m.musicfinder.model;

import java.util.List;
import java.time.LocalDateTime;

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

    // User who recommends the event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommender_id", nullable = false)
    private User recommender;

    // User to whom the event is recommended
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_to_id", nullable = false)
    private User recommendTo;

    // The recommended event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Timestamp of when the recommendation was sent
    @Column(nullable = false)
    private LocalDateTime recommendedAt;


    // Constructors ------------------------------------------

    // public Recommendation() {
    //     this.recommendedAt = LocalDateTime.now();
    // }

    public Recommendation(User recommender, User recommendTo, Event event) {
        this.recommender = recommender;
        this.recommendTo = recommendTo;
        this.event = event;
        this.recommendedAt = LocalDateTime.now();
    }


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

    public LocalDateTime getRecommendedAt() {
        return recommendedAt;
    }
    public void setRecommendedAt(LocalDateTime recommendedAt) {
        this.recommendedAt = recommendedAt;
    }

}
