package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AccountLogRepository;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/account-logs")
public class AccountLogController {
    private AccountLogRepository accountLogRepository;
    private UserService userService;

    public AccountLogController(AccountLogRepository accountLogRepository, UserService userService) {
        this.accountLogRepository = accountLogRepository;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAccountLogs(Principal principal) {
        try{
            List<AccountLog> logs = accountLogRepository.findByUser(getCurrentUser(principal));
            return ResponseEntity.ok(logs);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error getting Account Logs", ResponseMessage.Severity.LOW, "Error retrieving notifications: " + e.getMessage()));
        }
    }

    private User getCurrentUser(Principal principal) throws Exception{
        if (principal == null) {
            throw new Exception("No user logged in");
        }
        String username = principal.getName();
        User u = userService.findByEmail(username);
        return u;
    }
}
