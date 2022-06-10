package com.costi.csw9.Service;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.ConfirmationToken;
import com.costi.csw9.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MESSAGE = "Oh RATS! %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));
    }

    public User loadUserObjectByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));
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
        }
    }

    public void enableUser(Long id) {
        userRepository.enableUser(id);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }
}
