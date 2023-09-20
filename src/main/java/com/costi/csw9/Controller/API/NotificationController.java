package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.User;
import com.costi.csw9.Service.AccountNotificationService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/notifications")
public class NotificationController {
    private AccountNotificationService accountNotificationService;
    private UserService userService;


    public NotificationController(AccountNotificationService accountNotificationService, UserService userService) {
        this.accountNotificationService = accountNotificationService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getNotifications(Principal principal) {
        try{
            List<AccountNotification> notifications = accountNotificationService.findByUser(getCurrentUser(principal));
            return ResponseEntity.ok(notifications);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error getting Notifications", ResponseMessage.Severity.LOW, "Error retrieving notifications: " + e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteNotification(@RequestBody Long id, Principal principal) {
        try {
            accountNotificationService.delete(id, getCurrentUser(principal));
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Deleting Notification", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    private User getCurrentUser(Principal principal) throws Exception{
        if (principal == null) {
            throw new Exception("No user logged in");
        }
        String username = principal.getName();
        User u = userService.loadUserByUsername(username);
        return u;
    }
}
