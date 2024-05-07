package com.example.shoppapp.Controller;

import com.example.shoppapp.Models.Role;
import com.example.shoppapp.Services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllRole(){
        List<Role> roles=roleService.getAllRole();
        return ResponseEntity.ok(roles);
    }
}
