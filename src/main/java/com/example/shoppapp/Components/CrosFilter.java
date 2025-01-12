//package com.example.shoppapp.Components;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CrosFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        response.setHeader("Access-Control-Allow-Origin","*");
//        response.setHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE,OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers","authorization");
//        response.setHeader("Access-Control-Max-Age","3600");
//        response.setHeader("Access-Control-Expose-Header","xsrf-token");
//
//        if("OPTIONS".equals(request.getMethod())){
//            response.setStatus(HttpServletResponse.SC_OK);
//        }else{
//            filterChain.doFilter(request,response);
//        }
//
//
//    }
//}
