package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
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

    public AccountLog loadById(Long id){
        return accountLogRepository.findById(id);
    }

    public List<AccountLog> findByUser(Long id){
        return accountLogRepository.findByUser(id);
    }

    public void save(AccountLog log){
        accountLogRepository.save(log);
    }

}
