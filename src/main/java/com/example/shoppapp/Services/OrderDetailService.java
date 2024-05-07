package com.example.shoppapp.Services;

import com.example.shoppapp.Models.Order;
import com.example.shoppapp.Models.OrderDetail;
import com.example.shoppapp.Models.Product;
import com.example.shoppapp.Reponsitories.OrderDetailReponsitory;
import com.example.shoppapp.Reponsitories.OrderReponsitory;
import com.example.shoppapp.Reponsitories.ProductReponsitory;
import com.example.shoppapp.dto.OrderDetailDTO;
import com.example.shoppapp.exception.DataNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService implements IOrderDetailService{

    private  OrderService orderService;
    private OrderReponsitory orderReponsitory;
    private OrderDetailReponsitory orderDetailReponsitory;

    private ProductReponsitory productReponsitory;
    @Autowired
    public OrderDetailService(OrderService orderService, OrderReponsitory orderReponsitory, OrderDetailReponsitory orderDetailReponsitory, ProductReponsitory productReponsitory) {
        this.orderService = orderService;
        this.orderReponsitory = orderReponsitory;
        this.orderDetailReponsitory = orderDetailReponsitory;
        this.productReponsitory = productReponsitory;
    }

    @Override
    public OrderDetail createOder(OrderDetailDTO orderDetailDTO) throws Exception {
        Order order=orderReponsitory.findById(orderDetailDTO.getOrderId()).orElseThrow(()->
                new DataNotFoundException("cannot find with id "+(orderDetailDTO.getOrderId())));

        Product product=productReponsitory.findById(orderDetailDTO.getProductId()).orElseThrow(()->
                new DataNotFoundException("cannot find with id "+(orderDetailDTO.getOrderId())));

        OrderDetail orderDetail= OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProduct(orderDetailDTO.getNumberOfProduct())
                .price(orderDetailDTO.getPrice())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();

        //lưu vaof db
        return orderDetailReponsitory.save(orderDetail);
    }

    @Override
    public OrderDetail getOder(Long id) throws DataNotFoundException {
        return orderDetailReponsitory.findById(id).orElseThrow(()->new DataNotFoundException("cannot find orderdetail with id"+id));
    }

    @Override
    @Transactional
    public OrderDetail update(OrderDetailDTO orderDetailDTO, Long id) throws DataNotFoundException {
         OrderDetail existingOrderDtail = orderDetailReponsitory.findById(id).orElseThrow(()->
                new DataNotFoundException("cannot find orderdetail with id"+id));

        Order existingOrder = orderReponsitory.findById(orderDetailDTO.getOrderId()).orElseThrow(()->
                new DataNotFoundException("cannot find order with id"+id));
        Product existingProduct = productReponsitory.findById(orderDetailDTO.getProductId()).orElseThrow(()->
                new DataNotFoundException("cannot find product with id"+id));
        existingOrderDtail.setPrice(orderDetailDTO.getPrice());
        existingOrderDtail.setColor(orderDetailDTO.getColor());
        existingOrderDtail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDtail.setNumberOfProduct(orderDetailDTO.getNumberOfProduct());
        existingOrderDtail.setOrder(existingOrder);
        existingOrderDtail.setProduct(existingProduct);
        return orderDetailReponsitory.save(existingOrderDtail);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderDetailReponsitory.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByUserId(Long userId) {
        return orderDetailReponsitory.findByOrderId(userId);
    }


}
