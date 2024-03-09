package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AccountNotificationRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountNotificationService {
    private final AccountNotificationRepository repository;

    public AccountNotificationService(AccountNotificationRepository repository){
        this.repository = repository;
    }
    public List<AccountNotification> findByUser(User user){
        return repository.findByUser(user);
    }

    public void delete(Long id, User currentUser) throws Exception{
        Optional<AccountNotification> optionalAccountNotification = repository.findById(id);
        // Check if present
        if(optionalAccountNotification.isPresent()){
            AccountNotification notification = optionalAccountNotification.get();

            // Check if right permissions
            if(currentUser.isAdmin() || notification.getUser().getId() == currentUser.getId() || currentUser.isOwner()){
                repository.deleteById(id);
            }else{
                throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }

        }else{
            throw new Exception("Notification" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }
}
