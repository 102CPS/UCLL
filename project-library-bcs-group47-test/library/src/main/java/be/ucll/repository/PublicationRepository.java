package be.ucll.repository;

import be.ucll.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByTitleContainingIgnoreCase(String title);
    List<Publication> findByAvailableCopiesGreaterThanEqual(int availableCopies);
}
