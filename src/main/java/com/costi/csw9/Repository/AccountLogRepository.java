package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {
    List<AccountLog> findByUser(Long id);

}