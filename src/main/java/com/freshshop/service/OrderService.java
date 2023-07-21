package com.freshshop.service;

import com.freshshop.constant.FreshShopConstants;
import com.freshshop.model.Orders;
import com.freshshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    public boolean saveOrder(Orders order) {
        boolean isSaved = false;
        order.setStatus(FreshShopConstants.OPEN);
        order = orderRepository.save(order);
        if (order.getOrderId() >= 0) {
            isSaved = true;
        }
        return isSaved;
    }

    public Page<Orders> getAllOrder(String status,int pageNumber, String sortField, String sortDir) {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
        Page<Orders> pageOrder = orderRepository.getOderStatus(status,pageable);
        return pageOrder;
    }
    public void updateOrderStatus( String status,int orderId) {
        orderRepository.updateByStatus(status, orderId);
    }



}
