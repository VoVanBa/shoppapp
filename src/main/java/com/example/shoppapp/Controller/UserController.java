package com.example.shoppapp.Controller;

import com.example.shoppapp.Models.User;
import com.example.shoppapp.Services.IUserService;
import com.example.shoppapp.dto.UpdateUserDTO;
import com.example.shoppapp.dto.UserDTO;
import com.example.shoppapp.dto.UserLoginDTO;
import com.example.shoppapp.responses.LoginReponse;
import com.example.shoppapp.Components.LocalLizationUtils;
import com.example.shoppapp.responses.RegisterReponse;
import com.example.shoppapp.responses.UserResponse;
import com.example.shoppapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalLizationUtils lizationUtils;
    @PostMapping("/register")
    //BindingResult trong Spring MVC là một đối tượng được sử dụng để kiểm tra và lưu trữ kết quả của quá
    // trình binding giữa dữ liệu được gửi từ form và đối tượng trong model của ứng dụng.
    public ResponseEntity<RegisterReponse>createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errorr = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        RegisterReponse.builder()
                                .message("Error")
                                .build()
                );
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body(
                        RegisterReponse.builder()
                                .message(lizationUtils.getLocalzedMessage(MessageKeys.PASS_WORD_NOT_MATCH))
                                .build()
                );
            }
            User user= userService.creatẹ(userDTO);
          //  return ResponseEntity.ok("register success");
            return ResponseEntity.ok(
                    RegisterReponse.builder()
                            .message(lizationUtils.getLocalzedMessage(MessageKeys.CREATE_USER_SUCCESSFULLY))
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    RegisterReponse.builder()
                            .message(lizationUtils.getLocalzedMessage(MessageKeys.CREATE_USER_FAILED))
                            .build()
            );
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginReponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        //kiểm tra thông tin đăng nhập và sinh token
        try {
            String token=userService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword(), userLoginDTO.getRoleId());
            return ResponseEntity.ok(LoginReponse.builder().message(lizationUtils.getLocalzedMessage(MessageKeys.LOGIN_SUCCESSFULLY)).token(token).build());
        }  catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginReponse.builder().message(lizationUtils.getLocalzedMessage(MessageKeys.LOGIN_FAILED,e.getMessage())).build()
            );
        }
        //trả về token trong response
    }

    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            // Ensure that the user making the request matches the user being updated
            if (!Objects.equals(user.getId(), userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updatedUserDTO);

            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
