package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.DTO.AccountNotificationRequest;
import com.costi.csw9.Model.DTO.ModeratorLightRequest;
import com.costi.csw9.Model.DTO.ModeratorWikiRequest;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.costi.csw9.Repository.*;
import com.costi.csw9.Util.LogicTools;
import com.costi.csw9.Validation.FileValidator;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.rmi.ConnectIOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.costi.csw9.Util.LogicTools.POST_IMAGE_PATH;

@Service
public class COMTService {
    private final AnnouncementRepository announcementRepository;
    private final PostRepository postRepository;
    private final AttachmentService attachmentService;
    private final AccountNotificationRepository accountNotificationRepository;
    private final UserRepository userRepository;
    private final LightRepository lightRepository;
    private final LightLogRepository lightLogRepository;
    private final LightService lightService;
    private final WikiRepository wikiRepository;
    private final AccountLogService accountLogService;

    public COMTService(AnnouncementRepository announcementRepository, PostRepository postRepository, AttachmentService attachmentService, AccountNotificationRepository accountNotificationRepository, UserRepository userRepository, LightRepository lightRepository, LightLogRepository lightLogRepository, LightService lightService, WikiRepository wikiRepository, AccountLogService accountLogService) {
        this.announcementRepository = announcementRepository;
        this.postRepository = postRepository;
        this.attachmentService = attachmentService;
        this.accountNotificationRepository = accountNotificationRepository;
        this.userRepository = userRepository;
        this.lightRepository = lightRepository;
        this.lightService = lightService;
        this.lightLogRepository = lightLogRepository;
        this.wikiRepository = wikiRepository;
        this.accountLogService = accountLogService;
    }

    /*
        Announcements
     */

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
    public void saveNotification(AccountNotificationRequest notification) throws Exception{
        // Check if user exists
        if(notification.getDestination().equals("All")) {
            List<User> allUsers = findAllUsers();
            for (User user : allUsers) {
                // Convert to notification
                AccountNotification accountNotification = new AccountNotification(notification, user);
                // Save
                accountNotificationRepository.save(accountNotification);
            }
        }else{
            // Find associated user
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(notification.getDestination()));

            if(optionalUser.isPresent()){
                // Convert to notification
                AccountNotification accountNotification = new AccountNotification(notification, optionalUser.get());
                // Save
                accountNotificationRepository.save(accountNotification);
            }else{
                throw new Exception("User" + LogicTools.NOT_FOUND_MESSAGE);
            }
        }
    }

    public void deleteNotification(Long id) {
        accountNotificationRepository.deleteById(id);
    }

    /*
        Lights
     */
    private final int INTERVAL = 500000;

    public List<Light> findAllLights(){
        return lightRepository.findAll();
    }

    public String saveLight(ModeratorLightRequest request) throws Exception{
        Light light;
        // Get or create new light
        if(request.getId() == null){
            // This is a new light. Create one
            light = new Light();
            light.setDateAdded(LocalDateTime.now());
            light.setStatus("New");
            light.setValues(request);
            light.setLastModified(LocalDateTime.now());
            lightRepository.save(light);
        }else{
            Optional<Light> optionalLight = lightRepository.findById(request.getId());
            if(optionalLight.isPresent()){
                // This light already exists, edit it
                light = optionalLight.get();
                light.setValues(request);
                light.setLastModified(LocalDateTime.now());
            }else{
                // Light could not be found with specified id
                throw new NoSuchElementException("The light with id " + request.getId() + " could not be found.");
            }
        }

        //Upload data and save to light
        String status = syncUp(light);

        if(status.charAt(0) != 'C'){
            throw new ConnectIOException("Error sending data: " + status);
        }

        return status;
    }

    public void deleteLight(Long id) {
        lightRepository.deleteById(id);
    }

    @Scheduled(fixedRate = INTERVAL)
    public void updateAllStatus() {
        List<Light> lights = lightRepository.findAllByOrderByDateAddedDesc();
        for (Light light : lights) {
            Hibernate.initialize(light.getLogs());
            try{
                syncDown(light.getId()); // Inefficient, but couldn't really figure out a better way
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }

    public void syncDown(Long id) throws Exception{
        Optional<Light> optionalLight = lightRepository.findById(id);

        if(optionalLight.isEmpty()){
            throw new NoSuchElementException("Light could not be found");
        }

        Light light = optionalLight.get();

        String currentStatus = light.getCurrentStatus();
        LightLog log = new LightLog(light, currentStatus);
        light.getLogs().add(log);

        System.out.println(currentStatus);
        lightLogRepository.save(log);
        lightRepository.save(light);

        if(currentStatus.charAt(0) != 'C'){
            throw new Exception(currentStatus);
        }
    }

    public String syncUp(Light light) throws Exception{
        String response = lightService.syncUp(light);
        if(response.charAt(0) != 'C'){
            throw new Exception(response);
        }
        return response;
    }

    public String syncUp(Long id) throws Exception{
        Optional<Light> optionalLight = lightRepository.findById(id);

        if(optionalLight.isEmpty()){
            throw new NoSuchElementException("Light could not be found");
        }

        Light light = optionalLight.get();

        String response = lightService.syncUp(light);
        if(response.charAt(0) != 'C'){
            throw new Exception(response);
        }
        return response;
    }

    public List<LightLog> findLogs(Long id) throws NoSuchElementException{
        Optional<Light> optionalLight = lightRepository.findById(id);

        if(optionalLight.isEmpty()){
            throw new NoSuchElementException("The specified light could not be found in Costi Online");
        }

        Light light = optionalLight.get();
        return lightLogRepository.findAllByLightOrderByDateCreatedDesc(light);
    }

    /*
        Wiki
     */

    public List<WikiPage> findAllWikiPages(){
        return wikiRepository.findAll();
    }

    public void deleteWikiPage(Long id){
        wikiRepository.deleteById(id);
    }

    public WikiPage saveWikiPage(ModeratorWikiRequest request, User author){
        // id must be null to create new pages
        if(request.getId() != null && request.getId() >= 0){
            Optional<WikiPage> optionalWikiPage = wikiRepository.findById(request.getId());
            if(optionalWikiPage.isPresent()){
                // Wiki Page already exists, check if the author matches
                WikiPage originalWikiPage = optionalWikiPage.get();

                originalWikiPage.moderatorEditValues(request);
                WikiPage savedPage = wikiRepository.save(originalWikiPage);

                // Add this to log
                AccountLog log = new AccountLog("Wiki page changed", savedPage.getTitle() + " is saved.", savedPage.getAuthor());
                accountLogService.save(log);

                return savedPage;
            }else{
                // Id is present, but not valid
                throw new NoSuchElementException("ID is invalid");
            }
        }else{
            // Wiki Page does not exist, create a new one
            WikiPage newPage = new WikiPage(author);
            newPage.moderatorEditValues(request);
            WikiPage savedPage = wikiRepository.save(newPage);

            // Add this to log
            AccountLog log = new AccountLog("Wiki page created", savedPage.getTitle() + " is saved.", savedPage.getAuthor());
            accountLogService.save(log);

            return savedPage;
        }
    }

    /*
        Users
     */

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
    public User findUserById(Long id){
        return userRepository.getById(id);
    }
}
