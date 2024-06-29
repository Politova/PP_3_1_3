package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.*;

import java.util.List;


@Controller
@Secured("ROLE_ADMIN")
@GetMapping("/admin")
public class AdminController {
    @Autowired
    private UserServiceImp userService;

    @Autowired
    public AdminController(UserServiceImp userService) {
        this.userService = userService;
    }

    @Autowired
    private RoleService roleService;

    @GetMapping()
    public String displayAllUsers(Model model) {
        model.addAttribute("userList", userService.getAllUsers());
        return "admin";
    }
    @GetMapping("/addUser")
    public String displayNewUserForm(Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("headerMessage", "Добавить пользователя");
        model.addAttribute("user", new User());
        return "addUser";
    }


    @PostMapping("/editUser")
    public String updateUsers(@ModelAttribute("user") User user, @RequestParam(value = "nameRoles", required = false) String[] roles) {
        if(user.getId() != null) {
            userService.getUserAndRoles(user, roles);
            userService.saveUser(user);
            return "redirect:/admin";
        } else {
            return "error";
        }
    }

    @GetMapping("/editUser")
    public String displayEditUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("headerMessage", "Изменить пользователя");
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/addUser")

    public String addUser (@ModelAttribute("user") User user,
                           @RequestParam(value = "rolesList") String [] roles,
                           @ModelAttribute("pass") String pass) {

        userService.saveUser(user);
        return "redirect:/admin";
    }

    String create(@ModelAttribute("user") User user, @RequestParam(name = "roles", required = false) List<Long> roleId) {
        userService.getNotNullRole(user);
        userService.saveUser(user);
        return "redirect:/admin";
    }


    @GetMapping("/deleteUser")
    public String deleteUserById(@RequestParam("id") Long id) {
        if(userService.getUserById(id) != null) {
            userService.deleteUser(id);
        }
        return "redirect:/admin";
    }


    @PostMapping()
    public String  deleteUser(@RequestParam(required = true, defaultValue = "" ) Long userId,
                              @RequestParam(required = true, defaultValue = "" ) String action,
                              Model model) {
        if (userId != null) {
            if (userService.getUserById(userId) != null) {
                if (action.equals("delete")){
                    userService.deleteUser(userId);
                }
            } else {
                model.addAttribute("error", "User not found");
            }
        } else {
            model.addAttribute("error", "Invalid user ID");
        }
        return "redirect:/admin";
    }

}