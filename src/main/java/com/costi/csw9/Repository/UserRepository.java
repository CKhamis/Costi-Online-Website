package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    void enable(@Param("id") Long id, @Param("enabled") boolean enable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isLocked = :isLocked WHERE u.id = :id")
    void lock(@Param("id") Long id, @Param("isLocked") boolean isLocked);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role = 'USER' WHERE u.id = :id")
    void demote(@Param("id") Long id);

    Optional<User> findByEmail(String email);
}