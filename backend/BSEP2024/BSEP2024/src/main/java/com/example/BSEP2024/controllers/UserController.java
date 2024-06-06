package com.example.BSEP2024.controllers;

import com.example.BSEP2024.Dto.CredentialDto;
import com.example.BSEP2024.models.User;
import com.example.BSEP2024.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<User> getAll(){return userService.getAllUsers();}

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){return userService.getUserById(id);}


    @GetMapping("/login2")
    public User loginUser(@RequestBody CredentialDto credential){
        return userService.loginUser(credential);
    }

    @GetMapping("login/{username}/{password}")
    public User getUserByUsernameAndPassword(@PathVariable String username,@PathVariable String password) throws Exception {return userService.getUserByUsernameAndPassword(username,password);}

}
