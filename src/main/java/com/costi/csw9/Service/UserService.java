package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.DTO.UserAccountRequest;
import com.costi.csw9.Repository.AccountLogRepository;
import com.costi.csw9.Repository.AccountNotificationRepository;
import com.costi.csw9.Repository.UserRepository;
import com.costi.csw9.Repository.WikiRepository;
import com.costi.csw9.Util.LogicTools;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountLogService accountLogService;
    private final AccountNotificationRepository accountNotificationRepository;
    private final WikiRepository wikiRepository;
    private final AccountLogRepository accountLogRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AccountLogService accountLogService, AccountNotificationRepository accountNotificationRepository, WikiRepository wikiRepository, AccountLogRepository accountLogRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountLogService = accountLogService;
        this.accountNotificationRepository = accountNotificationRepository;
        this.wikiRepository = wikiRepository;
        this.accountLogRepository = accountLogRepository;
    }

    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new UsernameNotFoundException("User" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public String save(UserAccountRequest request){
        if(request.getId() == null){
            // New user account
            User newUser = new User();
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setEnabled(true);
            newUser.setIsLocked(false);
            newUser.setEmail(request.getEmail());
            newUser.setProfilePicture(request.getProfilePicture());

            if(request.getPassword() != null && !request.getPassword().isBlank()){
                String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
                newUser.setPassword(encodedPass);
            }else{
                throw new IllegalArgumentException("Password field cannot be blank for creating new users");
            }

            if(userRepository.findAll().size() == 0){
                // No users present. Genesis user will now be upgraded to owner role
                newUser.setRole(UserRole.OWNER);
            }else{
                // Admin accounts are unavailable in this method and must be done through COMT
                newUser.setRole(UserRole.USER);
            }

            User savedUser = userRepository.save(newUser);

            //Add to log
            AccountLog log = new AccountLog("Account Created", "User was created and activated", savedUser);
            accountLogService.save(log);

            //Add welcome message
            AccountNotification welcome = new AccountNotification("Welcome!", "<p>Welcome to your Costi Network ID!</p>", "primary", savedUser);
            accountNotificationRepository.save(welcome);

            return "New " + newUser.getRole().name() + " created";
        }else{
            // Edit existing user account
            Optional<User> optionalUser = userRepository.findById(request.getId());
            if(optionalUser.isPresent()){
                // Transfer values to the present user
                User presentUser = optionalUser.get();
                presentUser.setFirstName(request.getFirstName());
                presentUser.setLastName(request.getLastName());
                presentUser.setEmail(request.getEmail());
                presentUser.setProfilePicture(request.getProfilePicture());

                if(request.getPassword() != null && !request.getPassword().isBlank()){
                    String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
                    presentUser.setPassword(encodedPass);
                }

                User savedUser = userRepository.save(presentUser);

                //Add to log
                AccountLog log = new AccountLog("Account details updated", presentUser.toString(), savedUser);
                accountLogService.save(log);

                return "Account was edited successfully";
            }else{
                throw new IllegalArgumentException("There are no users in Costi Online with the given id");
            }
        }

    }

    @Transactional
    public void delete(Long id){
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
}
