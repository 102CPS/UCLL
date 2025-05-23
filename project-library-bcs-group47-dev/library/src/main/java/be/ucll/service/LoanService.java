package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    public List<Loan> getLoansByUser(String email, boolean onlyActive) {
        if (!userRepository.userExists(email)) {
            throw new IllegalArgumentException("User with email " + email + " does not exist.");
        }
        return loanRepository.findLoansByUser(email, onlyActive);
    }

    public String deleteUserLoans(String email) {
        // Check if user exists
        if (!userRepository.userExists(email)) {
            throw new IllegalArgumentException("User does not exist.");
        }

        // Get all loans for the user
        List<Loan> userLoans = loanRepository.findLoansByUser(email, false);

        // Check if user has any loans
        if (userLoans.isEmpty()) {
            throw new IllegalArgumentException("User has no loans.");
        }

        // Check if user has active loans
        boolean hasActiveLoans = userLoans.stream()
                .anyMatch(loan -> loan.getEndDate().isAfter(LocalDate.now()));

        if (hasActiveLoans) {
            throw new IllegalArgumentException("User has active loans.");
        }

        // Delete all loans for the user
        loanRepository.deleteByUserEmail(email);

        return "Loans of user successfully deleted.";
    }
}