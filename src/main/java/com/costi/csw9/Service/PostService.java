package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.PostRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.costi.csw9.Util.LogicTools.POST_IMAGE_PATH;

@Service
public class PostService {
    private final PostRepository postRepository;
    private AttachmentService attachmentService;


    public PostService(PostRepository postRepository, AttachmentService attachmentService) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
    }

    public Post loadById(Long id) throws Exception {
        try{
            return postRepository.findById(id).get();
        }catch (Exception e){
            throw new Exception("Post" + LogicTools.NOT_FOUND_MESSAGE);
        }

    }

    public List<Post> getByCategory(String category){
        return postRepository.findByCategory(category);
    }

    public List<Post> getByCategoryFixedAmountWithException(String category, Long exception, int entries){
        List<Post> original = postRepository.findByCategoryAndIdNot(category, exception), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size(); i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public List<Post> getByApproval(boolean enabled){
        return postRepository.findByEnabled(enabled);
    }

    public List<Post> getByApprovalFixedAmountWithException(boolean enabled, long exception, int entries){
        List<Post> original = postRepository.findByEnabledAndIdNot(enabled, exception), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size(); i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public void delete(Post post, User user) throws Exception {
        // Check if user is owner
        if(!user.isOwner()){
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
        postRepository.deleteById(post.getId());
    }

    public void save(Post post, User user) throws Exception {
        // Check if user is owner
        if(!user.isOwner()){
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }

        post.setLastEdited(LocalDateTime.now());
        post.setImagePath("/images/default-posts/" + post.getCategory() + ".jpg");
        postRepository.save(post);
    }

    public List<Post> getFixedAmount(int entries){
        List<Post> original = postRepository.findByEnabled(true), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size(); i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public List<Post> getFixedAmount(int entries, String category){
        List<Post> original = postRepository.findByCategory(category), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size(); i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public void save(Post post, MultipartFile file, User user) throws Exception {
        // Check if user is owner
        if(!user.isOwner()){
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }

        // check if file is empty or bad path
        if(file.getName().contains("..")) {
            throw new Exception("File contains a bad filepath");
        }else if(file.isEmpty()){
            // File is not present. Check to see if there is an existing post to edit
            try{
                Post originalPost = postRepository.findById(post.getId()).get();
                // The original post exists. User just wants to change the text
                post.setImagePath(originalPost.getImagePath());
                post.setLastEdited(LocalDateTime.now());
                postRepository.save(post);
            } catch (Exception e){
                // The file is not present and there is no existing post to edit
                throw new Exception("Original post could not be found. Id = " + post.getId());
            }
        }else {
            // File is present
            // Validate that the uploaded file is an image
            try {
                ImageIO.read(file.getInputStream()).toString();
                //File is valid and not null:
                try {
                    Attachment attachment = attachmentService.saveAttachment(file);
                    post.setLastEdited(LocalDateTime.now());
                    post.setImagePath(POST_IMAGE_PATH + attachment.getId());
                    postRepository.save(post);
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }

            } catch (Exception e) {
                throw e;
            }
        }


    }

    public void enable(Long id, boolean enable, User user) throws Exception {
        // Check if user is owner
        if(!user.isOwner()){
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
        postRepository.setEnabledById(id, enable);
    }

    public void addView(Post post) {
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
}
