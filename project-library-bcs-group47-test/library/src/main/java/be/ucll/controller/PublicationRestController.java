package be.ucll.controller;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.service.PublicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publications")
public class PublicationRestController {
    private final PublicationService publicationService;

    public PublicationRestController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    // Story 11 - Filter publications by title and type
    @GetMapping
    public List<Publication> getPublications(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        return publicationService.findPublicationsByTitleAndType(title, type);
    }

    // Story 12 - Retrieve publications by available copies
    @GetMapping("/stock/{availableCopies}")
    public List<Publication> getPublicationsByAvailableCopies(@PathVariable int availableCopies) {
        return publicationService.findPublicationsByAvailableCopies(availableCopies);
    }

    // Add new book with validation
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addPublication(@Valid @RequestBody Book book) {
        Publication savedPublication = publicationService.savePublication(book);
        return (Book) savedPublication;
    }

    // Add new magazine with validation
    @PostMapping("/magazines")
    @ResponseStatus(HttpStatus.CREATED)
    public Magazine addMagazine(@Valid @RequestBody Magazine magazine) {
        Publication savedPublication = publicationService.savePublication(magazine);
        return (Magazine) savedPublication;
    }
}