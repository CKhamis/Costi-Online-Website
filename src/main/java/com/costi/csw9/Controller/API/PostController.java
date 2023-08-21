package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.DTO.ResponseMessage;
import com.costi.csw9.Model.Post;
import com.costi.csw9.Service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/newsroom")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getPublicPosts() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/view")
    public ResponseEntity<?> getPostById(@RequestParam("id") Long id, HttpSession session) {
        try {
            Post post = postService.findById(id);
            if (session.getAttribute("noViewIncrement" + post.getId()) == null) {
                postService.addView(post);
                session.setAttribute("noViewIncrement" + post.getId(), true);
            }

            return ResponseEntity.ok(post);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Post Not Found", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Post Not Public", ResponseMessage.Severity.LOW, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Error Getting Post", ResponseMessage.Severity.MEDIUM, e.getMessage()));
        }
    }
}