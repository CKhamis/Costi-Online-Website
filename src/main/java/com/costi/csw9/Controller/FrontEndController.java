package com.costi.csw9.Controller;

import com.costi.csw9.Model.*;
import com.costi.csw9.Service.*;
import com.costi.csw9.Util.LogicTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private static final String VERSION = "7.1.1";

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
        User u = userService.findByEmail(username);
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
    public String editUser(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        model.addAttribute("action", "/Account/edit");
        model.addAttribute("logs", accountLogService.findByUser(user));
        return "main/ViewAccount";
    }

    @PostMapping("/Account/edit")
    public String updateUser(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes, Principal principal) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User", result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("user", user);

            String errors = getErrorString(result);

            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error editing account", errors, FlashMessage.Status.DANGER));

            return "redirect:/Account";
        }

        //Transfer id so it gets overwritten in data
        User currentUser = getCurrentUser(principal);
        user.setId(currentUser.getId());

        //Enable user if enabled
        user.setEnabled(currentUser.getEnabled());

        //Transfer role
        user.setRole(currentUser.getRole());

        //Save new user
        try {
            userService.save(user);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("✅ Account Successfully Edited", "Changes saved to server", FlashMessage.Status.SUCCESS));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error editing account", e.getMessage(), FlashMessage.Status.DANGER));
        }
        return "redirect:/Account";
    }

    @GetMapping("/SignUp")
    public String getNewAccount(Model model, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("action", "/SignUp");
        return "main/NewAccount";
    }

    @PostMapping(value = "/SignUp")
    public String addNewUser(@Valid  User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("user", user);

            String errors = getErrorString(result);

            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error creating account", errors, FlashMessage.Status.DANGER));

            // Redirect back to the form
            return "redirect:/SignUp";
        }

        if (user.getRole().name().equals("ADMIN")) {
            registrationService.registerAdmin(user);
            return "redirect:/";
        }

        try{
            registrationService.registerUser(user);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("✅ Costi Account Created!", "Please sign in to continue.", FlashMessage.Status.SUCCESS));
            return "redirect:/";
        }catch (Exception e){
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("user", user);

            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error creating account", e.getMessage(), FlashMessage.Status.DANGER));
            return "redirect:/SignUp";
        }

    }

    //Moderator
    @GetMapping("/COMT/Wiki")
    public String getCostiOnlineWikiTools(Model model) {
        model.addAttribute("disabled", wikiService.getByApproval(false));
        model.addAttribute("enabled", wikiService.getByApproval(true));
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
    public String getCostiOnlineAccountTools(Model model) {
        model.addAttribute("all", userService.loadAll());
        return "moderator/AccountTools";
    }

    @GetMapping("/COMT/Newsroom")
    public String getNewsroomTools(Model model) {
        model.addAttribute("categories", PostCategory.values());
        return "moderator/NewsroomTools";
    }

    @GetMapping("/COMT/Accounts/{id}")
    public String getCostiOnlineAccountSettings(Model model, Principal principal, RedirectAttributes redirectAttributes, @PathVariable Long id) {
        // TODO: add a nicer way to enable/disable, lock/unlock accounts
        model.addAttribute("all", userService.loadAll());
        model.addAttribute("action", "/COMT/Accounts/" + id + "/edit");

        //Selected User
        User selectedUser = userService.findById(id);
        model.addAttribute("selectedUser", selectedUser);
        List<AccountNotification> notifications = accountNotificationService.findByUser(selectedUser);
        model.addAttribute("SUNotificationCount", notifications.size());
        model.addAttribute("SUNotifications", notifications);
        List<AccountLog> logs = accountLogService.findByUser(selectedUser);
        model.addAttribute("SULogCount", logs.size());
        model.addAttribute("SULogs", logs);
        model.addAttribute("SUlastInteraction", logs.get(logs.size()-1).getDateCreated());
        model.addAttribute("SUCanSignIn", !selectedUser.getIsLocked() && selectedUser.getEnabled());
//        List<WikiPage> wikiPages = wikiService.getWikiPagesByAuthor(id);
//        model.addAttribute("SUWikiCount", wikiPages.size());
//        model.addAttribute("SUWikiPages", wikiPages);

        return "moderator/AdminAccountView";
    }


    @GetMapping("/COMT/Notifications")
    public String getCostiOnlineNotificationSettings() {
        return "moderator/NotificationTools";
    }

    @PostMapping("/COMT/Accounts/{id}/edit")
    public String adminUpdateUser(@Valid @ModelAttribute("selectedUser") User formUser, BindingResult result, RedirectAttributes redirectAttributes, Principal principal, @PathVariable Long id) {
        if (result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User", result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("selectedUser", formUser);

            String errors = getErrorString(result);

            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error editing user", errors, FlashMessage.Status.DANGER));
            return "redirect:/COMT/Accounts/" + id;
        }

        //Save new user
        try {
            User loggedInUser = userService.findByEmail(principal.getName());
            if(((!loggedInUser.isOwner()) && formUser.isOwner())){
                throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }else{
                userService.save(formUser);
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("✅ Account Successfully Edited", "Changes saved to server", FlashMessage.Status.SUCCESS));

            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("❌ Account Edit Failed", e.getMessage(), FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts/" + id;
    }

    @GetMapping("/COMT/Announcements")
    public String getCostiOnlineAnnouncementTools() {
        return "moderator/AnnouncementTools";
    }

    public static String getErrorString(BindingResult result) {
        String errors = "";
        if(result.getAllErrors().size() > 1){

            for(ObjectError error : result.getAllErrors()){
                errors += error.getDefaultMessage() + ". ";
            }
        }else{
            errors = result.getAllErrors().get(0).getDefaultMessage();
        }
        return errors;
    }

    //Main
    @GetMapping("/")
    public String getHome(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        model.addAttribute("version", VERSION);

        List<Post> recentNews = postService.findAll();
        generateSlides(model, recentNews);

        return "main/Home";
    }

    private void generateSlides(Model model, List<Post> recentNews) {
        if(recentNews.size() == 0){
            // if no news posts at all
            Post blank = new Post("No Posts", "No posts were found in database", PostCategory.NEWS.name(), "");
            blank.setLastEdited(LocalDateTime.MIN);
            blank.setId(-1L);
            blank.setImagePath("/images/default-posts/no-image.jpg");
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
    @GetMapping("/Wiki/{id}/edit")
    public String getCreateWiki(Model model, @PathVariable Long id) {
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("id", id);
        model.addAttribute("title", "Edit Wiki Page");

        return "wiki/WikiMaker";
    }


    @RequestMapping("/Wiki/{PageId}/view")
    public String viewPage(Model model, Principal principal, @PathVariable Long PageId) {
        User current = getCurrentUser(principal);
        model.addAttribute("isAdmin", current.isAdmin());
        model.addAttribute("id", PageId);

        try {
            WikiPage wiki = wikiService.loadById(PageId);
            model.addAttribute("showEdit", (current.isAdmin() || wiki.getAuthor().equals(current)));

            return "wiki/ViewWiki";
        } catch (Exception e) {
            return "wiki/ViewWiki";
        }
    }

    @PostMapping(value = "/Wiki/{PageId}/delete")
    public String deleteWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        try{
            wikiService.delete(PageId, getCurrentUser(principal));
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Wiki Page deleted!", "Page is no longer accessible nor recoverable.", FlashMessage.Status.SUCCESS));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error deleting wiki page", e.getMessage(), FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Wiki";
    }

    @RequestMapping("/Wiki/{PageId}/edit")
    public String getEditWiki(@PathVariable Long PageId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        User current = getCurrentUser(principal);
        try{
            WikiPage page = wikiService.loadById(PageId);

            model.addAttribute("isAllowed", (current.isAdmin() || page.getAuthor().equals(current)));
            model.addAttribute("action", "/Wiki/" + PageId + "/edit");
            model.addAttribute("categories", WikiCategory.values());
            model.addAttribute("title", "Edit Wiki Page");

            return "WikiMaker";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error viewing wiki page", e.getMessage(), FlashMessage.Status.DANGER));
            return "redirect:/COMT/Wiki";
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

    @GetMapping("/Minecraft/vote")
    public String getVoting() {
        return "minecraft/VotingCenter";
    }

    @GetMapping("/Minecraft/vote/VotingBooth")
    public String getVotingBooth() {
        return "minecraft/VotingBooth";
    }

    @GetMapping("/Minecraft/vote/allCitizens")
    public String getAllCitizens() {
        return "minecraft/AllCitizens";
    }

    @GetMapping("/Minecraft/vote/register")
    public String getRegister() {
        return "minecraft/Register";
    }

    @GetMapping("/Minecraft/vote/runForOffice")
    public String getAddCandidate() {
        return "minecraft/AddCandidate";
    }

    @GetMapping("/Minecraft/vote/Polls")
    public String getPolls() {
        return "minecraft/Polls";
    }

    @GetMapping("/Minecraft/vote/BallotInfo")
    public String getBallotInfo() {
        return "minecraft/BallotInfo";
    }

    @GetMapping("/Minecraft/vote/results")
    public String getResults(Model model) {
        return "minecraft/ElectionResults";
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
