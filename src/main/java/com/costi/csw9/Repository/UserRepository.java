package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {

    User findFirstByRole(UserRole role);
    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User newUser);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    User getById(Long id);
}