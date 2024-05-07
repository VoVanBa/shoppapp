package com.example.shoppapp.Reponsitories;

import com.example.shoppapp.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface CategoryReponsitory extends JpaRepository<Category,Long> {
}
