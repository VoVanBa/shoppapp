package com.example.shoppapp.Services;

import com.example.shoppapp.Models.*;
import com.example.shoppapp.Reponsitories.OrderDetailReponsitory;
import com.example.shoppapp.Reponsitories.OrderReponsitory;
import com.example.shoppapp.Reponsitories.ProductReponsitory;
import com.example.shoppapp.Reponsitories.UserReponsitory;
import com.example.shoppapp.dto.CartItemDto;
import com.example.shoppapp.dto.OrderDTO;
import com.example.shoppapp.dto.OrderDetailDTO;
import com.example.shoppapp.dto.OrderWithDetailsDTO;
import com.example.shoppapp.exception.DataNotFoundException;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService{
    private final UserReponsitory userReponsitory;
    private final OrderReponsitory orderReponsitory;
    private final ProductReponsitory productReponsitory;
    private final OrderDetailReponsitory orderDetailReponsitory;

    private final ModelMapper modelMapper;
    @Autowired
    public OrderService(UserReponsitory userReponsitory, OrderReponsitory orderReponsitory, ProductReponsitory productReponsitory, ModelMapper modelMapper, OrderDetailReponsitory orderDetailReponsitory) {
        this.userReponsitory = userReponsitory;
        this.orderReponsitory = orderReponsitory;
        this.productReponsitory = productReponsitory;
        this.orderDetailReponsitory = orderDetailReponsitory;
        this.modelMapper = modelMapper;
    }

    @Override
    public Order createOder(OrderDTO orderDTO) throws Exception {
        //tìm xem user'id có tồn tại ko
        User user = userReponsitory
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        //convert orderDTO => Order
        //dùng thư viện Model Mapper
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        //cấu hình
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());//lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        //Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);//đoạn này nên set sẵn trong sql
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderReponsitory.save(order);
        // Tạo danh sách các đối tượng OrderDetail từ cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDto cartItemDTO : orderDTO.getCartItems()) {
            // Tạo một đối tượng OrderDetail từ CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ cơ sở dữ liệu (hoặc sử dụng cache nếu cần)
            Product product = productReponsitory.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

            // Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProduct(quantity);
            // Các trường khác của OrderDetail nếu cần
            orderDetail.setPrice(product.getPrice());

            // Thêm OrderDetail vào danh sách
            orderDetails.add(orderDetail);
        }


        // Lưu danh sách OrderDetail vào cơ sở dữ liệu
        orderDetailReponsitory.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOder(Long id) {
        return orderReponsitory.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order update(OrderDTO orderDTO, Long id) throws DataNotFoundException {

//      Kiểm tra xem user có tồn tại không
        Order order = orderReponsitory.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find o rder with id: " + id));
        User existingUser = userReponsitory.findById(
                orderDTO.getUserId()).orElseThrow(() ->
                new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        return orderReponsitory.save(order);
    }

    @Transactional
    public Order updateOrderWithDetails(OrderWithDetailsDTO orderWithDetailsDTO) {
        modelMapper.typeMap(OrderWithDetailsDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderWithDetailsDTO, order);
        Order savedOrder = orderReponsitory.save(order);

        // Set the order for each order detail
        for (OrderDetailDTO orderDetailDTO : orderWithDetailsDTO.getOrderDetailDTOS()) {
            //orderDetail.setOrder(OrderDetail);
        }

        // Save or update the order details
        List<OrderDetail> savedOrderDetails = orderDetailReponsitory.saveAll(order.getOrderDetails());

        // Set the updated order details for the order
        savedOrder.setOrderDetails(savedOrderDetails);

        return savedOrder;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id)  {
    Order order =orderReponsitory.findById(id).orElse(null);
        //không xóa cứng
        if(order !=null ) {
            order.setActive(false);
            orderReponsitory.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderReponsitory.findByUserId(userId);
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderReponsitory.findByKeyword(keyword, pageable);
    }



}
