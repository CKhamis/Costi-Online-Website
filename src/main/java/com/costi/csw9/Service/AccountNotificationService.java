package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AccountNotificationRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountNotificationService {
    @Autowired
    private final AccountNotificationRepository accountNotificationRepository;

    public AccountNotificationService(AccountNotificationRepository accountNotificationRepository){
        this.accountNotificationRepository = accountNotificationRepository;
    }

    public Optional<AccountNotification> findById(Long id){
        return accountNotificationRepository.findById(id);
    }

    public List<AccountNotification> findByUser(User user){
        return accountNotificationRepository.findByUser(user);
    }

    public void save(AccountNotification notification){
        accountNotificationRepository.save(notification);
    }

    public void delete(Long id, User currentUser) throws Exception{
        Optional<AccountNotification> optionalAccountNotification = accountNotificationRepository.findById(id);
        // Check if present
        if(optionalAccountNotification.isPresent()){
            AccountNotification notification = optionalAccountNotification.get();

            // Check if right permissions
            if(currentUser.isAdmin() || notification.getUser().getId() == currentUser.getId() || currentUser.isOwner()){
                accountNotificationRepository.deleteById(id);
            }else{
                throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }

        }else{
            throw new Exception("Notification" + LogicTools.NOT_FOUND_MESSAGE);
        }


    }
}
