package com.costi.csw9.Controller;
import com.costi.csw9.Model.*;
import com.costi.csw9.Service.*;
import com.costi.csw9.Util.Slide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
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

    @Autowired
    public FrontEndController(UserService userService, RegistrationService registrationService, WikiService wikiService, AnnouncementService announcementService, AccountLogService accountLogService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.wikiService = wikiService;
        this.announcementService = announcementService;
        this.accountLogService = accountLogService;

    }
    // Theme
    private String choseTheme(){
        LocalDate today = LocalDate.now();
        if(today.getMonth().name().equalsIgnoreCase("July")){
            return "/XpTheme.css";
        }else if(today.getMonthValue() > 7 && today.getMonthValue() <=12){
            return "/White.css";
        }else{
            return "/Dark.css";
        }
    }

    //Account
    private User getCurrentUser(Principal principal) {
        if(principal == null){
            return new User("NULL","NULL","Not Signed In","error", UserRole.USER);
        }
        String username = principal.getName();
        User u = userService.loadUserObjectByUsername(username);
        return u;
    }

    @RequestMapping("/Account")
    public String editUser(Model model, Principal principal, RedirectAttributes redirectAttributes){
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);
        model.addAttribute("action", "/Account/edit");
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        model.addAttribute("logs", accountLogService.findByUser(user.getId()));
        return "main/ViewAccount";
    }

    @PostMapping("/Account/edit")
    public String updateUser(User user, BindingResult result, RedirectAttributes redirectAttributes, Principal principal){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User",result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("user",user);
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
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("✅ Account Successfully Edited", "Changes saved to server", FlashMessage.Status.SUCCESS));
        return "redirect:/Account";
    }

    @GetMapping("/SignUp")
    public String getNewAccount(Model model, RedirectAttributes redirectAttributes) {
        if(!model.containsAttribute("user")) {
            model.addAttribute("user",new User());
        }
        model.addAttribute("action","/SignUp/post");
        model.addAttribute("theme", choseTheme());
        return "main/NewAccount";
    }

    @RequestMapping(value = "/SignUp/post", method = RequestMethod.POST)
    public String addNewUser(User user, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("user", user);

            // Redirect back to the form
            return "redirect:/";
        }
        if(user.getRole().name().equals("ADMIN")){
            registrationService.registerAdmin(user);
            return "redirect:/";
        }
        registrationService.registerUser(user);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("✅ Costi Account Created!", "Please sign in to continue.", FlashMessage.Status.SUCCESS));
        return "redirect:/";
    }

    //Moderator
    @GetMapping("/COMT/Wiki")
    public String getCostiOnlineWikiTools(Model model, Principal principal, RedirectAttributes redirectAttributes){

        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("disabled", wikiService.getByApproval(false));
        model.addAttribute("enabled", wikiService.getByApproval(true));
        model.addAttribute("theme", choseTheme());
        return "moderator/WikiTools";
    }

    @GetMapping("/COMT/Accounts")
    public String getCostiOnlineAccountTools(Model model, Principal principal, RedirectAttributes redirectAttributes){

        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());

        model.addAttribute("all", userService.loadAll());
        return "moderator/AccountTools";
    }

    @GetMapping("/COMT/Announcements")
    public String getCostiOnlineAnnouncementTools(Model model, Principal principal, RedirectAttributes redirectAttributes){

        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("enabled", announcementService.getByApproval(true));
        model.addAttribute("disabled", announcementService.getByApproval(false));
        model.addAttribute("theme", choseTheme());
        return "moderator/AnnouncementTools";
    }

    @GetMapping("/COMT/Announcements/Create")
    public String getCreateAnnouncement(Model model, Principal principal, RedirectAttributes redirectAttributes){
        if(!model.containsAttribute("announcement")) {
            model.addAttribute("announcement",new Announcement());
        }
        model.addAttribute("action","/COMT/Announcements/Create");
        model.addAttribute("title", "Create New Announcement");
        model.addAttribute("theme", choseTheme());


        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "moderator/AnnouncementMaker";
    }

    @RequestMapping(value = "/COMT/Announcements/Create", method = RequestMethod.POST)
    public String addNewAnnouncement(Announcement announcement, Principal principal, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("announcement", announcement);

            // Redirect back to the form
            return "redirect:/COMT/Announcements/Create";
        }

        announcementService.save(announcement);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Announcement has been created", "To publish it, please press enable", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/enable", method = RequestMethod.POST)
    public String enableAnnouncement(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Announcement announcement = announcementService.findById(id);
        if(getCurrentUser(principal).isAdmin()){
            announcementService.enable(announcement, true);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Announcement Published!", "Announcement is publicly visible", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/disable", method = RequestMethod.POST)
    public String disableAnnouncement(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Announcement announcement = announcementService.findById(id);
        if(getCurrentUser(principal).isAdmin()){
            announcementService.enable(announcement, false);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Announcement Privated!", "Announcement was removed from public view", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping("/COMT/Announcements/{id}/edit")
    public String getEditAnnouncement(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes){
        User current = getCurrentUser(principal);
        Announcement announcement = announcementService.findById(id);

        model.addAttribute("announcement", announcement);
        model.addAttribute("action","/COMT/Announcements/" + id + "/edit");
        model.addAttribute("title", "Edit Announcement");

        model.addAttribute("user", current);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        return "moderator/AnnouncementMaker";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/edit", method = RequestMethod.POST)
    public String editAnnouncement(@PathVariable Long id, Announcement announcement, Principal principal, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User",result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("announcement",announcement);
        }

        //Transfer id
        announcement.setId(id);

        //Save
        announcementService.save(announcement);

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Announcement Edited", "Announcement contents was modified.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/COMT/Announcements/{id}/delete", method = RequestMethod.POST)
    public String deleteAnnouncement(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Announcement announcement = announcementService.findById(id);
        if(getCurrentUser(principal).isAdmin()){
            announcementService.delete(announcement);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Announcement Deleted!", "Announcement was permanently removed from database", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Announcements";
    }

    @RequestMapping(value = "/Accounts/{accountId}/lock", method = RequestMethod.POST)
    public String lockAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if(getCurrentUser(principal).isAdmin()){
            if(!userService.lock(user, true)){
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            }else{
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Account Locked", user.getFirstName() + " " + user.getLastName() + " is locked and can no longer log in.", FlashMessage.Status.SUCCESS));
            }
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";
    }

    @RequestMapping(value = "/Accounts/{accountId}/unlock", method = RequestMethod.POST)
    public String unlockAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if(getCurrentUser(principal).isAdmin()){
            userService.lock(user, false);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Account Unlocked!", user.getFirstName() + " " + user.getLastName() + " is now unlocked.", FlashMessage.Status.SUCCESS));

        return "redirect:/COMT/Accounts";
    }

    @RequestMapping(value = "/Accounts/{accountId}/enable", method = RequestMethod.POST)
    public String enableAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if(getCurrentUser(principal).isAdmin()){
            if(!userService.enable(user, true)){
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            }else{
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Account Enabled", user.getFirstName() + " " + user.getLastName() + " is now enabled.", FlashMessage.Status.SUCCESS));
            }
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";
    }

    @RequestMapping(value = "/Accounts/{accountId}/demote", method = RequestMethod.POST)
    public String demoteAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if(getCurrentUser(principal).isAdmin()){
            if(!userService.enable(user, false)){
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            }else{
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Account Demoted", user.getFirstName() + " " + user.getLastName() + " is now a user.", FlashMessage.Status.SUCCESS));
            }
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";

    }

    @RequestMapping(value = "/Accounts/{accountId}/disable", method = RequestMethod.POST)
    public String disableAccount(@PathVariable Long accountId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.loadUserObjectById(accountId);
        if(getCurrentUser(principal).isAdmin()){
            if(!userService.enable(user, false)){
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "You are unable to modify owner account", FlashMessage.Status.DANGER));
            }else{
                redirectAttributes.addFlashAttribute("flash",new FlashMessage("Account Disabled", user.getFirstName() + " " + user.getLastName() + " is now enabled.", FlashMessage.Status.SUCCESS));
            }
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
        }
        return "redirect:/COMT/Accounts";
    }
    //Main
    @GetMapping("/")
    public String getHome(Model model, Principal principal, RedirectAttributes redirectAttributes){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());

        List<Announcement> announcements = announcementService.getByApproval(true);
        model.addAttribute("announcements", announcements);
        model.addAttribute("isAnnouncement", announcements.size() > 0);

        List<WikiPage> random = wikiService.getByApproval(true);
        Collections.shuffle(random);

        if(random.size() > 3){
            random = new ArrayList<>(random.subList(0, 3));
        }

        model.addAttribute("wiki", random);
        return "main/Home";
    }
    @GetMapping("/Projects")
    public String getProjects(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("theme", choseTheme());
        return "main/Projects";
    }
    @GetMapping("/login")
    public String getLogin(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        return "main/login";
    }

    //Wiki
    @GetMapping("/Wiki")
    public String getWikiHome(Model model, Principal principal){

        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        List<WikiPage> allEnabled = wikiService.getByApproval(true);
        model.addAttribute("all", allEnabled);
        model.addAttribute("categories",WikiCategory.values());
        model.addAttribute("theme", choseTheme());
        return "wiki/WikiHome";
    }
    @GetMapping("/Wiki/Create")
    public String getCreateWiki(Model model, Principal principal, RedirectAttributes redirectAttributes){
        if(!model.containsAttribute("page")) {
            model.addAttribute("page",new WikiPage(getCurrentUser(principal)));
        }
        model.addAttribute("isAllowed", true);
        model.addAttribute("action","/Wiki/Create/post");
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("title", "Create New Wiki Page");
        model.addAttribute("theme", choseTheme());


        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "wiki/NewWiki";
    }
    @RequestMapping(value = "/Wiki/Create/post", method = RequestMethod.POST)
    public String addNewPage(WikiPage wikiPage, Principal principal, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);

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
    public String viewPage(Model model, Principal principal, RedirectAttributes redirectAttributes, @PathVariable Long PageId){
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

        return "wiki/viewWiki";
    }
    @RequestMapping(value = "/Wiki/{PageId}/delete", method = RequestMethod.POST)
    public String deleteWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        WikiPage page = wikiService.loadById(PageId);
        if(getCurrentUser(principal).isAdmin()){
            wikiService.delete(page);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Wiki Page deleted!", "Page is no longer accessible nor recoverable.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Wiki";
    }
    @RequestMapping(value = "/Wiki/{PageId}/enable", method = RequestMethod.POST)
    public String enableWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        WikiPage page = wikiService.loadById(PageId);
        if(getCurrentUser(principal).isAdmin()){
            wikiService.enable(page, true);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Wiki Page Published!", "Page is now accessible by non-administrators on the Costipedia page", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Wiki";
    }

    @RequestMapping(value = "/Wiki/{PageId}/disable", method = RequestMethod.POST)
    public String disableWikiPage(@PathVariable Long PageId, Principal principal, RedirectAttributes redirectAttributes) {
        WikiPage page = wikiService.loadById(PageId);
        if(getCurrentUser(principal).isAdmin()){
            wikiService.enable(page, false);
        }else{
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Invalid Permissions!", "Please use a moderator account to continue.", FlashMessage.Status.DANGER));
            System.out.println("Invalid Permissions");
        }

        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Wiki Page disabled!", "Page is no longer accessible by public.", FlashMessage.Status.SUCCESS));
        return "redirect:/COMT/Wiki";
    }
    @RequestMapping("/Wiki/{PageId}/edit")
    public String getEditWiki(@PathVariable Long PageId, Model model, Principal principal, RedirectAttributes redirectAttributes){
        User current = getCurrentUser(principal);
        WikiPage page = wikiService.loadById(PageId);

        model.addAttribute("page", page);
        model.addAttribute("isAllowed", (current.isAdmin() || page.getAuthor().equals(current)));
        model.addAttribute("action","/Wiki/" + PageId + "/edit");
        model.addAttribute("categories", WikiCategory.values());
        model.addAttribute("title", "Edit Wiki Page");

        model.addAttribute("user", current);
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        return "wiki/NewWiki";
    }
    @RequestMapping(value = "/Wiki/{PageId}/edit", method = RequestMethod.POST)
    public String addNewPage(@PathVariable Long PageId, WikiPage wikiPage, Principal principal, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User",result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("page",wikiPage);
        }

        wikiPage.setId(PageId);

        // Keep author the same
        wikiPage.setAuthor(wikiService.loadById(PageId).getAuthor());

        //Save new user
        wikiService.save(wikiPage);

        //Redirect depending on type of user
        if(getCurrentUser(principal).isAdmin()){
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Wiki Page Edited!", "Page has been updated.", FlashMessage.Status.SUCCESS));
            return "redirect:/COMT/Wiki";
        }else{
            return "redirect:/Wiki/" + wikiPage.getId() + "/view";
        }

    }

    //Media
    @GetMapping("/Media")
    public String getMedia(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        return "main/Media";
    }

    //About
    @GetMapping("/About")
    public String getAbout(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("theme", choseTheme());
        return "main/About";
    }


    //Minecraft
    @GetMapping("/Minecraft")
    public String getMCHome(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "minecraft/MCHome";
    }
    @GetMapping("/Minecraft/gov")
    public String getGovernmentInfo(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/yourGovernment";
    }
    @GetMapping("/Minecraft/vote")
    public String getVoting(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/votingCenter";
    }
    @GetMapping("/Minecraft/vote/VotingBooth")
    public String getVotingBooth(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/votingBooth";
    }
    @GetMapping("/Minecraft/vote/allCitizens")
    public String getAllCitizens(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/allCitizens";
    }
    @GetMapping("/Minecraft/vote/register")
    public String getRegister(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/register";
    }
    @GetMapping("/Minecraft/vote/runForOffice")
    public String getAddCandidate(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/addCandidate";
    }
    @GetMapping("/Minecraft/vote/Polls")
    public String getPolls(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/polls";
    }
    @GetMapping("/Minecraft/vote/BallotInfo")
    public String getBallotInfo(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/ballotInfo";
    }
    @GetMapping("/Minecraft/vote/results")
    public String getResults(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/electionResults";
    }
}
