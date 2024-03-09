package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AccountLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountLogService {
    private final AccountLogRepository repository;

    private AccountLogService(AccountLogRepository repository){
        this.repository = repository;
    }

    public List<AccountLog> findByUser(User user){
        return repository.findByUser(user);
    }

    public void save(AccountLog log){
        repository.save(log);
    }

}
