package be.ucll.repository;

import be.ucll.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId);
            nextId++;
        }
        users.put(user.getId(), user);
        return user;
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    public List<User> findByAgeBetween(int min, int max) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getAge() >= min && user.getAge() <= max) {
                result.add(user);
            }
        }
        return result;
    }

    public List<User> findByNameContainingIgnoreCase(String name) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(user);
            }
        }
        return result;
    }

    public List<User> findByAgeGreaterThanEqual(int age) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getAge() >= age) {
                result.add(user);
            }
        }
        return result;
    }

    public User findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public void deleteByEmail(String email) {
        users.values().removeIf(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public boolean userExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}