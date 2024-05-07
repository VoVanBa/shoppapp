package com.example.shoppapp.Controller;

import com.example.shoppapp.Components.LocalLizationUtils;
import com.example.shoppapp.Models.OrderDetail;
import com.example.shoppapp.Services.IOrderService;
import com.example.shoppapp.Services.OrderDetailService;
import com.example.shoppapp.dto.OrderDTO;
import com.example.shoppapp.dto.OrderDetailDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import com.example.shoppapp.responses.OrderDetailReponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order_detail")
public class OrderDetailController {


    private final OrderDetailService orderDetailService;
    private final LocalLizationUtils lizationUtils;
    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService, LocalLizationUtils lizationUtils) {
        this.orderDetailService = orderDetailService;
        this.lizationUtils = lizationUtils;
    }

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO ){
        try {
            OrderDetail newOrderDetail = orderDetailService.createOder(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailReponse.fromOrderDetail(newOrderDetail));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
     @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("id") Long user_id){
        try {
            OrderDetail orderDetail= orderDetailService.getOder(user_id);
            return ResponseEntity.ok().body(OrderDetailReponse.fromOrderDetail(orderDetail));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //lấy danh sách order detail từ một order nào đó
    @GetMapping("/order/{oder_id}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("oder_id") Long oder_id){
        try {
            List<OrderDetail> orderDetails=orderDetailService.findByUserId(oder_id);
            List<OrderDetailReponse > orderDetailReponses =orderDetails.stream().map(OrderDetailReponse::fromOrderDetail).toList();
            return ResponseEntity.ok().body(orderDetailReponses);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id,
                                         @Valid @RequestBody OrderDetailDTO orderDetailDTO
    )  {
        try {
           OrderDetail orderDetail= orderDetailService.update(orderDetailDTO,id);
            return ResponseEntity.ok().body(orderDetail);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable("id") Long id){
        orderDetailService.deleteOrder(id);
        return ResponseEntity.ok().body("xóa thông tin order");
    }
}
