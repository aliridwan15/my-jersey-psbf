package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.entity.Wishlist;
import myjerseyy.psbf_jersey.repository.UserRepository;
import myjerseyy.psbf_jersey.repository.WishlistRepository;
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
@RequestMapping("/admin/wishlists")
public class WishlistController {
    
    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public String wishlistsPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {
        
        model.addAttribute("activePage", "wishlist");
        model.addAttribute("pageTitle", "Pantau Wishlist");
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "addedDate"));
        Page<Wishlist> wishlistPage = wishlistRepository.findAll(pageable);
        
        model.addAttribute("wishlists", wishlistPage.getContent());
        model.addAttribute("currentPage", wishlistPage.getNumber());
        model.addAttribute("totalPages", wishlistPage.getTotalPages());
        model.addAttribute("totalItems", wishlistPage.getTotalElements());
        model.addAttribute("isFirst", wishlistPage.isFirst());
        model.addAttribute("isLast", wishlistPage.isLast());
        model.addAttribute("hasNext", wishlistPage.hasNext());
        model.addAttribute("hasPrevious", wishlistPage.hasPrevious());
        model.addAttribute("showPagination", wishlistPage.getTotalElements() > 10);
        
        return "admin/pantau-wishlist";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteWishlist(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        if (wishlistRepository.existsById(id)) {
            wishlistRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Wishlist berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Wishlist tidak ditemukan!");
        }
        
        return "redirect:/admin/wishlists";
    }
}
