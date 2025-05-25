package be.ucll.controller;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.service.LoanService;
import be.ucll.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Story 09 - Retrieve users by age range
    @GetMapping("/age/{min}/{max}")
    public List<User> getUsersByAgeRange(@PathVariable int min, @PathVariable int max) {
        return userService.getUsersByAgeRange(min, max);
    }

    // Story 10 - Retrieve users by name (optional query param)
    @GetMapping
    public List<User> getUsersByName(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            return userService.getAllUsers(); // Return all users
        }
        return userService.getUsersByName(name);
    }

    // Story 08 - Retrieve all adult users
    @GetMapping("/adults")
    public List<User> getAllAdultUsers() {
        return userService.getAllAdultUsers();
    }

    // Story 14 - Add a new user with validation
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    // Story 15 - Update existing user with validation
    @PutMapping("/{email}")
    public User updateUser(@PathVariable String email, @Valid @RequestBody User updatedUser) {
        // Find existing user
        User existingUser = userService.findUserByEmail(email);
        if (existingUser == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        // Update user fields (email immutability is handled in User.setEmail())
        existingUser.setName(updatedUser.getName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setAge(updatedUser.getAge());
        // Don't call setEmail - it's immutable after creation

        // Save updated user
        return userService.addUser(existingUser);
    }

    // Story 17 - Delete User
    @DeleteMapping("/{email}")
    public String deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }

    // Story 13 - Retrieve loans for a user
    @GetMapping("/{email}/loans")
    public ResponseEntity<?> getUserLoans(
            @PathVariable String email,
            @RequestParam(required = false, defaultValue = "false") boolean onlyActive) {

        User user = userService.findUserByEmail(email);
        if (user == null) {
            Map<String, String> errorResponse = Map.of(
                    "status", "error",
                    "message", "User with email " + email + " does not exist"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<Loan> loans = loanService.getLoansByUser(email, onlyActive);
        return ResponseEntity.ok(loans);
    }

    // Story 16 - Delete loans of user
    @DeleteMapping("/{email}/loans")
    public String deleteUserLoans(@PathVariable String email) {
        return loanService.deleteUserLoans(email);
    }
}