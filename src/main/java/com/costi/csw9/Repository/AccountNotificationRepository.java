package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface AccountNotificationRepository extends JpaRepository<AccountNotification, Long> {
    List<AccountNotification> findByUser(User user);
    void deleteByUser(User user);
}
