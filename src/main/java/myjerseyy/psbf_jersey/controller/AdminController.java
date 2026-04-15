package myjerseyy.psbf_jersey.controller;

import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private JerseyRepository jerseyRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin.html")
    public String adminPage(Model model, HttpSession session) {
        Long totalJersey = jerseyRepository.count();
        Long totalOrder = orderRepository.count();
        Long totalUser = userRepository.count();
        Double totalPendapatan = orderRepository.sumTotalRevenue();
        List recentOrders = orderRepository.findTop5ByOrderByOrderDateDesc();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("totalJersey", totalJersey);
        model.addAttribute("totalOrder", totalOrder);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("totalPendapatan", totalPendapatan);
        model.addAttribute("recentOrders", recentOrders);
        
        return "admin/admin";
    }

    @GetMapping("/kelola-tim.html")
    public RedirectView kelolatiPage() {
        return new RedirectView("/admin/teams");
    }
}
