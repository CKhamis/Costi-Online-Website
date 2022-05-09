package com.costi.csw9.Controller;
import com.costi.csw9.Model.Article;
import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@Controller
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
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

    @GetMapping("/")
    public String getArticles(Model model){
        List<Article> list = articleService.getArticles();
        model.addAttribute("articles", list);
        return "home";
    }
    // Your Government
    @GetMapping("/gov")
    public String getGovernmentInfo(Model model){
        return "yourGovernment";
    }

    // Voting Center
    @GetMapping("/vote")
    public String getVoting(Model model){
        return "votingCenter";
    }
    @GetMapping("/vote/VotingBooth")
    public String getVotingBooth(Model model){
        return "votingBooth";
    }
    @GetMapping("/vote/allCitizens")
    public String getAllCitizens(Model model){
        return "allCitizens";
    }
    @GetMapping("/vote/register")
    public String getRegister(Model model){
        return "register";
    }

    //Adding New Articles
    @RequestMapping("Articles/Add")
    public String formNewMember(Model model) {
        if(!model.containsAttribute("article")) {
            model.addAttribute("article",new Article());
        }
        model.addAttribute("action","/Articles/Upload");
        model.addAttribute("submit","Add");
        return "uploadArticle";
    }

    @PostMapping("Articles/Upload")
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
