package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AccountLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountLogService {
    @Autowired
    private final AccountLogRepository accountLogRepository;

    private AccountLogService(AccountLogRepository accountLogRepository){
        this.accountLogRepository = accountLogRepository;
    }

    public List<AccountLog> findByUser(User user){
        return accountLogRepository.findByUser(user);
    }

    public void save(AccountLog log){
        accountLogRepository.save(log);
    }

}
