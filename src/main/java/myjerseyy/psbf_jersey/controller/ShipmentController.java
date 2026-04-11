package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Address;
import myjerseyy.psbf_jersey.entity.Order;
import myjerseyy.psbf_jersey.entity.OrderStatus;
import myjerseyy.psbf_jersey.entity.Shipment;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.AddressRepository;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.ShipmentRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private static final List<OrderStatus> SHIPMENT_STATUSES = Arrays.asList(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED,
            OrderStatus.RETURNED,
            OrderStatus.CANCELLED,
            OrderStatus.COMPLETED
    );

    @GetMapping
    @Transactional(readOnly = true)
    public String shipmentsPage(
            @RequestParam(required = false) String status,
            Model model,
            HttpSession session) {

        model.addAttribute("activePage", "shipment");
        model.addAttribute("pageTitle", "Kelola Pengiriman");
        model.addAttribute("statuses", SHIPMENT_STATUSES);

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        List<Order> ordersToShow;
        List<Order> allOrders = orderRepository.findAllWithPromoCode(org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        String filterStatus;
        if (status != null && !status.isEmpty()) {
            filterStatus = status;
            try {
                OrderStatus statusEnum = OrderStatus.valueOf(status);
                final OrderStatus finalStatus = statusEnum;
                ordersToShow = allOrders.stream()
                        .filter(o -> o.getStatus() == finalStatus)
                        .toList();
            } catch (IllegalArgumentException e) {
                ordersToShow = allOrders;
                filterStatus = null;
            }
        } else {
            ordersToShow = allOrders;
            filterStatus = null;
        }

        for (Order o : ordersToShow) {
            Hibernate.initialize(o.getCustomer());
            Hibernate.initialize(o.getItems());
            if (o.getPromoCode() != null) {
                Hibernate.initialize(o.getPromoCode());
            }
            Optional<Shipment> shipment = shipmentRepository.findByOrderId(o.getId());
            shipment.ifPresent(s -> {
                if (s.getAddress() != null) {
                    Hibernate.initialize(s.getAddress());
                }
                o.setShipment(s);
            });
        }

        List<Address> addresses = addressRepository.findAll();

        model.addAttribute("orders", ordersToShow);
        model.addAttribute("addresses", addresses);
        model.addAttribute("selectedStatus", filterStatus);

        return "kelola-pengiriman";
    }

    @PostMapping("/update-resi")
    public String updateResi(
            @RequestParam Long orderId,
            @RequestParam String courierName,
            @RequestParam String trackingNumber,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam(value = "shippingCost", required = false, defaultValue = "0") Double shippingCost,
            RedirectAttributes redirectAttributes) {

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan!");
            return "redirect:/admin/shipments";
        }

        Order order = orderOpt.get();
        Optional<Shipment> existingShipment = shipmentRepository.findByOrderId(orderId);

        Shipment shipment;
        if (existingShipment.isPresent()) {
            shipment = existingShipment.get();
        } else {
            shipment = new Shipment();
            shipment.setOrder(order);
            shipment.setStatus(OrderStatus.PROCESSING);
        }

        if (addressId != null && addressId > 0) {
            addressRepository.findById(addressId).ifPresent(shipment::setAddress);
        }

        shipment.setCourierName(courierName);
        shipment.setTrackingNumber(trackingNumber);
        if (shippingCost != null && shippingCost > 0) {
            shipment.setShippingCost(shippingCost);
        }
        shipment.setStatus(OrderStatus.SHIPPED);
        order.setStatus(OrderStatus.SHIPPED);

        shipmentRepository.save(shipment);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Resi berhasil diinput dan order sedang dikirim!");
        return "redirect:/admin/shipments";
    }

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam Long orderId,
            @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes) {

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan!");
            return "redirect:/admin/shipments";
        }

        Order order = orderOpt.get();
        
        if (status == OrderStatus.PROCESSING && order.getStatus() == OrderStatus.CONFIRMED) {
            Shipment shipment = new Shipment();
            shipment.setOrder(order);
            shipment.setStatus(OrderStatus.PROCESSING);
            shipment.setShippingCost(25000.0);
            shipmentRepository.save(shipment);
        }
        
        Optional<Shipment> existingShipment = shipmentRepository.findByOrderId(orderId);
        
        if (existingShipment.isPresent()) {
            Shipment shipment = existingShipment.get();
            shipment.setStatus(status);
            shipmentRepository.save(shipment);
        }
        
        order.setStatus(status);
        orderRepository.save(order);

        String message = "Status berhasil diupdate!";
        if (status == OrderStatus.DELIVERED) {
            message = "Pengiriman berhasil diselesaikan!";
        } else if (status == OrderStatus.RETURNED) {
            message = "Order ditandai sebagai dikembalikan!";
        } else if (status == OrderStatus.CANCELLED) {
            message = "Order berhasil dibatalkan!";
        }

        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/shipments";
    }

    @GetMapping("/create/{orderId}")
    public String createShipment(
            @PathVariable Long orderId,
            @RequestParam(required = false) Long addressId,
            RedirectAttributes redirectAttributes) {

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan!");
            return "redirect:/admin/orders";
        }

        Order order = orderOpt.get();
        if (order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PROCESSING) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak bisa diproses!");
            return "redirect:/admin/orders";
        }

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.PROCESSING);
        }

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setStatus(OrderStatus.PROCESSING);

        if (addressId != null && addressId > 0) {
            addressRepository.findById(addressId).ifPresent(shipment::setAddress);
        }

        shipmentRepository.save(shipment);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Pengiriman berhasil dibuat! Silakan input resi.");
        return "redirect:/admin/shipments";
    }
}
