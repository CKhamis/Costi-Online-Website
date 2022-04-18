package com.costi.csw9.Service;

import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public void addNewUser(User user) {
        Optional<User> match = userRepository.findUserByEmail(user.getEmail());

        if(match.isPresent()){
            throw new IllegalStateException("email taken");
        }else{
            userRepository.save(user);
        }
    }

    public void deleteUser(Long userId) {
        if(userRepository.existsById(userId)){
            userRepository.deleteById(userId);
        }else{
            throw new IllegalStateException("User with " + userId + " does not exist");
        }

    }

    @Transactional
    public void modifyUser(Long userId, String name, String email) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with " + userId + " does not exist"));

        if(name != null && name.length() > 0 && !Objects.equals(user.getName(), name)){
            user.setName(name);
        }

        if(email != null && email.length() > 0 && !Objects.equals(user.getEmail(), email)){
            Optional<User> match = userRepository.findUserByEmail(email);

            if(match.isPresent()){
                throw new IllegalStateException("email taken");
            }else{
                user.setEmail(email);
            }
        }
    }
}
