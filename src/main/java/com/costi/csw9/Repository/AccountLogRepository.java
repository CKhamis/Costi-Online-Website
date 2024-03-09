package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountLogRepository {
    List<AccountLog> findByUser(User user);
    void deleteByUser(User user);

    void save(AccountLog log);

    List<AccountLog> findAll();

    void deleteById(Long id);

    Optional<AccountLog> findById(Long id);
}
