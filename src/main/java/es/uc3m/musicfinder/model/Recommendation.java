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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    private List<Event> event;

    @Column(nullable = false)
    @NotBlank
    private User recomender;

    @Column(nullable = false)
    @NotBlank
    private User recomendee;

    @Column(nullable = false)
    @NotBlank
    private Date date; // fecha de la recomendación hora ???


    // Getters & Setters --------------------------------------

    public List<Event> getEvent() {
        return event;
    }
    public void setEvent(List<Event> event) {
        this.event = event;
    }


    public User getRecomender() {
        return recomender;
    }
    public void setRecomender(User recomender) {
        this.recomender = recomender;
    }


    public User getRecomendee() {
        return recomendee;
    }
    public void setRecomendee(User recomendee) {
        this.recomendee = recomendee;
    }

}
