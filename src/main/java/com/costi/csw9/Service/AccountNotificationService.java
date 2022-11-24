package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Repository.AccountNotificationRepository;
import com.costi.csw9.Service.WikiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountNotificationService {
    @Autowired
    private final AccountNotificationRepository accountNotificationRepository;

    public AccountNotificationService(AccountNotificationRepository accountNotificationRepository){
        this.accountNotificationRepository = accountNotificationRepository;
    }

    public AccountNotification findById(Long id){
        return accountNotificationRepository.findById(id);
    }

    public List<AccountNotification> findByUser(Long id){
        return accountNotificationRepository.findByUser(id);
    }

    public void save(AccountNotification notification){
        accountNotificationRepository.save(notification);
    }
}
