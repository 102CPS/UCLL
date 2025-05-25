package be.ucll.model;

import jakarta.persistence.*;

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String interests;

    @OneToOne(mappedBy = "profile")
    private User user;

    // Default constructor for JPA
    public Profile() {}

    public Profile(String bio, String location, String interests) {
        setBio(bio);
        setLocation(location);
        setInterests(interests);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getInterests() {
        return interests;
    }

    public User getUser() {
        return user;
    }

    // Setters with validation
    public void setId(Long id) {
        this.id = id;
    }

    public void setBio(String bio) {
        if (bio == null || bio.trim().isEmpty()) {
            throw new IllegalArgumentException("Bio is required.");
        }
        this.bio = bio;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required.");
        }
        this.location = location;
    }

    public void setInterests(String interests) {
        if (interests == null || interests.trim().isEmpty()) {
            throw new IllegalArgumentException("Interests are required.");
        }
        this.interests = interests;
    }

    public void setUser(User user) {
        this.user = user;
    }
}