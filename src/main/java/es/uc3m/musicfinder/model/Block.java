package es.uc3m.musicfinder.model;

import java.util.Date;

import es.uc3m.musicfinder.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private User blockerUser;

    @Column(nullable = false)
    private User blockedUser;


    // Getters & Setters --------------------------------------

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public User getBlockerUser() {
        return blockerUser;
    }
    public void setBlockerUser(User blockerUser) {
        this.blockerUser = blockerUser;
    }


    public User getBlockedUser() {
        return blockedUser;
    }
    public void setBlockedUser(User blockedUser) {
        this.blockedUser = blockedUser;
    }

}
