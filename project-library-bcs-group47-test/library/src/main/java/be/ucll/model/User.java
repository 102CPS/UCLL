package be.ucll.model;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private int age;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // Constructor with validation
    public User(String name, String password, String email, int age) {
        setName(name);
        setPassword(password);
        setEmail(email);
        setAge(age);
    }

    // Constructor with profile
    public User(String name, String password, String email, int age, Profile profile) {
        this(name, password, email, age);
        setProfile(profile);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public Profile getProfile() {
        return profile;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        this.password = password;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("E-mail must be a valid format.");
        }
        this.email = email;
    }

    public void setAge(int age) {
        if (age < 0 || age > 101) {
            throw new IllegalArgumentException("Age must be between 0 and 101.");
        }
        this.age = age;
    }

    public void setProfile(Profile profile) {
        if (profile != null && this.age < 18) {
            throw new IllegalArgumentException("User must be at least 18 years old to have a profile.");
        }
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

    // Default constructor for JPA
    public User() {}
}