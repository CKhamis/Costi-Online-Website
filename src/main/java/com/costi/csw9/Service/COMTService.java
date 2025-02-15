package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.DTO.*;
import com.costi.csw9.Repository.*;
import com.costi.csw9.Util.LogicTools;
import com.costi.csw9.Validation.FileValidator;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.rmi.ConnectIOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.costi.csw9.Util.LogicTools.POST_IMAGE_PATH;

@Service
@AllArgsConstructor
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
    private final AccountLogRepository accountLogRepository;
    private final AttachmentRepository attachmentRepository;
    private final DynamicContentRepository dynamicContentRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String POST_PREFIX = "/Downloads/Uploads/";

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

    public void deletePost(Long id) throws Exception {
        Optional<Post> newsroomPost = postRepository.findById(id);
        if(newsroomPost.isPresent()){
            String path = newsroomPost.get().getImagePath();
            if (path.startsWith(POST_PREFIX)) {
                // Uploaded image is present, delete it as well
                attachmentRepository.deleteById(path.substring(POST_PREFIX.length()));
            }
            postRepository.deleteById(id);
        } else {
            throw new Exception("Post" + LogicTools.NOT_FOUND_MESSAGE);
        }
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
            post.setViews(optionalPost.get().getViews());
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
                post.setViews(originalPost.getViews());
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
        // Check if post already exists
        if(post.getId() != null){
            // Post most likely exists. Check for an existing image
            Post savedPost = postRepository.getById(post.getId());
            post.setViews(savedPost.getViews());
            if(savedPost.getImagePath().contains("/Downloads")){
                // Post already contains an attachment
                String previousPath = savedPost.getImagePath();
                if (previousPath.startsWith(POST_PREFIX)) {
                    // Uploaded image is present, delete it before next is saved regardless of lock
                    attachmentRepository.deleteById(previousPath.substring(POST_PREFIX.length()));
                }
            }
        }

        // Save image and post
        Attachment attachment = attachmentService.saveAttachment(file, true);
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

    public List<AccountNotification> findAllNotificationsByUser(Long id){
        if(id == null){
            throw new NullPointerException("ID field cannot be null");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            return accountNotificationRepository.findByUser(optionalUser.get());
        }else{
            throw new IllegalArgumentException("There are no logs in Costi Online with the given id");
        }
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

    public List<WikiPage> findAllWikiPagesByUser(Long id){
        if(id == null){
            throw new NullPointerException("ID field cannot be null");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            return wikiRepository.findByAuthor(optionalUser.get());
        }else{
            throw new IllegalArgumentException("There are no logs in Costi Online with the given id");
        }
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
        Account Logs
     */

    public List<AccountLog> findAllAccountLogs(){return accountLogRepository.findAll();}

    public List<AccountLog> findAllAccountLogsByUser(Long id){
        if(id == null){
            throw new NullPointerException("ID field cannot be null");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            return accountLogRepository.findByUser(optionalUser.get());
        }else{
            throw new IllegalArgumentException("There are no logs in Costi Online with the given id");
        }
    }

    public void deleteAccountLog(Long id){
        accountLogRepository.deleteById(id);
    }

    public void saveAccountLog(ModeratorAccountLogRequest request){
        // Check if new log or editing log
        if(request.getId() != null){
            // Check if the id exists
            Optional<AccountLog> optionalAccountLog = accountLogRepository.findById(request.getId());
            if(optionalAccountLog.isPresent()){
                // Account log exists, edit it
                AccountLog log = optionalAccountLog.get();

                // Set assignee
                Optional<User> optionalUser = userRepository.findById(request.getUserId());
                if(optionalUser.isPresent()){
                    // User is present
                    User assignee = optionalUser.get();
                    log.setUser(assignee);

                    // Set fields
                    log.setTitle(request.getTitle());
                    log.setBody(request.getBody());

                    // Save
                    accountLogRepository.save(log);
                }else{
                    // Account log does not exist
                    throw new IllegalArgumentException("There are no logs in Costi Online with the given id");
                }

            }else{
                // Account log does not exist
                throw new IllegalArgumentException("There are no logs in Costi Online with the given id");
            }

        }else{
            // Create new log

            // Check if a user is assigned
            if(request.getUserId() == null){
                throw new IllegalArgumentException("User id field cannot be null");
            }

            Optional<User> optionalUser = userRepository.findById(request.getUserId());
            if(optionalUser.isPresent()){
                // ALl fields present
                AccountLog log = new AccountLog(request.getTitle(), request.getBody(), optionalUser.get());
                accountLogRepository.save(log);
            }else{
                // User is incorrect
                throw new IllegalArgumentException("There are no users in Costi Online with the given id");
            }
        }
    }

    /*
        Users
     */

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id){
        // Check if the id is valid
        if(id != null){
            // Check if the id exists
            Optional<User> optionalUser = userRepository.findById(id);
            if(optionalUser.isPresent()){
                // User exists
                User user = optionalUser.get();

                if(user.isOwner()){
                    throw new AccessDeniedException("Owner user cannot be deleted");
                }

                // Check if there are any wiki pages that are owned by account
                List<WikiPage> wikiPages = wikiRepository.findByAuthor_Id(id);
                if(!wikiPages.isEmpty()){
                    // Rectify issue

                    // Re-assign them to owner
                    // Find owner
                    User costi = userRepository.findFirstByRole(UserRole.OWNER);
                    for(WikiPage page : wikiPages){
                        // Go through each one and transfer ownership
                        page.setAuthor(costi);
                        page.setBody(page.getBody() + "<br /><br /><p>Owner of this wiki page was deleted, ownership was transferred to owner.</p>");
                        wikiRepository.save(page);
                    }
                }

                // Delete any logs that are owned by account
                accountLogRepository.deleteByUser(user);

                // Delete any notifications that are owned by account
                accountNotificationRepository.deleteByUser(user);

                // Ready to delete
                userRepository.deleteById(id);

                return;
            }
        }
        // ID is either null or doesn't have a user
        throw new IllegalArgumentException("There are no users in Costi Online with the given id");
    }

    public void saveUser(ModeratorAccountRequest request){
        // Check if the id is valid
        if(request.getId() != null){
            // Check if the id exists
            Optional<User> optionalUser = userRepository.findById(request.getId());
            if(optionalUser.isPresent()){
                // User exists
                User user = optionalUser.get();

                // Set values
                if(!user.isOwner()){
                    // Ignore field changes if user is the owner
                    if(!request.getRole().equals(UserRole.OWNER)){
                        user.setRole(request.getRole());
                    }
                    user.setEmail(request.getEmail());
                    user.setEnabled(request.isEnabled());
                    user.setIsLocked(request.isLocked());
                }

                if(request.getPassword() != null && !request.getPassword().isBlank()){
                    String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
                    user.setPassword(encodedPass);
                }
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setProfilePicture(request.getProfilePicture());

                // Create log
                AccountLog editLog = new AccountLog("Edited by Moderator", "A moderator has modified this account.", user);
                accountLogRepository.save(editLog);

                // Save user
                userRepository.save(user);
            }else{
                // User does not exist
                throw new IllegalArgumentException("There are no users in Costi Online with the given id");
            }
        }else{
            // Create new user
            User newUser = new User();

            // promote to owner if no other accounts exist
            if(userRepository.findAll().size() == 0){
                newUser.setRole(UserRole.OWNER);
            }else if(request.getRole().equals(UserRole.OWNER)){
                // An owner already exists, but the request is asking for another
                newUser.setRole(UserRole.USER);
            }else{
                // Either admin or user
                newUser.setRole(request.getRole());
            }

            // Assign values
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setDateCreated(LocalDateTime.now());
            newUser.setProfilePicture(request.getProfilePicture());
            newUser.setEnabled(request.isEnabled());
            newUser.setIsLocked(request.isLocked());
            newUser.setEmail(request.getEmail());

            String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
            newUser.setPassword(encodedPass);
            User savedUser = userRepository.save(newUser);

            // Add to log
            AccountLog log = new AccountLog("Account Created", "User was created and activated", savedUser);
            accountLogService.save(log);

            // Create welcoming message
            AccountNotification welcome = new AccountNotification("Welcome!", "<p>Welcome to your Costi Network ID!</p>", "success", savedUser);
            accountNotificationRepository.save(welcome);
        }
    }
    public User findUserById(Long id){
        return userRepository.getById(id);
    }

    /*
        File Uploads
     */

    public List<Attachment> findAllAttachments(){
        return attachmentRepository.findAllByOrderByCreatedDesc();
    }

    public void saveFile(MultipartFile file, boolean isLocked) throws Exception {
        attachmentService.saveAttachment(file, isLocked);
    }

    public void deleteAttachment(String id) throws Exception {
        // Check to see if the attachment exists
        Attachment file = attachmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new Exception("File not found with Id: " + id));

        // Check if file is locked
        if(file.isLocked()){
            throw new Exception("File is locked");
        }

        attachmentRepository.deleteById(id);
    }

    /*
        Dynamic Content
     */

    public DynamicContent findDynamicContentById(Long id) throws Exception {
        Optional<DynamicContent> optionalDynamicContent = dynamicContentRepository.findById(id);
        if(optionalDynamicContent.isPresent()){
            return optionalDynamicContent.get();
        }else{
            throw new NoSuchElementException("Dynamic Content" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public void saveDynamicContent(DynamicContent content){
        content.setLastEdited(LocalDateTime.now());
        dynamicContentRepository.save(content);
    }

    public List<DynamicContent> findAllDynamicContent(){
        return dynamicContentRepository.findAll();
    }

    public void deleteDynamicContent(Long id){
        Optional<DynamicContent> optionalDynamicContent = dynamicContentRepository.findById(id);
        if(optionalDynamicContent.isPresent()){
            dynamicContentRepository.deleteById(id);
        }else{
            throw new NoSuchElementException("Dynamic Content" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public void enableContentById(Long id){
        dynamicContentRepository.disableAll();
        dynamicContentRepository.enable(id);
    }
}
