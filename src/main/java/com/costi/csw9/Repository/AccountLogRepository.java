package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountLogRepository {
    AccountLog findById(Long id);
    List<AccountLog> findByUser(Long id);
    @Modifying
    void save(AccountLog log);
}
