package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountNotification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountNotificationRepository {
    AccountNotification findById(Long id);
    List<AccountNotification> findByUser(Long id);
    @Modifying
    void delete(Long id);
    @Modifying
    void save(AccountNotification notification);
}
