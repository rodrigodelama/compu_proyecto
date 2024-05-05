package es.uc3m.musicfinder.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 64)
    @NotBlank
    @Size(max = 64)
    private String name;

    @Column(nullable = false)
    private Date date; //and time?

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false, length = 64)
    @NotBlank
    @Size(max = 64)
    private String location;

    @Column(nullable = false, length = 512)
    @NotBlank
    @Size(max = 512)
    private String description;

    @Column(nullable = false)
    private Date postedDate;


    // Getters & Setters --------------------------------------

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }


    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }


    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public Date getPostedDate() {
        return postedDate;
    }
    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

}
