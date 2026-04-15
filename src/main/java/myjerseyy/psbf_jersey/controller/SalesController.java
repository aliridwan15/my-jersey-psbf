package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Order;
import myjerseyy.psbf_jersey.entity.OrderStatus;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class SalesController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/sales-report")
    public String salesReportPage(Model model, HttpSession session) {
        model.addAttribute("activePage", "salesReport");
        model.addAttribute("pageTitle", "Laporan Penjualan");
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        List<Order> completedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
                .toList();
        
        double totalRevenue = completedOrders.stream()
                .mapToDouble(o -> o.getFinalPrice() != null ? o.getFinalPrice() : o.getTotalPrice())
                .sum();
        
        Map<String, Long> jerseySalesCount = new HashMap<>();
        for (Order order : completedOrders) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    String jerseyName = item.getJersey().getName();
                    jerseySalesCount.merge(jerseyName, (long) item.getQuantity(), Long::sum);
                }
            }
        }
        
        List<Map.Entry<String, Long>> topJerseys = jerseySalesCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        List<String> chartLabels = new ArrayList<>();
        List<Long> chartData = new ArrayList<>();
        
        for (Map.Entry<String, Long> entry : topJerseys) {
            chartLabels.add(entry.getKey());
            chartData.add(entry.getValue());
        }
        
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", completedOrders.size());
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);
        
        return "admin/laporan-penjualan";
    }
}
