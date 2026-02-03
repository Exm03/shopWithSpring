package com.example.shop.controllers;

import com.example.shop.dto.UserUpdateDto;
import com.example.shop.models.Role;
import com.example.shop.models.User;
import com.example.shop.repo.UserRepository;
import com.example.shop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/user")
    public String user(Principal principal, Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user";
    }

    @PostMapping("/user")
    public String updateUser(Principal principal,
                             @ModelAttribute UserUpdateDto dto) {
        User user = userService.getCurrentUser(principal);
        userService.updateUser(user, dto);

        return "redirect:/user";
    }

    @GetMapping("/reg")
    public String reg(@RequestParam(name = "error", defaultValue = "", required = false) String error,
                      Model model) {
        if (error.equals("username")) {
            model.addAttribute("error", "Username is already taken");
        }
        return "reg";
    }

    @PostMapping("/reg")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password) {
        if(userRepository.findByUsername(username) != null) {
            return "redirect:/reg?error=username";
        }

        password = passwordEncoder.encode(password);
        User user = new User(username, password, email, true, Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }
}
