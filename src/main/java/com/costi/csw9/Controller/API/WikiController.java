package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Model.Post;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Service.UserService;
import com.costi.csw9.Service.WikiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/wiki")
public class WikiController {
    private final WikiService wikiService;
    private final UserService userService;

    public WikiController(WikiService wikiService, UserService userService){
        this.wikiService = wikiService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<WikiPage>> getPublicWikiPages() {
        return ResponseEntity.ok(wikiService.findAll());
    }

    @GetMapping("/view")
    public ResponseEntity<?> getWikiPageById(@RequestParam("id") Long id, HttpSession session, Principal principal) {
        try {
            WikiPage wikiPage = wikiService.findById(id, getCurrentUser(principal));
            if (session.getAttribute("noViewIncrement" + wikiPage.getId()) == null) {
                wikiService.addView(wikiPage);
                session.setAttribute("noViewIncrement" + wikiPage.getId(), true);
            }

            return ResponseEntity.ok(wikiPage);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Wiki page Not Found", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error getting wiki page", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @PostMapping("/save")
    public String save(@Valid WikiRequest request, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        try {
            WikiPage page = wikiService.save(request, getCurrentUser(principal));
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Wiki submission sent", "Please allow a few days for Costi Online moderators to review your wiki page", FlashMessage.Status.SUCCESS));
            return "redirect:/Wiki/" + page.getId() + "/edit";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Error editing wiki page", "ID of wiki page is invalid", FlashMessage.Status.DANGER));
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid permissions", "Please sign in with account used to create the wiki page", FlashMessage.Status.DANGER));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("OOOOPS! Generic error (awwww man)", "Perhaps the title isn't unique?", FlashMessage.Status.DANGER));
        }

        String referer = httpServletRequest.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/Wiki");
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
