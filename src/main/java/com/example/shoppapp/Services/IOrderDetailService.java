package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Order;
import com.example.shoppapp.Models.OrderDetail;
import com.example.shoppapp.dto.OrderDTO;
import com.example.shoppapp.dto.OrderDetailDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOder(OrderDetailDTO orderDetailDTO) throws Exception;

    OrderDetail getOder(Long id) throws DataNotFoundException;

    OrderDetail update(OrderDetailDTO orderDetailDTO,Long id) throws DataNotFoundException;

    void deleteOrder(Long id) ;

    List<OrderDetail> findByUserId(Long userId);


}
