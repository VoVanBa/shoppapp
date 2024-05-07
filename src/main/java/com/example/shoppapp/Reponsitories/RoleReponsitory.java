package com.example.shoppapp.Reponsitories;

import com.example.shoppapp.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleReponsitory extends JpaRepository<Role,Long> {

}
