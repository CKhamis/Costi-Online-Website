package com.costi.csw9.Controller;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.Temp.AccountNotificationRequest;
import com.costi.csw9.Service.*;
import com.costi.csw9.Util.LogicTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class FrontEndController {
    private final UserService userService;
    private RegistrationService registrationService;
    private WikiService wikiService;

    private AnnouncementService announcementService;

    private AccountLogService accountLogService;

    private AccountNotificationService accountNotificationService;
    private PostService postService;

    @Autowired
    public FrontEndController(UserService userService, RegistrationService registrationService, WikiService wikiService, AnnouncementService announcementService, AccountLogService accountLogService, AccountNotificationService accountNotificationService, PostService postService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.wikiService = wikiService;
        this.announcementService = announcementService;
        this.accountLogService = accountLogService;
        this.accountNotificationService = accountNotificationService;
        this.postService = postService;
    }

    // Theme
    private String choseTheme() {
        LocalDate today = LocalDate.now();
        if (today.getMonth().name().equalsIgnoreCase("July")) {
            return "/XpTheme.css";
        } else if (today.getMonthValue() > 7 && today.getMonthValue() <= 12) {
            return "/White.css";
        } else {
            return "/Dark.css";
        }
    }

    //Account
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return new User("NULL", "NULL", "Not Signed In", "error", UserRole.USER);
        }
        String username = principal.getName();
        User u = userService.loadUserObjectByUsername(username);
        return u;
    }

    @RequestMapping("/Account")
    public String editUser(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("action", "/Account/edit");
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("logs", accountLogService.findByUser(user.getId()));
        List<AccountNotification> notifications = accountNotificationService.findByUser(user.getId());
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", notifications.size());
        return "main/ViewAccount";
    }

    @PostMapping("/Account/edit")
    public String updateUser(User user, BindingResult result, RedirectAttributes redirectAttributes, Principal principal) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User", result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("user", user);
        }

        //Transfer id so it gets overwritten in data
        User currentUser = getCurrentUser(principal);
        user.setId(currentUser.getId());

        //Enable user if enabled
        user.setEnabled(currentUser.getEnabled());

        //Transfer role
        user.setRole(currentUser.getRole());

        //Save new user
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("✅ Account Successfully Edited", "Changes saved to server", FlashMessage.Status.SUCCESS));
        return "redirect:/Account";
    }

    @RequestMapping(value = "/Account/Notification/{id}/delete", method = RequestMethod.GET)
    public String deleteNotification(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        AccountNotification notification = accountNotificationService.findById(id);
        if (getCurrentUser(principal).isAdmin() || notification.getUser().getId() == getCurrentUser(principal).getId()) {
            accountNotificationService.delete(id);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Notification Dismissed", "Notification was permanently removed from your account", FlashMessage.Status.SUCCESS));
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }
        return "redirect:/Account";
    }

    @GetMapping("/SignUp")
    public String getNewAccount(Model model, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("action", "/SignUp/post");
        model.addAttribute("theme", choseTheme());
        return "main/NewAccount";
    }

    @RequestMapping(value = "/SignUp/post", method = RequestMethod.POST)
    public String addNewUser(User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("user", user);

            // Redirect back to the form
            return "redirect:/";
        }
        if (user.getRole().name().equals("ADMIN")) {
            registrationService.registerAdmin(user);
            return "redirect:/";
        }
        registrationService.registerUser(user);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("✅ Costi Account Created!", "Please sign in to continue.", FlashMessage.Status.SUCCESS));
        return "redirect:/";
    }

    //Moderator
    @GetMapping("/COMT/Wiki")
    public String getCostiOnlineWikiTools(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("disabled", wikiService.getByApproval(false));
        model.addAttribute("enabled", wikiService.getByApproval(true));
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "moderator/WikiTools";
    }

    @GetMapping("/COMT/Accounts")
    public String getCostiOnlineAccountTools(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());

        model.addAttribute("all", userService.loadAll());
        return "moderator/AccountTools";
    }

    @GetMapping("/COMT/Newsroom")
    public String getNewsroomTools(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("disabled", postService.getByApproval(false));
        model.addAttribute("enabled", postService.getByApproval(true));
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "moderator/NewsroomTools";
    }

    @GetMapping("/COMT/Accounts/{id}")
    public String getCostiOnlineAccountSettings(Model model, Principal principal, RedirectAttributes redirectAttributes, @PathVariable Long id) {
        // TODO: add a nicer way to enable/disable, lock/unlock accounts
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", true);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());

        model.addAttribute("all", userService.loadAll());
        model.addAttribute("action", "/COMT/Accounts/" + id + "/edit");

        //Selected User
        User selectedUser = userService.loadUserObjectById(id);
        model.addAttribute("selectedUser", selectedUser);
        List<AccountNotification> notifications = accountNotificationService.findByUser(id);
        model.addAttribute("SUNotificationCount", notifications.size());
        model.addAttribute("SUNotifications", notifications);
        List<AccountLog> logs = accountLogService.findByUser(id);
        model.addAttribute("SULogCount", logs.size());
        model.addAttribute("SULogs", logs);
        model.addAttribute("SUlastInteraction", logs.get(logs.size()-1).getDateCreated());
        model.addAttribute("SUCanSignIn", !selectedUser.getIsLocked() && selectedUser.getEnabled());
//        List<WikiPage> wikiPages = wikiService.getWikiPagesByAuthor(id);
//        model.addAttribute("SUWikiCount", wikiPages.size());
//        model.addAttribute("SUWikiPages", wikiPages);

        return "moderator/AdminAccountView";
    }

    @GetMapping("/COMT/Newsroom/Create")
    public String getNewsroomPostMaker(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", true);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());

        model.addAttribute("isAllowed", user.getRole() == UserRole.OWNER);
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("title", "Create Newsroom Post");
        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
        model.addAttribute("action", "/COMT/Newsroom/Create");

        return "moderator/NewPost";
    }

    @RequestMapping(value = "/COMT/Newsroom/Create", method = RequestMethod.POST)
    public String createNewPostImage(Post post, @RequestParam("image") MultipartFile file, Principal principal, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("post", post);

            // Redirect back to the form
            return "redirect:/COMT/Newsroom/Create";
        }

        if(post.getCategory().equals(PostCategory.EMERGENCY.name())){
            AccountNotification notification = null;
            for(User user : userService.loadAll()){
                notification = new AccountNotification();
                notification.setNotificationType("danger");
                notification.setUser(user);
                notification.setTitle("EMERGENCY");
                notification.setBody("An emergency post was made. View it in Newsroom");
                accountNotificationService.save(notification);
            }
            postService.save(post, file);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Emergency Notification Sent", "Notification was sent to all accounts on Costi Online. Please publish draft.", FlashMessage.Status.SUCCESS));
        }else{
            postService.save(post, file);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Newsroom Draft Created", "Please approve via COMT to publish.", FlashMessage.Status.SUCCESS));
        }

        return "redirect:/COMT/Newsroom/Create";
    }

    @RequestMapping(value = "/COMT/Newsroom/CreateNoImage", method = RequestMethod.POST)
    public String createNewPost(Post post, Principal principal, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("post", post);

            // Redirect back to the form
            return "redirect:/COMT/Newsroom/Create";
        }

        if(post.getCategory().equals(PostCategory.EMERGENCY.name())){
            AccountNotification notification = null;
            for(User user : userService.loadAll()){
                notification = new AccountNotification();
                notification.setNotificationType("danger");
                notification.setUser(user);
                notification.setTitle("EMERGENCY");
                notification.setBody("An emergency post was made. View it in Newsroom");
                accountNotificationService.save(notification);
            }
            postService.save(post);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Emergency Notification Sent", "Notification was sent to all accounts on Costi Online. Please publish draft.", FlashMessage.Status.SUCCESS));
        }else{
            postService.save(post);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Newsroom Draft Created", "Please approve via COMT to publish.", FlashMessage.Status.SUCCESS));
        }

        return "redirect:/COMT/Newsroom/Create";
    }

    @RequestMapping(value = "/Newsroom/{PostId}/delete", method = RequestMethod.POST)
    public String deletePost(@PathVariable Long PostId, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postService.loadById(PostId);
        if (getCurrentUser(principal).isAdmin()) {
            postService.delete(post);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Newsroom post deleted!", "Post is no longer accessible nor recoverable.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Newsroom";
    }

    @RequestMapping(value = "/Newsroom/{PostId}/enable", method = RequestMethod.POST)
    public String enablePost(@PathVariable Long PostId, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postService.loadById(PostId);
        if (getCurrentUser(principal).isAdmin()) {
            postService.enable(post, true);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Post Published!", "Post is now accessible by non-administrators on the Newsroom page", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Newsroom";
    }

    @RequestMapping(value = "/Newsroom/{PostId}/disable", method = RequestMethod.POST)
    public String disablePost(@PathVariable Long PostId, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postService.loadById(PostId);
        if (getCurrentUser(principal).isAdmin()) {
            postService.enable(post, false);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Post disabled!", "Post is no longer accessible by public.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Newsroom";
    }

    @GetMapping("/COMT/Notifications/Create")
    public String getCostiOnlineNotificationSettings(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        // TODO: add a nicer way to enable/disable, lock/unlock accounts
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", true);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());

        if (!model.containsAttribute("notification")) {
            model.addAttribute("notification", new AccountNotificationRequest());
        }
        model.addAttribute("allUsers", userService.loadAll());
        model.addAttribute("action", "/COMT/Notifications/Create/post");

        return "moderator/NotificationTools";
    }

    @RequestMapping(value = "/COMT/Notifications/Create/post", method = RequestMethod.POST)
    public String createNewNotification(AccountNotificationRequest notificationRequest, Principal principal, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("notification", notificationRequest);

            // Redirect back to the form
            return "redirect:/COMT/Notifications/Create";
        }


        if(notificationRequest.getDestination().equals("All")){
            AccountNotification notification = null;
            for(User user : userService.loadAll()){
                notification = new AccountNotification(notificationRequest);
                notification.setUser(user);
                accountNotificationService.save(notification);
            }
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Notification Batch Sent", "Notification was sent to all accounts on Costi Online", FlashMessage.Status.SUCCESS));
        }else{
            AccountNotification notification = new AccountNotification(notificationRequest);
            notification.setUser(userService.loadUserObjectById(Long.parseLong(notificationRequest.getDestination())));
            accountNotificationService.save(notification);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Notification Sent", "Notification was sent to user with ID of " + notificationRequest.getDestination(), FlashMessage.Status.SUCCESS));
        }

        return "redirect:/COMT/Notifications/Create";
    }

    @RequestMapping(value = "/COMT/Accounts/{userId}/Notification/{id}/delete", method = RequestMethod.GET)
    public String adminDeleteNotification(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes, @PathVariable Long userId) {
        AccountNotification notification = accountNotificationService.findById(id);
        if (getCurrentUser(principal).isAdmin() || notification.getUser().getId() == getCurrentUser(principal).getId()) {
            accountNotificationService.delete(id);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Notification Dismissed", "Notification was permanently removed from your account", FlashMessage.Status.SUCCESS));
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }
        return "redirect:/COMT/Accounts/" + userId;
    }

    @PostMapping("/COMT/Accounts/{id}/edit")
    public String adminUpdateUser(User user, BindingResult result, RedirectAttributes redirectAttributes, Principal principal, @PathVariable Long id) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User", result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("selectedUser", user);
        }

        //Save new user
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("✅ Account Successfully Edited", "Changes saved to server", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Accounts/" + id;
    }

    @GetMapping("/COMT/Announcements")
    public String getCostiOnlineAnnouncementTools(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("enabled", announcementService.getByApproval(true));
        model.addAttribute("disabled", announcementService.getByApproval(false));
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "moderator/AnnouncementTools";
    }

    @GetMapping("/COMT/Announcements/Create")
    public String getCreateAnnouncement(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("announcement")) {
            model.addAttribute("announcement", new Announcement());
        }
        model.addAttribute("action", "/COMT/Announcements/Create");
        model.addAttribute("title", "Create New Announcement");
        model.addAttribute("theme", choseTheme());

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        model.addAttribute("loggedIn", principal != null);
        return "moderator/AnnouncementMaker";
    }

    @RequestMapping(value = "/COMT/Announcements/Create", method = RequestMethod.POST)
    public String addNewAnnouncement(Announcement announcement, Principal principal, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("announcement", announcement);

            // Redirect back to the form
            return "redirect:/COMT/Announcements/Create";
        }

        announcementService.save(announcement);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Announcement has been created", "To publish it, please press enable", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/enable", method = RequestMethod.POST)
    public String enableAnnouncement(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Announcement announcement = announcementService.findById(id);
        if (getCurrentUser(principal).isAdmin()) {
            announcementService.enable(announcement, true);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Announcement Published!", "Announcement is publicly visible", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/disable", method = RequestMethod.POST)
    public String disableAnnouncement(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Announcement announcement = announcementService.findById(id);
        if (getCurrentUser(principal).isAdmin()) {
            announcementService.enable(announcement, false);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Announcement Private!", "Announcement was removed from public view", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping("/COMT/Announcements/{id}/edit")
    public String getEditAnnouncement(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User current = getCurrentUser(principal);
        Announcement announcement = announcementService.findById(id);

        model.addAttribute("announcement", announcement);
        model.addAttribute("action", "/COMT/Announcements/" + id + "/edit");
        model.addAttribute("title", "Edit Announcement");

        model.addAttribute("user", current);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(current.getId()).size());
        return "moderator/AnnouncementMaker";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/edit", method = RequestMethod.POST)
    public String editAnnouncement(@PathVariable Long id, Announcement announcement, Principal principal, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User", result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("announcement", announcement);
        }

        //Transfer id
        announcement.setId(id);

        //Save
        announcementService.save(announcement);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Announcement Edited", "Announcement contents was modified.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/delete", method = RequestMethod.POST)
    public String deleteAnnouncement(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Announcement announcement = announcementService.findById(id);
        if (getCurrentUser(principal).isAdmin()) {
            announcementService.delete(announcement);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Announcement Deleted!", "Announcement was permanently removed from database", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/Accounts/{accountId}/lock", method = RequestMethod.POST)
    public String lockAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if (getCurrentUser(principal).isAdmin()) {
            if (!userService.lock(user, true)) {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            } else {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Account Locked", user.getFirstName() + " " + user.getLastName() + " is locked and can no longer log in.", FlashMessage.Status.SUCCESS));
            }
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";
    }

    @RequestMapping(value = "/Accounts/{accountId}/unlock", method = RequestMethod.POST)
    public String unlockAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if (getCurrentUser(principal).isAdmin()) {
            userService.lock(user, false);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Account Unlocked!", user.getFirstName() + " " + user.getLastName() + " is now unlocked.", FlashMessage.Status.SUCCESS));

        return "redirect:/COMT/Accounts";
    }

    @RequestMapping(value = "/Accounts/{accountId}/enable", method = RequestMethod.POST)
    public String enableAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if (getCurrentUser(principal).isAdmin()) {
            if (!userService.enable(user, true)) {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            } else {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Account Enabled", user.getFirstName() + " " + user.getLastName() + " is now enabled.", FlashMessage.Status.SUCCESS));
            }
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";
    }

    @RequestMapping(value = "/Accounts/{accountId}/demote", method = RequestMethod.POST)
    public String demoteAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if (getCurrentUser(principal).isAdmin()) {
            if (!userService.enable(user, false)) {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            } else {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Account Demoted", user.getFirstName() + " " + user.getLastName() + " is now a user.", FlashMessage.Status.SUCCESS));
            }
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";

    }

    @RequestMapping(value = "/Accounts/{accountId}/disable", method = RequestMethod.POST)
    public String disableAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if (getCurrentUser(principal).isAdmin()) {
            if (!userService.enable(user, false)) {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            } else {
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Account Disabled", user.getFirstName() + " " + user.getLastName() + " is now enabled.", FlashMessage.Status.SUCCESS));
            }
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";
    }

    //Main
    @GetMapping("/")
    public String getHome(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());

        List<Announcement> announcements = announcementService.getByApproval(true);
        model.addAttribute("announcements", announcements);
        model.addAttribute("isAnnouncement", announcements.size() > 0);

        List<WikiPage> random = wikiService.getByApproval(true);
        Collections.shuffle(random);

        if (random.size() > 3) {
            random = new ArrayList<>(random.subList(0, 3));
        }

        model.addAttribute("wiki", random);
        return "main/Home";
    }

    @GetMapping("/Projects")
    public String getProjects(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "main/Projects";
    }

    @GetMapping("/login")
    public String getLogin(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "main/login";
    }

    //Wiki
    @GetMapping("/Wiki")
    public String getWikiHome(Model model, Principal principal) {

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        List<WikiPage> allEnabled = wikiService.getByApproval(true);
        model.addAttribute("all", allEnabled);
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "wiki/WikiHome";
    }

    @GetMapping("/Wiki/Create")
    public String getCreateWiki(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("page")) {
            model.addAttribute("page", new WikiPage(getCurrentUser(principal)));
        }
        model.addAttribute("isAllowed", true);
        model.addAttribute("action", "/Wiki/Create/post");
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("title", "Create New Wiki Page");
        model.addAttribute("theme", choseTheme());


        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        model.addAttribute("loggedIn", principal != null);
        return "wiki/NewWiki";
    }

    @RequestMapping(value = "/Wiki/Create/post", method = RequestMethod.POST)
    public String addNewPage(WikiPage wikiPage, Principal principal, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("page", wikiPage);

            // Redirect back to the form
            return "redirect:/Wiki/Create";
        }
        wikiPage.setAuthor(getCurrentUser(principal));

        wikiService.save(wikiPage);
        return "redirect:/Wiki/" + wikiPage.getId() + "/view";
    }

    @RequestMapping("/Wiki/{PageId}/view")
    public String viewPage(Model model, Principal principal, RedirectAttributes redirectAttributes, @PathVariable Long PageId) {
        User current = getCurrentUser(principal);
        WikiPage wiki = wikiService.loadById(PageId);

        model.addAttribute("showEdit", (current.isAdmin() || wiki.getAuthor().equals(current)));
        model.addAttribute("isAdmin", current.isAdmin());
        model.addAttribute("user", current);
        model.addAttribute("isViewable", current.isAdmin() || wiki.isEnabled());
        model.addAttribute("loggedIn", principal != null);

        model.addAttribute("wiki", wiki);
        model.addAttribute("categoryPages", wikiService.getWikiPagesByCat(wiki.getCategory()));
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(current.getId()).size());

        return "wiki/ViewWiki";
    }

    @RequestMapping(value = "/Wiki/{PageId}/delete", method = RequestMethod.POST)
    public String deleteWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        WikiPage page = wikiService.loadById(PageId);
        if (getCurrentUser(principal).isAdmin()) {
            wikiService.delete(page);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Wiki Page deleted!", "Page is no longer accessible nor recoverable.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Wiki";
    }

    @RequestMapping(value = "/Wiki/{PageId}/enable", method = RequestMethod.POST)
    public String enableWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        WikiPage page = wikiService.loadById(PageId);
        if (getCurrentUser(principal).isAdmin()) {
            wikiService.enable(page, true);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Wiki Page Published!", "Page is now accessible by non-administrators on the Costipedia page", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Wiki";
    }

    @RequestMapping(value = "/Wiki/{PageId}/disable", method = RequestMethod.POST)
    public String disableWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        WikiPage page = wikiService.loadById(PageId);
        if (getCurrentUser(principal).isAdmin()) {
            wikiService.enable(page, false);
        } else {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Wiki Page disabled!", "Page is no longer accessible by public.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Wiki";
    }

    @RequestMapping("/Wiki/{PageId}/edit")
    public String getEditWiki(@PathVariable Long PageId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User current = getCurrentUser(principal);
        WikiPage page = wikiService.loadById(PageId);

        model.addAttribute("page", page);
        model.addAttribute("isAllowed", (current.isAdmin() || page.getAuthor().equals(current)));
        model.addAttribute("action", "/Wiki/" + PageId + "/edit");
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("title", "Edit Wiki Page");

        model.addAttribute("user", current);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(current.getId()).size());
        return "wiki/NewWiki";
    }

    @RequestMapping(value = "/Wiki/{PageId}/edit", method = RequestMethod.POST)
    public String addNewPage(@PathVariable Long PageId, WikiPage wikiPage, Principal principal, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User", result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("page", wikiPage);
        }

        wikiPage.setId(PageId);

        // Keep author the same
        wikiPage.setAuthor(wikiService.loadById(PageId).getAuthor());

        //Save new user
        wikiService.save(wikiPage);

        //Redirect depending on type of user
        if (getCurrentUser(principal).isAdmin()) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Wiki Page Edited!", "Page has been updated.", FlashMessage.Status.SUCCESS));
            return "redirect:/COMT/Wiki";
        } else {
            return "redirect:/Wiki/" + wikiPage.getId() + "/view";
        }

    }

    //Media
    @GetMapping("/Media")
    public String getMedia(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "main/Media";
    }

    //About
    @GetMapping("/About")
    public String getAbout(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());
        return "main/About";
    }


    //Minecraft
    @GetMapping("/Minecraft")
    public String getMCHome(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        return "minecraft/MCHome";
    }

    @GetMapping("/Minecraft/gov")
    public String getGovernmentInfo(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/YourGovernment";
    }

    @GetMapping("/Minecraft/vote")
    public String getVoting(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/VotingCenter";
    }

    @GetMapping("/Minecraft/vote/VotingBooth")
    public String getVotingBooth(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/VotingBooth";
    }

    @GetMapping("/Minecraft/vote/allCitizens")
    public String getAllCitizens(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/AllCitizens";
    }

    @GetMapping("/Minecraft/vote/register")
    public String getRegister(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/Register";
    }

    @GetMapping("/Minecraft/vote/runForOffice")
    public String getAddCandidate(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/AddCandidate";
    }

    @GetMapping("/Minecraft/vote/Polls")
    public String getPolls(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/Polls";
    }

    @GetMapping("/Minecraft/vote/BallotInfo")
    public String getBallotInfo(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/BallotInfo";
    }

    @GetMapping("/Minecraft/vote/results")
    public String getResults(Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        return "minecraft/ElectionResults";
    }

    // Newsroom
    @GetMapping("/Newsroom")
    public String getNewsroomHome(Model model, Principal principal) {

        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("notificationCount", accountNotificationService.findByUser(user.getId()).size());

        // Newsroom posts
        List<Post> allPosts = postService.getByApproval(true);
        List<Post> recentNews = postService.getWikiPagesByCategory(PostCategory.NEWS.name());
        List<Post> recentPosts = postService.getFixedAmount(6);

        if(recentNews.size() == 0){
            // if no news posts at all
            Post blank = new Post("No Posts", "No posts were found in database", PostCategory.NEWS.name(), "");
            blank.setLastEdited(LocalDateTime.MIN);
            blank.setId(-1L);
            blank.setImagePath("no-image.png");
            model.addAttribute("slide1", blank);
            model.addAttribute("slide2", blank);
            model.addAttribute("slide3", blank);
            model.addAttribute("slide4", blank);
            model.addAttribute("slide5", blank);
            model.addAttribute("slide6", blank);
            model.addAttribute("slide7", blank);
            model.addAttribute("slide8", blank);
            model.addAttribute("slide9", blank);
            model.addAttribute("slide10", blank);
        }else{
            model.addAttribute("slide1", recentNews.get(LogicTools.clamp(0, 0, recentNews.size()-1)));
            model.addAttribute("slide2", recentNews.get(LogicTools.clamp(1, 0, recentNews.size()-1)));
            model.addAttribute("slide3", recentNews.get(LogicTools.clamp(2, 0, recentNews.size()-1)));
            model.addAttribute("slide4", recentNews.get(LogicTools.clamp(3, 0, recentNews.size()-1)));
            model.addAttribute("slide5", recentNews.get(LogicTools.clamp(4, 0, recentNews.size()-1)));
            model.addAttribute("slide6", recentNews.get(LogicTools.clamp(5, 0, recentNews.size()-1)));
            model.addAttribute("slide7", recentNews.get(LogicTools.clamp(6, 0, recentNews.size()-1)));
            model.addAttribute("slide8", recentNews.get(LogicTools.clamp(7, 0, recentNews.size()-1)));
            model.addAttribute("slide9", recentNews.get(LogicTools.clamp(8, 0, recentNews.size()-1)));
            model.addAttribute("slide10", recentNews.get(LogicTools.clamp(9, 0, recentNews.size()-1)));
        }

        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("allPosts", allPosts);

        //Announcements
        List<Announcement> announcements = announcementService.getByApproval(true);
        model.addAttribute("announcements", announcements);
        model.addAttribute("isAnnouncement", announcements.size() > 0);

        return "newsroom/NewsroomHome";
    }
}
