package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountNotificationRepository {
    List<AccountNotification> findByUser(User user);
    void deleteByUser(User user);

    void save(AccountNotification welcome);

    List<AccountNotification> findAll();

    void deleteById(Long id);

    Optional<AccountNotification> findById(Long id);
}
