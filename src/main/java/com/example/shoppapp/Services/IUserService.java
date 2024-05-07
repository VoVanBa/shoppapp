package com.example.shoppapp.Services;

import com.example.shoppapp.Models.User;
import com.example.shoppapp.dto.UpdateUserDTO;
import com.example.shoppapp.dto.UserDTO;
import com.example.shoppapp.exception.DataNotFoundException;

public interface IUserService {
    User creatẹ(UserDTO userDTO) throws  Exception;
    String login(String Phonenumber,String password,Long role_id) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;


}
