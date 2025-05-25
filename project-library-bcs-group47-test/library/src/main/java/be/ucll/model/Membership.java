package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    @NotBlank(message = "Membership type is required.")
    private String type;

    @NotNull
    @Min(0)
    private Integer freeLoansRemaining;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Membership() {}

    public Membership(LocalDate startDate, LocalDate endDate, String type, int freeLoansRemaining) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type.toUpperCase();
        validateFreeLoans(this.type, freeLoansRemaining);
        this.freeLoansRemaining = freeLoansRemaining;
    }

    private void validateFreeLoans(String type, int freeLoansRemaining) {
        switch (type.toUpperCase()) {
            case "BRONZE":
                if (freeLoansRemaining < 0 || freeLoansRemaining > 5) {
                    throw new IllegalArgumentException("Invalid number of free loans for membership type.");
                }
                break;
            case "SILVER":
                if (freeLoansRemaining < 6 || freeLoansRemaining > 10) {
                    throw new IllegalArgumentException("Invalid number of free loans for membership type.");
                }
                break;
            case "GOLD":
                if (freeLoansRemaining < 11 || freeLoansRemaining > 15) {
                    throw new IllegalArgumentException("Invalid number of free loans for membership type.");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid membership type.");
        }
    }

    public void redeemFreeLoan() {
        if (freeLoansRemaining == null || freeLoansRemaining <= 0) {
            throw new IllegalArgumentException("No more free loans available within membership.");
        }
        freeLoansRemaining--;
    }

    @JsonIgnore
    public int getRemainingFreeLoans() {
        return freeLoansRemaining != null ? freeLoansRemaining : 0;
    }

    public Long getId() { return id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getType() { return type; }
    public void setType(String type) {
        this.type = type.toUpperCase();
        validateFreeLoans(this.type, freeLoansRemaining);
    }
    public Integer getFreeLoansRemaining() { return freeLoansRemaining; }
    public void setFreeLoansRemaining(Integer freeLoansRemaining) {
        validateFreeLoans(this.type, freeLoansRemaining);
        this.freeLoansRemaining = freeLoansRemaining;
    }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
