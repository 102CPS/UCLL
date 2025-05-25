package be.ucll.model;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class Loan {

    @NotNull(message = "User is required.")
    private User user;

    @NotNull(message = "List is required.")
    @NotEmpty(message = "List is required.")
    private List<Publication> publications;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    private LocalDate endDate;

    // No-args constructor for frameworks
    public Loan() {}

    public Loan(User user, List<Publication> publications, LocalDate startDate) {
        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }
        if (publications == null || publications.isEmpty()) {
            throw new IllegalArgumentException("List is required.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        if (startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future.");
        }

        this.user = user;
        this.publications = publications;
        this.startDate = startDate;
        this.endDate = startDate.plusDays(21);

        setPublications();
    }

    private void setPublications() {
        // First check all publications have available copies
        for (Publication publication : publications) {
            if (publication.getAvailableCopies() <= 0) {
                throw new IllegalArgumentException("Unable to lend publication. No copies available for " + publication.getTitle() + ".");
            }
        }
        // Only if all are available, lend them
        for (Publication publication : publications) {
            publication.lendPublication();
        }
    }

    // Getters
    public User getUser() {
        return user;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // Setters
    public void setUser(User user) {
        this.user = user;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void returnPublication() {
        for (Publication publication : publications) {
            publication.returnPublication();
        }
    }
}