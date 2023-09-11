package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.DTO.UserAccountRequest;
import com.costi.csw9.Repository.AccountNotificationRepository;
import com.costi.csw9.Repository.UserRepository;
import com.costi.csw9.Util.LogicTools;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountLogService accountLogService;
    private final AccountNotificationRepository accountNotificationRepository;

    public User findByEmail(String email) throws UsernameNotFoundException {
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

    public void signUpUser(User user){
        //Check if exists
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if(userExists){
            throw new IllegalStateException("username already taken");
        }else{
            //Encode Password
            String encodedPass = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPass);

            //Enable user
            user.setEnabled(true);

            //Save User
            userRepository.save(user);

            //Add to log
            AccountLog log = new AccountLog("Account Created", "User was created and activated", user);
            accountLogService.save(log);

            //Add welcome message
            AccountNotification welcome = new AccountNotification("Welcome!", "<p>Welcome to your Costi Network ID, here you will see various details regarding your account. Try changing your profile picture!</p>", "primary", user);
            try {
                accountNotificationRepository.save(welcome);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void legacySave(User user) throws Exception {
        if(user.getPassword().equals("")){
            //Reuse old password
            Optional<User> optionalOld = userRepository.findById(user.getId());
            if(optionalOld.isPresent()){
                User old = optionalOld.get();
                user.setPassword(old.getPassword());
            }else{
                throw new Exception("User" + LogicTools.NOT_FOUND_MESSAGE);
            }
        }else{
            //Encode Password
            String encodedPass = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPass);
        }

        //Add to log
        AccountLog log = new AccountLog("Account details updated", user.toString(), user);
        accountLogService.save(log);

        userRepository.save(user);
    }

    public boolean isEmpty(){
        return userRepository.findAll().isEmpty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new UsernameNotFoundException("User" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }
}
