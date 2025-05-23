package be.ucll.model;

import java.time.LocalDate;
import java.util.List;

public class Loan {
    private User user;
    private List<Publication> publications;
    private LocalDate startDate;
    private LocalDate endDate;

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
        for (Publication publication : publications) {
            if (publication.getAvailableCopies() <= 0) {
                throw new IllegalArgumentException("Unable to lend publication. No copies available for " + publication.getTitle());
            }
        }
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

    public void returnPublication(){
        for (Publication publication : publications) {
            publication.returnPublication();
        }
    }
}
