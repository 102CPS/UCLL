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

    List<User> findByAgeGreaterThanEqual(int age);
    List<User> findByAgeBetween(int minAge, int maxAge);
    List<User> findByNameContainingIgnoreCase(String name);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean userExists(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.age = (SELECT MAX(u2.age) FROM User u2)")
    Optional<User> findOldestUser();

    @Query("SELECT u FROM User u WHERE LOWER(u.profile.interests) LIKE LOWER(CONCAT('%', :interest, '%'))")
    List<User> findUsersByInterest(@Param("interest") String interest);

    @Query("SELECT u FROM User u WHERE LOWER(u.profile.interests) LIKE LOWER(CONCAT('%', :interest, '%')) AND u.age > :age ORDER BY u.profile.location ASC")
    List<User> findUsersByInterestAndAgeSortedByLocation(@Param("interest") String interest, @Param("age") int age);
}
