package com.costi.csw9.Service;

import com.costi.csw9.Model.*;
import com.costi.csw9.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MESSAGE = "Oh RATS! %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final AccountNotificationService accountNotificationService;

    private final AccountLogService accountLogService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));
    }

    public UserDetails findById(Long id) {
        return userRepository.findById(id);
    }

    public User loadUserObjectByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));
    }

    public User loadUserObjectById(Long id) throws UsernameNotFoundException {
        return userRepository.findById(id);
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

            // TODO: delete
            AccountNotification noti = new AccountNotification("rat", "rat", "primary", user);
            accountNotificationService.save(noti);
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

    public void updateUser(User user){
        System.out.println(user.getPassword());
        if(user.getPassword().equals("")){
            //Reuse old password
            User old = userRepository.findById(user.getId());
            user.setPassword(old.getPassword());
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

    public boolean demoteUser(User user){
        if(!user.getRole().equals(UserRole.ADMIN)){
            //User is already not an admin
            return false;
        }else{
            //Add to log
            AccountLog log = new AccountLog("Account demoted", "Account is now regular user", user);
            accountLogService.save(log);

            userRepository.demote(user);
            return true;
        }
    }

    public boolean isEmpty(){
        return userRepository.findAll().isEmpty();
    }
}
