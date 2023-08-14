package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.AccountNotificationRepository;
import com.costi.csw9.Repository.AnnouncementRepository;
import com.costi.csw9.Repository.PostRepository;
import com.costi.csw9.Repository.UserRepository;
import com.costi.csw9.Util.LogicTools;
import com.costi.csw9.Validation.FileValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.costi.csw9.Util.LogicTools.POST_IMAGE_PATH;

@Service
public class COMTService {
    private final AnnouncementRepository announcementRepository;
    private final PostRepository postRepository;
    private final AttachmentService attachmentService;
    private final AccountNotificationRepository accountNotificationRepository;
    private final UserRepository userRepository;

    public COMTService(AnnouncementRepository announcementRepository, PostRepository postRepository, AttachmentService attachmentService, AccountNotificationRepository accountNotificationRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
        this.accountNotificationRepository = accountNotificationRepository;
        this.userRepository = userRepository;
    }

    /*
        Announcements
     */

    public Announcement findAnnouncementById(Long id) throws Exception{
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);

        // Check if announcement exists
        if(optionalAnnouncement.isPresent()){
            return optionalAnnouncement.get();
        }else{
            throw new Exception("Announcement" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<Announcement> findAllAnnouncements(){
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement){
        announcement.setDate(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id){
        announcementRepository.deleteById(id);
    }

    /*
        Newsroom
     */

    public Post findPostById(Long id) throws Exception {
        try{
            return postRepository.findById(id).get();
        }catch (Exception e){
            throw new Exception("Post" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<Post> findAllPosts(){return postRepository.findAll();}

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public void savePost(Post post){
        post.setLastEdited(LocalDateTime.now());
        if(post.getId() == null){
            post.setId(-1L);
        }
        Optional<Post> optionalPost = postRepository.findById(post.getId());

        if (!optionalPost.isPresent()) {
            post.setImagePath("/images/default-posts/" + post.getCategory() + ".jpg");
            postRepository.save(post);
        } else {
            //Post already Exists
            post.setImagePath(optionalPost.get().getImagePath());
            postRepository.save(post);
        }
    }

    public void savePost(Post post, MultipartFile file) throws Exception{
        if(file.isEmpty()){
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
        }else{
            // File is present, check if valid file name

            String validFilename = file.getOriginalFilename();
            if (!FileValidator.isValidFileName(file)) {
                // If the filename is not valid, generate a new filename
                String newFilename = FileValidator.generateValidFilename(validFilename);
                if (newFilename == null) {
                    throw new Exception("Failed to generate a valid filename");
                }

                validFilename = newFilename;
            }

            String fileType = "";
            if (validFilename != null) {
                int dotIndex = validFilename.lastIndexOf(".");
                if (dotIndex > 0 && dotIndex < validFilename.length() - 1) {
                    fileType = validFilename.substring(dotIndex + 1).toLowerCase();
                }
            }

            if(fileType.toLowerCase().equals("png") ||
                    fileType.toLowerCase().equals("jpg") ||
                    fileType.toLowerCase().equals("jpeg") ||
                    fileType.toLowerCase().equals("gif") ||
                    fileType.toLowerCase().equals("bmp") ||
                    fileType.toLowerCase().equals("wbmp") ||
                    fileType.toLowerCase().equals("tiff") ||
                    fileType.toLowerCase().equals("tif") ||
                    fileType.toLowerCase().equals("ico") ||
                    fileType.toLowerCase().equals("pnm")){

                //file types that need validation
                try {
                    ImageIO.read(file.getInputStream()).toString();
                    //File is valid and not null:
                    try {
                        databaseSave(post, file);
                    } catch (Exception e) {
                        throw new IOException(e.getMessage());
                    }

                } catch (Exception e) {
                    throw e;
                }

            }else if(fileType.toLowerCase().equals("webp") || fileType.toLowerCase().equals("avif")){
                // Special file types that do not currently require validation
                try {
                    databaseSave(post, file);
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }
            }else{
                //unknown type
                throw new Exception("Unsupported file type");
            }
        }
    }

    private void databaseSave(Post post, MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(file);
        post.setLastEdited(LocalDateTime.now());
        post.setImagePath(POST_IMAGE_PATH + attachment.getId());
        postRepository.save(post);
    }

    /*
        Notifications
     */

    public List<AccountNotification> findAllNotifications(){
        return accountNotificationRepository.findAll();
    }
    public void saveNotification(AccountNotification notification){
        accountNotificationRepository.save(notification);
    }

    /*
        Users
     */

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
}
