package com.costi.csw9.Service;

import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MESSAGE = "Oh RATS! %s not found";
    UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));
    }

    public String signUpUser(User user){
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

            //TODO: send confirmation token

            return "done.";
        }
    }
}
