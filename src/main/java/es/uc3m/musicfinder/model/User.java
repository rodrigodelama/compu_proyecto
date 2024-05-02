package es.uc3m.musicfinder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
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

    @Column(nullable = false, length = 64)
    @NotBlank
    @Size(max = 64)
    private String name;

    @Column(unique = true, nullable = false, length = 64)
    @Email
    @NotBlank
    @Size(max = 64)
    private String email;

    @Lob
    private String description;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(nullable = false)
    @NotBlank
    private String role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="following",
        joinColumns=@JoinColumn(name="follower_id"),
        inverseJoinColumns=@JoinColumn(name="followed_id"))
    private List<User> following; //lista de usuarios que sigue

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="following",
        joinColumns=@JoinColumn(name="followed_id"),
        inverseJoinColumns=@JoinColumn(name="follower_id"))
    private List<User> followers; //lista usuarios que le siguien 

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<Message> messages;

    // Getters & setters -----------------------------------------

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


    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
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
        return "User: " + name + " <" + email + ">";
    }
}
