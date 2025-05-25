package be.ucll.controller;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.model.Membership;
import be.ucll.service.LoanService;
import be.ucll.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final LoanService loanService;

    public UserRestController(UserService userService, LoanService loanService) {
        this.userService = userService;
        this.loanService = loanService;
    }

    @GetMapping("/age/{min}/{max}")
    public List<User> getUsersByAgeRange(@PathVariable int min, @PathVariable int max) {
        return userService.getUsersByAgeRange(min, max);
    }

    @GetMapping
    public List<User> getUsersByName(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            return userService.getAllUsers();
        }
        return userService.getUsersByName(name);
    }

    @GetMapping("/adults")
    public List<User> getAllAdultUsers() {
        return userService.getAllAdultUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/{email}")
    public User updateUser(@PathVariable String email, @RequestBody User user) {
        return userService.updateUser(email, user);
    }

    @DeleteMapping("/{email}")
    public String deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }

    @GetMapping("/{email}/loans")
    public List<Loan> getUserLoans(@PathVariable String email, @RequestParam(required = false, defaultValue = "false") boolean onlyActive) {
        return loanService.getLoansByUser(email, onlyActive);
    }

    @PostMapping("/{email}/loans/{startDate}")
    public Loan registerLoan(@PathVariable String email, @PathVariable LocalDate startDate, @RequestBody List<Long> publicationIds) {
        return loanService.registerLoan(email, startDate, publicationIds);
    }

    @PutMapping("/{email}/loans/return/{returnDate}")
    public Loan returnLoan(@PathVariable String email, @PathVariable LocalDate returnDate) {
        return loanService.returnLoan(email, returnDate);
    }

    @DeleteMapping("/{email}/loans")
    public String deleteUserLoans(@PathVariable String email) {
        return loanService.deleteUserLoans(email);
    }

    @GetMapping("/oldest")
    public User getOldestUser() {
        return userService.getOldestUser();
    }

    @GetMapping("/interest/{interest}")
    public List<User> getUsersByInterest(@PathVariable String interest) {
        return userService.getUsersByInterest(interest);
    }

    @GetMapping("/interest/{interest}/{age}")
    public List<User> getUsersByInterestAndAgeSortedByLocation(@PathVariable String interest, @PathVariable int age) {
        return userService.getUsersByInterestAndAgeSortedByLocation(interest, age);
    }

    @PostMapping("/{email}/membership")
    public User addMembership(@PathVariable String email, @Valid @RequestBody Membership membership) {
        return userService.addMembership(email, membership);
    }

    @GetMapping("/{email}/membership")
    public Membership getMembershipForDate(@PathVariable String email, @RequestParam("date") LocalDate date) {
        return userService.getMembershipForDate(email, date);
    }
}
