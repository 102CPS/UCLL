package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "loan_publication",
            joinColumns = @JoinColumn(name = "loan_id"),
            inverseJoinColumns = @JoinColumn(name = "publication_id")
    )
    private List<Publication> publications;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private Double price;

    public Loan() {}

    public Loan(User user, List<Publication> publications, LocalDate startDate) {
        // Validate user
        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }

        // Validate publications
        if (publications == null || publications.isEmpty()) {
            throw new IllegalArgumentException("List is required.");
        }

        // Validate start date
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required.");
        }

        if (startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future.");
        }

        // Validate available copies for each publication
        for (Publication pub : publications) {
            if (pub.getAvailableCopies() <= 0) {
                throw new IllegalArgumentException("Unable to lend publication. No copies available for " + pub.getTitle() + ".");
            }
            pub.lendPublication();  // Decrease available copies (if needed)
        }

        this.user = user;
        this.publications = publications;
        this.startDate = startDate;
        this.endDate = startDate.plusDays(30);
        this.price = 0.0;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public List<Publication> getPublications() { return publications; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Double getPrice() { return price; }

    public void setUser(User user) { this.user = user; }
    public void setPublications(List<Publication> publications) { this.publications = publications; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setPrice(Double price) { this.price = price; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public void returnPublication() {
        for (Publication pub : publications) {
            pub.returnPublication();
        }
    }
}
