package com.freshshop.controller.AdminController;

import com.freshshop.constant.FreshShopConstants;
import com.freshshop.model.OrderDetails;
import com.freshshop.model.Orders;
import com.freshshop.repository.ContactUsRepository;
import com.freshshop.repository.OrderRepository;
import com.freshshop.repository.ProductRepository;
import com.freshshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminOrderAndOrderDetailsController {
    @Autowired
    ContactUsService contactUsService;

    @Autowired
    ContactUsRepository contactUsRepository;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CustomerService customerService;
    @GetMapping("/order/{currentPage}")
    public String displayOrder(Model model, @PathVariable("currentPage") int currentPage, @RequestParam("status") String status,
                               @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        Page<Orders> orderPage = orderService.getAllOrder(status, currentPage, sortField, sortDir);
        List<Orders> listOrder = orderPage.getContent();
        List<Orders> listOrderStatus = orderRepository.findAllByStatus(FreshShopConstants.CLOSE);
        double profit =0;
        for (Orders order: listOrderStatus) {
            profit += order.getTotalAmount();
        }
//        model.addAttribute("myLocale", Locale.US);
        model.addAttribute("profit", profit);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentStatus", status);
        model.addAttribute("status", status.equals(FreshShopConstants.OPEN) ? FreshShopConstants.CLOSE : FreshShopConstants.OPEN);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("sortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listOrder", listOrder);

        return "Admin/admin-Order.html";
    }

    @RequestMapping(value = "/closeOrder/{currentPage}", method = RequestMethod.GET)
    public String closeOrder(@RequestParam("orderId") int orderId, @PathVariable("currentPage") int currentPage,
                             @RequestParam("sortField") String sortField,
                             @RequestParam("sortDir") String sortDir,
                             @RequestParam("status") String status, RedirectAttributes ra) {
        orderService.updateOrderStatus(FreshShopConstants.CLOSE, orderId);
        ra.addFlashAttribute("message","Close Successfully");
        return "redirect:/admin/order/" + currentPage + "?sortField=" + sortField + "&sortDir=" + sortDir + "&status=" + status;


    }

    @GetMapping("/orderDetail/{currentPage}")
    public String displayOrderDetail(Model model, @PathVariable("currentPage") int currentPage,
                                     @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        Page<OrderDetails> orderDetailPage = orderDetailService.getAllOrderDetail(currentPage, sortField, sortDir);
        List<OrderDetails> listOrderDetail = orderDetailPage.getContent();
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("totalPages", orderDetailPage.getTotalPages());
        model.addAttribute("sortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listOrderDetail", listOrderDetail);

        return "Admin/admin-OrderDetails.html";
    }
}
