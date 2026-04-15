package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.*;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.ShipmentRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class UserOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @GetMapping
    public String myOrdersPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser.get());

        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(userId);

        for (Order order : orders) {
            Hibernate.initialize(order.getItems());
            Hibernate.initialize(order.getCustomer());
            if (order.getPromoCode() != null) {
                Hibernate.initialize(order.getPromoCode());
            }
            if (order.getAddress() != null) {
                Hibernate.initialize(order.getAddress());
            }
            Optional<Shipment> shipment = shipmentRepository.findByOrderId(order.getId());
            shipment.ifPresent(s -> order.setShipment(s));
        }

        model.addAttribute("orders", orders);

        return "my-orders";
    }

    @GetMapping("/{id}")
    public String orderDetailPage(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/orders";
        }

        Order order = orderOpt.get();

        if (!order.getCustomer().getId().equals(userId)) {
            return "redirect:/orders";
        }

        Hibernate.initialize(order.getItems());
        Hibernate.initialize(order.getCustomer());
        if (order.getPromoCode() != null) {
            Hibernate.initialize(order.getPromoCode());
        }
        if (order.getAddress() != null) {
            Hibernate.initialize(order.getAddress());
        }
        Optional<Shipment> shipment = shipmentRepository.findByOrderId(order.getId());
        shipment.ifPresent(s -> order.setShipment(s));

        model.addAttribute("currentUser", currentUser.get());
        model.addAttribute("order", order);

        return "order-detail";
    }
}
