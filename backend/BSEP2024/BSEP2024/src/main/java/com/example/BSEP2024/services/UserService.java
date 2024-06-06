package com.example.BSEP2024.services;

import com.example.BSEP2024.Dto.CredentialDto;
import com.example.BSEP2024.models.User;
import com.example.BSEP2024.repositories.UserRepository;
import com.example.BSEP2024.utils.DecryptPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user=userRepository.findById(id);
        if(user.isPresent())return user.get();
        return null;
    }

    public User loginUser(CredentialDto credential){
        List<User> users = getAllUsers();
        for(User user : users){
            if(user.getUsername().equals(credential.getUsername())){
                if(user.getPassword().equals(credential.getPassword())){
                    return user;
                }
            }
        }
        return null;

    }
    public User getUserByUsernameAndPassword(String username,String password) throws Exception {
        Optional<User> us = userRepository.findByUsername(username);
        User u = new User();
        if(us.isPresent()) {
            u = us.get();
        }
        String pass = DecryptPassword.encrypt(password, u.getId()+u.getEmail()+u.getUsername()+u.getCity()+u.getCountry()+u.getOrganization());
        Optional<User> user=userRepository.findByUsernameAndPassword(username,pass);
        if(user.isPresent())return user.get();
        return null;
    }
    public User updateUser(User userData){
        return userRepository.save(userData);
    }

    public User getByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) return user.get();
        return null;
    }

    public User findByPublicKey(String publicKey){
        return userRepository.findByPublicKey(publicKey);
    }
}
