package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {
    List<AccountLog> findByUser(User user);
    void deleteByUser(User user);
}