package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    User findById(Long id);
    Optional<User> findByEmail(String email);
    void enableUser(Long id);
    List<User> findAll();
    void save(User user);
    void delete(User user);

    void enable(Long id, boolean enable);
    void lock(Long id, boolean lock);
}
