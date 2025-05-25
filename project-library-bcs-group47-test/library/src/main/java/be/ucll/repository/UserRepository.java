package be.ucll.repository;

import be.ucll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Story 8 - Find users by age greater than or equal to specified age
    List<User> findByAgeGreaterThanEqual(int age);

    // Story 9 - Find users by age range (between min and max inclusive)
    List<User> findByAgeBetween(int minAge, int maxAge);

    // Story 10 - Find users by name containing (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);

    // Find user by email (unique)
    Optional<User> findByEmail(String email);

    // Check if user exists by email
    boolean existsByEmail(String email);

    // Delete user by email
    void deleteByEmail(String email);

    // Custom query methods for more complex operations
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean userExists(@Param("email") String email);
}