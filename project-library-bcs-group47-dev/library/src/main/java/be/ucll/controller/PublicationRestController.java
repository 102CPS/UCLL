package be.ucll.controller;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.service.PublicationService;
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

    @GetMapping
    public List<Publication> getPublications(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        return publicationService.findPublicationsByTitleAndType(title, type);
    }

    @GetMapping("/stock/{availableCopies}")
    public List<Publication> getPublicationsByAvailableCopies(@PathVariable int availableCopies) {
        return publicationService.findPublicationsByAvailableCopies(availableCopies);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addPublication(@RequestBody Book book) {
        Publication savedPublication = publicationService.savePublication(book);
        return (Book) savedPublication;
    }

    @PostMapping("/magazines")
    @ResponseStatus(HttpStatus.CREATED)
    public Magazine addMagazine(@RequestBody Magazine magazine) {
        Publication savedPublication = publicationService.savePublication(magazine);
        return (Magazine) savedPublication;
    }
}