package com.freshshop.repository;

import com.freshshop.model.Orders;
import com.freshshop.model.ReportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Integer> {
    @Query("select o from Orders o where o.status=?1")
    Page<Orders> getOderStatus(String status,Pageable pageable);

    @Transactional
    @Modifying //Use this to tell the Spring that this method will modify the table in DB (delete/update/...)
    @Query("Update Orders o set o.status = ?1 where o.orderId = ?2")
        //Query with the class
    void updateByStatus(String status, int orderId);

    List<Orders> findAllByStatus(String status);


    @Query("select o from Orders o where o.customer.customerId =?1 and o.status =?2")
    Page<Orders> findAllOrderIsClose(int customerID , String status, Pageable pageable);

    @Query("SELECT new com.freshshop.model.ReportOrder(CONCAT(FUNCTION('YEAR', o.createdAt), '-', FUNCTION('MONTH', o.createdAt)), SUM(o.totalAmount)) " +
            "FROM Orders o " +
            "GROUP BY CONCAT(FUNCTION('YEAR', o.createdAt), '-', FUNCTION('MONTH', o.createdAt))")
    List<ReportOrder> getOrderReport();





}
