package es.uc3m.musicfinder.model;

import java.util.List;

// bean creation
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

// constraints
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// sql
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false, length = 64)
    @NotBlank
    @Size(max = 64)
    private String username;

    @Column(unique = false, nullable = false, length = 64)
    @Email
    @NotBlank
    @Size(max = 64)
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;
    
    // this is initialized in the controller
    @Column(nullable = true) 
    // @NotBlank
    private String role;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<Event> favoriteEvents;

    //TODO: deprecate below
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="following",
        joinColumns=@JoinColumn(name="follower_id"),
        inverseJoinColumns=@JoinColumn(name="followed_id"))
    private List<User> following; // usuarios seguidos

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="following",
        joinColumns=@JoinColumn(name="followed_id"),
        inverseJoinColumns=@JoinColumn(name="follower_id"))
    private List<User> followers; // usuarios que le siguen


    // Getters & setters -----------------------------------------

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }


    public List<Event> getFavoriteEvents() {
        return favoriteEvents;
    }
    public void setFavoriteEvents(List<Event> favoriteEvents) {
        this.favoriteEvents = favoriteEvents;
    }


    public List<User> getFollowing() {
        return this.following;
    }
    public void setFollowing(List<User> following) {
        this.following = following;
    }


    public List<User> getFollowers() {
        return this.followers;
    }
    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    // ------------------------------------------------------------

    @Override
    public String toString() {
        return "User: " + username + " <" + email + ">";
    }

}
