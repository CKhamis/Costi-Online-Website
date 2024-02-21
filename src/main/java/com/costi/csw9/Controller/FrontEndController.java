package com.costi.csw9.Controller;

import com.costi.csw9.Model.*;
import com.costi.csw9.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
public class FrontEndController {
    private final UserService userService;
    private final WikiService wikiService;
    private static final String VERSION = "10.0.2";

    @Autowired
    public FrontEndController(UserService userService, WikiService wikiService) {
        this.userService = userService;
        this.wikiService = wikiService;
    }

    /**************************
       Common Model Attributes
     **************************/

    // Theme
    private String chooseTheme() {
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
        User u = userService.loadUserByUsername(username);
        return u;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {
        // If user is logged in
        if (principal != null) {
            User user = getCurrentUser(principal);
            model.addAttribute("user", user);
        }
        
        // User is logged in or out
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", chooseTheme());
    }

    /*******************
        Page Mappings
     ******************/

    @RequestMapping("/Account")
    public String editUser() {
        return "main/ViewAccount";
    }

    @GetMapping("/SignUp")
    public String getNewAccount() {
        return "main/NewAccount";
    }

    //Moderator
    @GetMapping("/COMT/Wiki")
    public String getCostiOnlineWikiTools() {
        return "moderator/WikiTools";
    }

    @GetMapping("/COMT/LED")
    public String getCostiOnlineLEDTools() {
        return "moderator/LEDTools";
    }

    @GetMapping("/COMT/LED/{id}")
    public String getCostiOnlineLEDInfo(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "moderator/LEDInfo";
    }

    @GetMapping("/COMT/Accounts")
    public String getCostiOnlineAccountTools() {
        return "moderator/AccountTools";
    }

    @GetMapping("/COMT/Attachments")
    public String getCostiOnlineAttachmentTools() {
        return "moderator/AttachmentTools";
    }

    @GetMapping("/COMT/Newsroom")
    public String getNewsroomTools(Model model) {
        model.addAttribute("categories", PostCategory.values());
        return "moderator/NewsroomTools";
    }

    @GetMapping("/COMT/Accounts/{id}")
    public String getCostiOnlineAccountSettings(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "moderator/AccountInfo";
    }

    @GetMapping("/COMT/Notifications")
    public String getCostiOnlineNotificationSettings() {
        return "moderator/NotificationTools";
    }

    @GetMapping("/COMT/Announcements")
    public String getCostiOnlineAnnouncementTools() {
        return "moderator/AnnouncementTools";
    }

    @GetMapping("/COMT/Content")
    public String getCostiOnlineContentTools() {
        return "moderator/ContentTools";
    }

    @GetMapping("/COMT/Content/{id}/edit")
    public String getCostiOnlineContentEditor(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "moderator/EditContent";
    }

    //Main
    @GetMapping("/")
    public String getHome(Model model) {
        model.addAttribute("version", VERSION);
        return "main/Home";
    }

    @GetMapping("/Projects")
    public String getProjects() {
        return "main/Projects";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "main/login";
    }

    //Wiki
    @GetMapping("/Wiki")
    public String getWikiHome() {
        return "wiki/WikiHome";
    }

    @GetMapping("/Wiki/Create")
    public String getCreateWiki(Model model, Principal principal) {
        if(principal == null){
            return "main/login";
        }
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("id", null);
        model.addAttribute("title", "Create New Wiki Page");

        return "wiki/WikiMaker";
    }
    @GetMapping("/Wiki/{pageId}/edit")
    public String getCreateWiki(Model model, @PathVariable Long pageId, Principal principal) {
        User current = getCurrentUser(principal);
        try {
            WikiPage wiki = wikiService.loadById(pageId);
            if(current.isAdmin() || wiki.getAuthor().equals(current)){
                model.addAttribute("categories", WikiCategory.values());
                model.addAttribute("id", pageId);
                model.addAttribute("title", "Edit Wiki Page");

                return "wiki/WikiMaker";
            }
        } catch (Exception e) {
            return "redirect:/Wiki";
        }
        return "redirect:/Wiki";
    }


    @RequestMapping("/Wiki/{pageId}/view")
    public String viewPage(Model model, Principal principal, @PathVariable Long pageId) {
        User current = getCurrentUser(principal);
        model.addAttribute("isAdmin", current.isAdmin());
        model.addAttribute("id", pageId);

        try {
            WikiPage wiki = wikiService.loadById(pageId);
            model.addAttribute("showEdit", (current.isAdmin() || wiki.getAuthor().equals(current)));

            return "wiki/ViewWiki";
        } catch (Exception e) {
            return "wiki/ViewWiki";
        }
    }

    //Media
    @GetMapping("/Media")
    public String getMedia() {
        return "main/Media";
    }

    //About
    @GetMapping("/About")
    public String getAbout() {
        return "main/About";
    }

    //Landing Page
    @GetMapping("/Tree")
    public String getLandingPage() {
        return "main/Tree";
    }

    //Minecraft
    @GetMapping("/Minecraft")
    public String getMCHome() {
        return "minecraft/MCHome";
    }

    @GetMapping("/Minecraft/gov")
    public String getGovernmentInfo() {
        return "minecraft/YourGovernment";
    }

    // Axcel
    @GetMapping("/Axcel")
    public String getAxcel() {
        return "main/Axcel";
    }

    // Costi Labs
    @GetMapping("/Labs")
    public String getLabs() {
        return "labs/Home";
    }
    @GetMapping("/Labs/LED")
    public String getLED() {
        return "labs/LED";
    }

    // Newsroom
    @GetMapping("/Newsroom")
    public String getNewsroomHome() {
        return "newsroom/NewsroomHome";
    }

    @GetMapping("/Newsroom/{PageId}/view")
    public String getNewsroomView(@PathVariable Long PageId, Model model, Principal principal) {
        model.addAttribute("id", PageId);
        model.addAttribute("isOwner", getCurrentUser(principal).isOwner());
        return "newsroom/ViewPost";
    }

    @RequestMapping("/COMT/Newsroom/{PostId}/edit")
    public String getEditPost(@PathVariable Long PostId, Model model) {
        model.addAttribute("categories", PostCategory.values());
        model.addAttribute("id", PostId);
        return "moderator/EditPost";
    }
}