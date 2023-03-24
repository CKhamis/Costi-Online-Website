package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.PostRepository;
import com.costi.csw9.Util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
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

    public List<Post> getFixedAmount(int entries){
        List<Post> original = postRepository.getByApproval(true), outputList = new ArrayList<>();
        for (int i = 0; i < entries && i < original.size() - 1; i++) {
            outputList.add(original.get(i));
        }
        return outputList;
    }

    public void save(Post post, MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        post.setImageName(fileName);
        post.setLastEdited(LocalDateTime.now());
        postRepository.save(post);

        FileUploadUtil.saveFile(post.getId().toString(), fileName, file);
    }

    public void enable(Post post, boolean enable) {
        postRepository.enable(post.getId(), enable);
    }

}
