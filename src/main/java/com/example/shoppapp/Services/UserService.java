package com.example.shoppapp.Services;

import com.example.shoppapp.Components.JwTokenUtils;
import com.example.shoppapp.Models.Role;
import com.example.shoppapp.Models.User;
import com.example.shoppapp.Reponsitories.RoleReponsitory;
import com.example.shoppapp.Reponsitories.UserReponsitory;
import com.example.shoppapp.dto.UpdateUserDTO;
import com.example.shoppapp.dto.UserDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import com.example.shoppapp.exception.PermissionDenyException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserReponsitory userReponsitory;

    @Autowired
    private RoleReponsitory roleReponsitory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwTokenUtils jwTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public User creatẹ(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        //kiểm tra sdt tồn tại hay chưa
        if (userReponsitory.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("phone number aready exists");
        }
        //convert userDto =>user
        Role role = roleReponsitory.findById(userDTO.getRodeId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if (role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("you can not register admin account");
        }
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);
        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
        }
        return userReponsitory.save(newUser);
    }

    @Override
    public String login(String phoneNumber,
                        String password,
                        Long roleId) throws Exception {
        Optional<User> optionalUser = userReponsitory.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy pass");
        }
        //return optionalUser.get();//muốn trả JWT token ?
        User existingUser = optionalUser.get();
        //check password
        if (existingUser.getFacebookAccountId() == 0
                && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Sai mật khẩu");
            }
        }
        Optional<Role> optionalRole = roleReponsitory.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException("Không tìm thấy Roles");
        }
//        if(!optionalUser.get().isActive()) {
//            throw new DataNotFoundException("Tài khoản đã khóa");
//        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwTokenUtil.generationToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        //Kiểm tra token đã hết hạn hay chưa
        if(jwTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        //Trích xuất số điện thoại từ token:
        String phoneNumber = jwTokenUtil.extractPhoneNumber(token);
        //Tìm kiếm người dùng trong cơ sở dữ liệu:
        Optional<User> user = userReponsitory.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userReponsitory.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
                userReponsitory.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Update user information based on the DTO
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
        if (updatedUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
        }
        if (updatedUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userReponsitory.save(existingUser);
    }

}
