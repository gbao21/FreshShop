package com.freshshop.controller.UserController;

import com.freshshop.constant.FreshShopConstants;
import com.freshshop.implementation.CartImp;
import com.freshshop.model.Customer;
import com.freshshop.model.OrderDetails;
import com.freshshop.model.Orders;
import com.freshshop.model.Products;
import com.freshshop.repository.OrderDetailRepository;
import com.freshshop.repository.OrderRepository;
import com.freshshop.repository.ProductRepository;
import com.freshshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class CheckOutController {

    @Autowired
    private CartImp cartImp;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;

    private final MessageSource messageSource;
    Locale currentLocale = LocaleContextHolder.getLocale();

    public CheckOutController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/checkout")
    public String displayCheckOut(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggingCustomer");
        if (customer != null) {
            model.addAttribute("customer", customer);
            List<String> addressOptions = null;
            if (customer.getAddress() != null && customer.getAddress().getAddressId() >= 0) {
                addressOptions = Arrays.asList(customer.getAddress().getAddress1(), customer.getAddress().getAddress2());
            }
            model.addAttribute("addressOptions", addressOptions);

            Collection<Products> productList = cartImp.getProducts();
            if (productList.isEmpty()) {
                String localizedMessage = messageSource.getMessage("cart.noItems", null, currentLocale);
                model.addAttribute("message", localizedMessage);
                return "User/cart.html";
            }
            model.addAttribute("cartProducts", cartImp.getProducts());
            model.addAttribute("count", cartImp.getCount());
            model.addAttribute("subtotalAmount", cartImp.getAmount());
            model.addAttribute("discount", cartImp.getAmount() * 0.1);
            double total = cartImp.getAmount() - cartImp.getAmount() * 0.1;
            model.addAttribute("total", total);
        }
        return "User/checkout.html";
    }

    @GetMapping("/placeOrder")
    public String placeOrder(Model model,
                            HttpSession session, RedirectAttributes ra) {
        Customer customer = (Customer) session.getAttribute("loggingCustomer");
        Collection<Products> listProduct = cartImp.getProducts();
        if (customer != null && customer.getAddress()!=null) {
            Orders order = new Orders();
            order.setCustomer(customer);
            order.setTotalAmount(cartImp.getAmount()-cartImp.getAmount()*0.1);
            orderService.saveOrder(order);
            if (order.getOrderId() > 0) {
                for (Products product:listProduct){
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setOrder(order);
                    orderDetails.setProduct(product);
                    orderDetails.setQuantity(product.getQuantity());
                    orderDetails.setAmount(product.getQuantity()*product.getPrice());
                    orderDetailRepository.save(orderDetails);
                }
                cartImp.clear();
                session.setAttribute("itemInCart", cartImp.getSize());
                ra.addFlashAttribute("message", "Wait for admin to accept");
                return "redirect:/viewCart";
            }
            ra.addFlashAttribute("message", "Checkout Failed");
            return "redirect:/checkout";
        } else {
            ra.addFlashAttribute("message", "Update Your Address to shipping");
            return "redirect:/profile";
        }
    }
    @GetMapping("/order/completed")
    public String orderCompleted(Model model, HttpSession session,
                                 @RequestParam("currentPage") Optional<Integer>currentPage,
                                 Optional<String> error, Optional<String> success) {
        Customer customer = (Customer) session.getAttribute("loggingCustomer");
        Sort sort  = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(currentPage.orElse(0), 4, sort);
        Page<Orders> page = orderRepository.findAllOrderIsClose( customer.getCustomerId(), FreshShopConstants.CLOSE, pageable);

        System.out.println(page.getContent().toString());
        List<Orders> ordersList = page.getContent();
        model.addAttribute("ordersList", ordersList);
        model.addAttribute("page", page);
        model.addAttribute("currentPage",  currentPage.orElse(0));

        return "User/order-completed.html";
    }

    @GetMapping("/orderDetail/completed/{orderId}")
    public String orderDetailCompleted(Model model, HttpSession session, @PathVariable("orderId")int orderId) {
        Customer customer = (Customer) session.getAttribute("loggingCustomer");
        List<OrderDetails> listOrderDetailDetail = orderDetailRepository.findOrderDetailsByCustomerId(customer.getCustomerId(),orderId);
        model.addAttribute("listOrderDetailCompleted", listOrderDetailDetail);

        return "User/order-detail-completed.html";
    }

}
