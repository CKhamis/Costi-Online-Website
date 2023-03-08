package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class PostService {
    @Autowired
    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post loadById(Long id){
        return postRepository.findById(id);
    }

    public List<Post> getWikiPagesByCategory(String category){
        return postRepository.findByCategory(category);
    }

    public List<Post> getByApproval(boolean enabled){
        return postRepository.getByApproval(enabled);
    }

    public void delete(Post post){
        postRepository.delete(post.getId());
    }

    public void save(Post post){
        post.setLastEdited(LocalDateTime.now());

        postRepository.save(post);
    }

    public void enable(Post post, boolean enable) {
        postRepository.enable(post.getId(), enable);
    }

}
