package myjerseyy.psbf_jersey.controller;

import myjerseyy.psbf_jersey.entity.Address;
import myjerseyy.psbf_jersey.entity.Order;
import myjerseyy.psbf_jersey.entity.OrderStatus;
import myjerseyy.psbf_jersey.entity.Shipment;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.AddressRepository;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.ShipmentRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AddressRepository addressRepository;

    @GetMapping
    public String ordersPage(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String filterToday,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {
        
        model.addAttribute("activePage", "orders");
        model.addAttribute("pageTitle", "Kelola Order");
        model.addAttribute("statuses", OrderStatus.values());
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        final LocalDate filterDate;
        if (filterToday != null) {
            filterDate = LocalDate.now();
        } else {
            filterDate = date;
        }
        
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> orderPage = orderRepository.findAllWithPromoCode(pageable);
        
        List<Order> orders = orderPage.getContent();
        
        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus statusEnum = OrderStatus.valueOf(status);
                orders = orders.stream()
                        .filter(o -> o.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }
        
        if (filterDate != null) {
            orders = orders.stream()
                    .filter(o -> o.getOrderDate().toLocalDate().equals(filterDate))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDate", filterDate);
        model.addAttribute("filterToday", filterToday);
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("pageSize", orderPage.getSize());
        model.addAttribute("isFirst", orderPage.isFirst());
        model.addAttribute("isLast", orderPage.isLast());
        model.addAttribute("hasNext", orderPage.hasNext());
        model.addAttribute("hasPrevious", orderPage.hasPrevious());
        model.addAttribute("showPagination", orderPage.getTotalElements() > 10);
        
        return "kelola-transaksi";
    }

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam Long orderId,
            @RequestParam OrderStatus status) {
        
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
        
        return "redirect:/admin/orders";
    }

    @PostMapping("/process-shipping/{orderId}")
    public String processShipping(
            @PathVariable Long orderId,
            RedirectAttributes redirectAttributes) {
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan!");
            return "redirect:/admin/orders";
        }
        
        Order order = orderOpt.get();
        
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hanya order dengan status CONFIRMED yang bisa diproses!");
            return "redirect:/admin/orders";
        }
        
        order.setStatus(OrderStatus.PROCESSING);
        
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setStatus(OrderStatus.PROCESSING);
        
        Address defaultAddress = addressRepository.findByUser_Id(order.getCustomer().getId()).stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                .findFirst()
                .orElseGet(() -> addressRepository.findByUser_Id(order.getCustomer().getId()).stream().findFirst().orElse(null));
        
        if (defaultAddress != null) {
            shipment.setAddress(defaultAddress);
        }
        
        shipmentRepository.save(shipment);
        orderRepository.save(order);
        
        redirectAttributes.addFlashAttribute("successMessage", "Order berhasil diproses! Silakan input resi di halaman Kelola Pengiriman.");
        return "redirect:/admin/shipments";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/admin/orders";
    }
}
