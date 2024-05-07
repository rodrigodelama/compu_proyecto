package es.uc3m.musicfinder.model;

import java.util.Date;

import es.uc3m.musicfinder.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker; // The user who initiates the block

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked; // The user being blocked

    @Column(nullable = false)
    private Date blockedAt; // The time when the block was made


    // Getters & Setters --------------------------------------

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public User getBlocker() {
        return blocker;
    }
    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }


    public User getBlocked() {
        return blocked;
    }
    public void setBlocked(User blocked) {
        this.blocked = blocked;
    }


    public Date getBlockedAt() {
        return blockedAt;
    }
    public void setBlockedAt(Date blockedAt) {
        this.blockedAt = blockedAt;
    }

}
