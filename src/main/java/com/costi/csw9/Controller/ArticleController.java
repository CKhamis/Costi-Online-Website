package com.costi.csw9.Controller;
import com.costi.csw9.Model.Article;
import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Model.User;
import com.costi.csw9.Service.ArticleService;
import com.costi.csw9.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }
    /*
    @GetMapping
    public List<Article> getarticles(){
        return articleService.getArticles();
    }

    @PostMapping
    public void registerNewArticle(@RequestBody Article article){
        articleService.addNewArticle(article);
    }

    @DeleteMapping(path = "{articleId}")
    public void deleteArticle(@PathVariable("articleId") Long articleId){
        articleService.deleteArticle(articleId);
    }

    @PutMapping(path = "{articleId}")
    public void modifyArticle(@PathVariable("articleId") Long articleId, @RequestParam(required = false) String name, @RequestParam(required = false) String email){
        articleService.modifyArticle(articleId, name, email);
    }*/
    //Account
    private User getCurrentUser(Principal principal) {
        // TODO: null recovery
        String username = principal.getName();
        User u = userService.loadUserObjectByUsername(username);
        return u;
    }

    //Main
    @GetMapping("/")
    public String getHome(Model model){
        return "main/Home";
    }
    @GetMapping("/Test")
    public String getTest(Model model){
        return "main/logout";
    }
    @GetMapping("/Projects")
    public String getProjects(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        return "main/Projects";
    }
    @GetMapping("/login")
    public String getLogin(Model model){
        return "main/login";
    }


    @GetMapping("/Minecraft")
    public String getMCHome(Model model){
        //List<Article> list = articleService.getArticles();
        //model.addAttribute("articles", list);
        return "minecraft/MCHome";
    }
    // Your Government
    @GetMapping("/Minecraft/gov")
    public String getGovernmentInfo(Model model){
        return "minecraft/yourGovernment";
    }

    // Voting Center
    @GetMapping("/Minecraft/vote")
    public String getVoting(Model model){
        return "minecraft/votingCenter";
    }
    @GetMapping("/Minecraft/vote/VotingBooth")
    public String getVotingBooth(Model model){
        return "minecraft/votingBooth";
    }
    @GetMapping("/Minecraft/vote/allCitizens")
    public String getAllCitizens(Model model){
        return "minecraft/allCitizens";
    }
    @GetMapping("/Minecraft/vote/register")
    public String getRegister(Model model){
        return "minecraft/register";
    }
    @GetMapping("/Minecraft/vote/runForOffice")
    public String getAddCandidate(Model model){
        return "minecraft/addCandidate";
    }
    @GetMapping("/Minecraft/vote/Polls")
    public String getPolls(Model model){
        return "minecraft/polls";
    }
    @GetMapping("/Minecraft/vote/BallotInfo")
    public String getBallotInfo(Model model){
        return "minecraft/ballotInfo";
    }
    @GetMapping("/Minecraft/vote/results")
    public String getResults(Model model){
        return "minecraft/electionResults";
    }

    //Adding New Articles
    @RequestMapping("/Minecraft/Articles/Add")
    public String formNewMember(Model model) {
        if(!model.containsAttribute("article")) {
            model.addAttribute("article",new Article());
        }
        model.addAttribute("action","/Articles/Upload");
        model.addAttribute("submit","Add");
        return "minecraft/uploadArticle";
    }

    @PostMapping("/Minecraft/Articles/Upload")
    public RedirectView saveMember(Article article, @RequestParam("image") MultipartFile file, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.person",result);

            // Add  person if invalid was received
            redirectAttributes.addFlashAttribute("article",article);
        }
        articleService.saveNew(article, file);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Success", "Person successfully added", FlashMessage.Status.SUCCESS));
        return new RedirectView("/Articles", true);
    }
}
