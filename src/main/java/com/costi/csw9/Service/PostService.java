package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.PostRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.costi.csw9.Util.LogicTools.POST_IMAGE_PATH;

@Service
public class PostService {
    private final PostRepository postRepository;
    private AttachmentService attachmentService;


    public PostService(PostRepository postRepository, AttachmentService attachmentService) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
    }

    public Post findById(Long id) throws Exception{
        Optional<Post> optionalPost = postRepository.findById(id);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            if(post.isEnabled()){
                return post;
            }
            throw new AccessDeniedException("Post" + LogicTools.INVALID_PERMISSIONS_MESSAGE);

        }else{
            throw new NoSuchElementException("Post" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<Post> findAll(){
        return postRepository.findByEnabledAndIsPublicOrderByLastEditedDesc(true, true);
    }

    public void addView(Post post) {
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
}
