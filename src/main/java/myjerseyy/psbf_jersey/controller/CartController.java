package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Cart;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.CartRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/admin/carts")
public class CartController {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public String cartsPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {
        
        model.addAttribute("activePage", "carts");
        model.addAttribute("pageTitle", "Pantau Keranjang");
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Cart> cartPage = cartRepository.findAll(pageable);
        
        model.addAttribute("carts", cartPage.getContent());
        model.addAttribute("currentPage", cartPage.getNumber());
        model.addAttribute("totalPages", cartPage.getTotalPages());
        model.addAttribute("totalItems", cartPage.getTotalElements());
        model.addAttribute("isFirst", cartPage.isFirst());
        model.addAttribute("isLast", cartPage.isLast());
        model.addAttribute("hasNext", cartPage.hasNext());
        model.addAttribute("hasPrevious", cartPage.hasPrevious());
        model.addAttribute("showPagination", cartPage.getTotalElements() > 10);
        
        return "admin/pantau-keranjang";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCart(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        if (cartRepository.existsById(id)) {
            cartRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Keranjang berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Keranjang tidak ditemukan!");
        }
        
        return "redirect:/admin/carts";
    }
}
