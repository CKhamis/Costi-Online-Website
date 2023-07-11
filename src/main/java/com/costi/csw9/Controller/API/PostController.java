package com.costi.csw9.Controller.API;

import com.costi.csw9.Model.Post;
import com.costi.csw9.Service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

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
    public ResponseEntity<Post> getAnnouncementById(@RequestParam("id") Long id, HttpSession session) {
        try {
            Post post = postService.findById(id);
            if (post.isEnabled()) {

                if (session.getAttribute("noViewIncrement" + post.getId()) == null) {
                    postService.addView(post);
                    session.setAttribute("noViewIncrement" + post.getId(), true);
                }

                return ResponseEntity.ok(post);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}