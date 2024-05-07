package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Role;
import com.example.shoppapp.Reponsitories.RoleReponsitory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService{
    @Autowired
    private  RoleReponsitory roleReponsitory;
    @Override
    public List<Role> getAllRole() {
        return roleReponsitory.findAll();
    }
}
