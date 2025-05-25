package be.ucll.unit.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicationServiceTest {
    private PublicationService publicationService;
    private PublicationRepository publicationRepository;

    @BeforeEach
    void setUp() {
        publicationRepository = new PublicationRepository();
        publicationService = new PublicationService(publicationRepository);

        // Add some test data
        publicationRepository.addBook(new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 1997, 5));
        publicationRepository.addBook(new Book("The Hobbit", "J.R.R. Tolkien", "978-0-618-00221-4", 1937, 3));
        publicationRepository.addMagazine(new Magazine("National Geographic", "Susan Goldberg", "0027-9358", 2023, 10));
        publicationRepository.addMagazine(new Magazine("Time", "Edward Felsenthal", "0040-781X", 2023, 7));
    }

    @Test
    void findAllPublications_NoFilters() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType(null, null);
        assertEquals(4, publications.size());
    }

    @Test
    void findPublicationsByTitle_OnlyTitleFilter() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType("Harry", null);
        assertEquals(1, publications.size());
        assertEquals("Harry Potter", publications.get(0).getTitle());
    }

    @Test
    void findPublicationsByType_OnlyTypeFilter() {
        List<Publication> books = publicationService.findPublicationsByTitleAndType(null, "Book");
        assertEquals(2, books.size());

        List<Publication> magazines = publicationService.findPublicationsByTitleAndType(null, "Magazine");
        assertEquals(2, magazines.size());
    }

    @Test
    void findPublicationsByTitleAndType_BothFilters() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType("Time", "Magazine");
        assertEquals(1, publications.size());
        assertEquals("Time", publications.get(0).getTitle());
    }

    @Test
    void findPublicationsByTitle_CaseInsensitive() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType("harry", null);
        assertEquals(1, publications.size());
        assertEquals("Harry Potter", publications.get(0).getTitle());
    }

    @Test
    void findPublicationsByTitle_PartialMatch() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType("Hob", null);
        assertEquals(1, publications.size());
        assertEquals("The Hobbit", publications.get(0).getTitle());
    }

    @Test
    void findPublicationsByTitle_NoMatch() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType("Nonexistent", null);
        assertEquals(0, publications.size());
    }

    @Test
    void findPublicationsByType_InvalidType() {
        List<Publication> publications = publicationService.findPublicationsByTitleAndType(null, "Newspaper");
        assertEquals(0, publications.size());
    }
}