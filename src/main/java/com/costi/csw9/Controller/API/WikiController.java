package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.costi.csw9.Model.Post;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Service.UserService;
import com.costi.csw9.Service.WikiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<?> getWikiPageById(@RequestParam("id") Long id, HttpSession session) {
        try {
            WikiPage wikiPage = wikiService.findById(id);
            if (session.getAttribute("noViewIncrement" + wikiPage.getId()) == null) {
                wikiService.addView(wikiPage);
                session.setAttribute("noViewIncrement" + wikiPage.getId(), true);
            }

            return ResponseEntity.ok(wikiPage);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Wiki page Not Found", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Wiki page Not Public", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Getting wiki page", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }

    @GetMapping("/save")
    public ResponseEntity<?> save(@RequestBody @Valid WikiRequest request, Principal principal) {
        try {
            return ResponseEntity.ok(wikiService.save(request, getCurrentUser(principal)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("ID of wiki page is invalid", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Wiki page Not Public", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Getting wiki page", ResponseMessage.Severity.MEDIUM, e.getMessage()));
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
