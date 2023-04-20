package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Model.Temp.ConfirmationToken;
import com.costi.csw9.Repository.UserRepository;
import com.costi.csw9.Util.LogicTools;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final AccountNotificationService accountNotificationService;

    private final AccountLogService accountLogService;

    public User findByEmail(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new UsernameNotFoundException("User" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public User findById(Long id) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new UsernameNotFoundException("User" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<User> loadAll(){
        return userRepository.findAll();
    }

    public String signUpAdmin(User user){
        //Check if exists
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if(userExists){
            throw new IllegalStateException("username already taken");
        }else{
            //Encode Password
            String encodedPass = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPass);

            //Save User
            userRepository.save(user);

            //Add to log
            AccountLog log = new AccountLog("Account Created", "Admin was created and not yet activated", user);
            accountLogService.save(log);

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);
            confirmationTokenService.saveConfirmationToken(confirmationToken);

            return token;
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
                accountNotificationService.save(welcome, user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean enable(User user, boolean enable) {
        if(enable == false && user.getRole().equals(UserRole.OWNER)){
            //Cannot be locked out
            return false;
        }

        //Add to log
        AccountLog log = new AccountLog("Account enabled/disabled", "Account activation set to: " + enable, user);
        accountLogService.save(log);

        userRepository.enable(user.getId(), enable);
        return true;
    }

    public boolean lock(User user, boolean lock) {
        if(user.getRole().equals(UserRole.OWNER)){
            //Cannot be locked out
            return false;
        }

        //Add to log
        AccountLog log = new AccountLog("Account locked/unlocked", "Account lock set to: " + lock, user);
        accountLogService.save(log);

        userRepository.lock(user.getId(), lock);
        return true;
    }

    public void updateUser(User user, User requester) throws Exception {
        if(requester.isAdmin() || requester.isOwner() || requester.getId().equals(user.getId())){
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
        }else{
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
    }

    public void demoteUser(User user, User requester) throws Exception{
        if(requester.isAdmin() || requester.isOwner()){
            AccountLog log = new AccountLog("Account demoted", "Account is now regular user", user);
            accountLogService.save(log);

            userRepository.demote(user.getId());
        }else{
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
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
