package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.PostRepository;
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
    @Autowired
    private final PostRepository postRepository;

    private AttachmentService attachmentService;


    public PostService(PostRepository postRepository, AttachmentService attachmentService) {
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
    }

    public Post loadById(Long id){
        return postRepository.findById(id);
    }

    public List<Post> getByCategory(String category){
        return postRepository.findByCategory(category);
    }

    public List<Post> getByCategoryFixedAmountWithException(String category, Long exception, int entries){
        List<Post> original = postRepository.findByCategory(category, exception), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size(); i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public List<Post> getByApproval(boolean enabled){
        return postRepository.getByApproval(enabled);
    }

    public List<Post> getByApprovalFixedAmountWithException(boolean enabled, long exception, int entries){
        List<Post> original = postRepository.getByApproval(enabled, exception), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size(); i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public void delete(Post post){
        postRepository.delete(post.getId());
    }

    public void save(Post post){
        post.setLastEdited(LocalDateTime.now());
        post.setImagePath("/images/default-posts/" + post.getCategory() + ".jpg");
        postRepository.save(post);
    }

    public List<Post> getFixedAmount(int entries){
        List<Post> original = postRepository.getByApproval(true), outputList = new ArrayList<>();
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

    public void save(Post post, MultipartFile file) throws IOException {
        // check if file is empty or bad path
        if(file.getName().contains("..")) {
            throw new IOException("File contains a bad filepath");
        }else if(file.isEmpty()){
            try{
                post.setImagePath(postRepository.findById(post.getId()).getImagePath());
                // File is not present, just save the text
                post.setLastEdited(LocalDateTime.now());
                postRepository.save(post);
            } catch (Exception e){
                throw new IOException("File contains no data");
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
                throw new IOException("File is not an image");
            }
        }


    }

    public void enable(Post post, boolean enable) {
        postRepository.enable(post.getId(), enable);
    }

    public void forceSave(Post post) {
        post.setLastEdited(LocalDateTime.now());
        postRepository.save(post);
    }

    public void addView(Post post) {
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
}
