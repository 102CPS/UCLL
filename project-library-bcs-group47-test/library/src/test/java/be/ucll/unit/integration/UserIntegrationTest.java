package be.ucll.unit.integration;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.Membership;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PublicationRepository publicationRepository;


    private User user1;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User("John Doe", "password123", "john.doe@example.com", 30);
        user1 = userRepository.save(user1);

        User user2 = new User("Jane Smith", "password456", "jane.smith@example.com", 25);
        userRepository.save(user2);

        // Save book first
        Book book = new Book("Test Book", "Test Author", "978-0-545-01022-1", 2020, 5);
        book = publicationRepository.save(book); // <-- persist book first!

        // Now create loan with the persisted book entity
        Loan loan = new Loan(user1, List.of(book), LocalDate.now().minusDays(5));

        loanRepository.save(loan);
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetUsersByName() throws Exception {
        mockMvc.perform(get("/users").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    void testGetAllAdultUsers() throws Exception {
        mockMvc.perform(get("/users/adults"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].age", everyItem(greaterThanOrEqualTo(18))));
    }

    @Test
    void testGetUsersByAgeRange() throws Exception {
        mockMvc.perform(get("/users/age/{minAge}/{maxAge}", 20, 40))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddUser() throws Exception {
        User newUser = new User("New User", "newpassword", "new.user@example.com", 28);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New User")));
    }

    @Test
    void testUpdateUser() throws Exception {
        user1.setName("Updated User");
        user1.setPassword("updatedpassword");
        user1.setAge(35);

        mockMvc.perform(put("/users/{email}", user1.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated User")));
    }




    @Test
    void testDeleteUser_WithActiveLoans() throws Exception {
        mockMvc.perform(delete("/users/{email}", user1.getEmail()))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetUserLoans() throws Exception {
        mockMvc.perform(get("/users/{email}/loans", user1.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.email", is(user1.getEmail())));
    }

    @Test
    void testGetUserLoansOnlyActive() throws Exception {
        mockMvc.perform(get("/users/{email}/loans", user1.getEmail()).param("onlyActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
