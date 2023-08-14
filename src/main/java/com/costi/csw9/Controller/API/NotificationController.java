package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.AccountNotification;
import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Service.AccountNotificationService;
import com.costi.csw9.Service.AnnouncementService;
import com.costi.csw9.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving notifications: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteNotification(@RequestBody Long id, Principal principal) {
        try {
            accountNotificationService.delete(id, getCurrentUser(principal));
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting notification: " + e.getMessage());
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
