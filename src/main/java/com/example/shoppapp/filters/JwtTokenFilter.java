package com.example.shoppapp.filters;

import com.example.shoppapp.Components.JwTokenUtils;
import com.example.shoppapp.Models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwTokenUtils jwTokenUtil;

    @Autowired
    public JwtTokenFilter(UserDetailsService userDetailsService, JwTokenUtils jwTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwTokenUtil = jwTokenUtil;
    }
// Phương thức này được gọi một lần cho mỗi yêu cầu HTTP2.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            if (isbyPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authenHeader = request.getHeader("Authorization");
            if (authenHeader == null || !authenHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            final String token = authenHeader.substring(7);
            final String phoneNumber = jwTokenUtil.extractPhoneNumber(token);

            //SecurityContextHolder: Là một lớp trong Spring Security giữ thông tin về bảo mật cho một luồng thực thi.
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwTokenUtil.validateToken(token, userDetails)) {
                    //Dòng mã này tạo một đối tượng UsernamePasswordAuthenticationToken, một loại đối tượng Authentication, được sử dụng trong Spring Security để đại diện cho quá trình xác thực bằng tên người dùng và mật khẩu.
                    //userDetails: Đây là một đối tượng UserDetails, thường là một đối tượng được cung cấp bởi Spring Security chứa thông tin về người dùng, bao gồm tên đăng nhập, mật khẩu (thường đã được mã hóa), và danh sách các quyền (authorities) mà người dùng có.
                    //null: Tham số thứ hai là mật khẩu, nhưng ở đây đã được đặt là null. Điều này thường xảy ra khi mật khẩu không được sử dụng trong quá trình xác thực, chẳng hạn khi người dùng đã đăng nhập bằng một phương thức khác như mã token hoặc chứng chỉ.
                    //userDetails.getAuthorities(): Tham số thứ ba là danh sách các quyền (authorities) mà người dùng có. Thông thường, các quyền này sẽ được trích xuất từ đối tượng UserDetails và đại diện cho các quyền mà người dùng đã được cấp phép trong hệ thống.
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    //Dòng mã này được sử dụng để cập nhật thông tin về quá trình xác thực của người dùng trong Spring Security.
                    //set authentication token
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            //cho đi qua het
            filterChain.doFilter(request, response); //enable by pass
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }


    }

    private boolean isbyPassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> byPassTokens = Arrays.asList(
                Pair.of("/roles", "GET"),
                Pair.of("/category", "GET"),
                Pair.of("/products", "GET"),
             //   Pair.of("/orders", "GET"),
//                Pair.of("/products", "POST"),
                Pair.of("/users/login", "POST"),
                Pair.of("/users/register", "POST")
        );
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        if (requestPath.equals("/orders")
                && requestMethod.equals("GET")) {
            // Allow access to %s/orders
            return true;
        }

        for (Pair<String, String> bypassToken : byPassTokens) {
            if (requestPath.contains(bypassToken.getFirst())
                    && requestMethod.equals(bypassToken.getSecond())) {
                return true;
            }
        }

        return false;
    }



}
