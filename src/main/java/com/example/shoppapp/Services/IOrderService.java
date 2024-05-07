package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Order;
import com.example.shoppapp.dto.OrderDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import com.example.shoppapp.responses.OrderReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderService {
    Order createOder(OrderDTO orderDTO) throws Exception;

    Order getOder(Long id);

    Order update(OrderDTO orderDTO,Long id) throws DataNotFoundException;

    void deleteOrder(Long id) ;

    List<Order> findByUserId(Long userId);

    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
