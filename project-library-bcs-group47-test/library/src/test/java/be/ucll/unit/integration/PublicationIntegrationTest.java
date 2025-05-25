package be.ucll.unit.integration;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PublicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationService publicationService;

    @BeforeEach
    void setUp() {
        publicationRepository.deleteAll();

        Book book1 = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 1997, 5);
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", "978-0-618-00221-4", 1937, 3);
        Magazine magazine1 = new Magazine("National Geographic", "Susan Goldberg", "0027-9358", 2023, 10);
        Magazine magazine2 = new Magazine("Time", "Edward Felsenthal", "0040-781X", 2023, 7);

        publicationService.savePublication(book1);
        publicationService.savePublication(book2);
        publicationService.savePublication(magazine1);
        publicationService.savePublication(magazine2);
    }

    @Test
    void testGetPublications_NoFilters() throws Exception {
        mockMvc.perform(get("/publications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].title", is("Harry Potter")))
                .andExpect(jsonPath("$[1].title", is("The Hobbit")))
                .andExpect(jsonPath("$[2].title", is("National Geographic")))
                .andExpect(jsonPath("$[3].title", is("Time")));
    }

    @Test
    void testGetPublicationsByAvailableCopies() throws Exception {
        int availableCopies = 5;
        mockMvc.perform(get("/publications/stock/{availableCopies}", availableCopies))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title", is("Harry Potter")))
                .andExpect(jsonPath("$[0].availableCopies", is(5)))
                .andExpect(jsonPath("$[1].title", is("National Geographic")))
                .andExpect(jsonPath("$[1].availableCopies", is(10)))
                .andExpect(jsonPath("$[2].title", is("Time")))
                .andExpect(jsonPath("$[2].availableCopies", is(7)));
    }

    @Test
    void testAddPublication() throws Exception {
        Book newBook = new Book("New Book", "New Author", "978-0-123-45678-9", 2023, 10);

        mockMvc.perform(post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.author", is("New Author")))
                .andExpect(jsonPath("$.isbn", is("978-0-123-45678-9")))
                .andExpect(jsonPath("$.publicationYear", is(2023)))
                .andExpect(jsonPath("$.availableCopies", is(10)));
    }

    @Test
    void testGetPublicationsByTitleAndType() throws Exception {
        mockMvc.perform(get("/publications")
                        .param("title", "Harry Potter")
                        .param("type", "Book"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Harry Potter")));
    }

    @Test
    void testGetPublicationsByInvalidType() throws Exception {
        mockMvc.perform(get("/publications")
                        .param("type", "Newspaper"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetPublicationsByTitleNoMatch() throws Exception {
        mockMvc.perform(get("/publications")
                        .param("title", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
