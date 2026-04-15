package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Cart;
import myjerseyy.psbf_jersey.entity.Jersey;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.CartRepository;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class UserCartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JerseyRepository jerseyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String cartPage(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        double subtotal = 0;
        for (Cart item : cartItems) {
            subtotal += item.getJersey().getPrice() * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal);
        model.addAttribute("cartCount", cartItems.size());

        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("jerseyId") Long jerseyId,
                           @RequestParam(value = "size", required = false, defaultValue = "M") String size,
                           @RequestParam(value = "quantity", required = false, defaultValue = "1") Integer quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Silakan login terlebih dahulu!");
            return "redirect:/login";
        }

        Optional<Jersey> jerseyOpt = jerseyRepository.findById(jerseyId);
        if (jerseyOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Jersey tidak ditemukan!");
            return "redirect:/products";
        }

        Jersey jersey = jerseyOpt.get();
        List<Cart> existingItems = cartRepository.findByUserId(user.getId());
        
        for (Cart existing : existingItems) {
            if (existing.getJersey().getId().equals(jerseyId) && 
                existing.getSize() != null && 
                existing.getSize().equals(size)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                cartRepository.save(existing);
                redirectAttributes.addFlashAttribute("successMessage", "Jumlah item di keranjang diperbarui!");
                return "redirect:/cart";
            }
        }

        Cart newItem = new Cart(user, jersey, size, quantity);
        cartRepository.save(newItem);

        Integer currentCount = (Integer) session.getAttribute("cartCount");
        session.setAttribute("cartCount", (currentCount != null ? currentCount : 0) + 1);

        redirectAttributes.addFlashAttribute("successMessage", "Jersey ditambahkan ke keranjang!");
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("cartItemId") Long cartItemId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Cart> cartOpt = cartRepository.findById(cartItemId);
        if (cartOpt.isPresent() && cartOpt.get().getUser().getId().equals(user.getId())) {
            if (quantity <= 0) {
                cartRepository.deleteById(cartItemId);
                redirectAttributes.addFlashAttribute("successMessage", "Item dihapus dari keranjang!");
            } else {
                Cart cart = cartOpt.get();
                cart.setQuantity(quantity);
                cartRepository.save(cart);
                redirectAttributes.addFlashAttribute("successMessage", "Jumlah diperbarui!");
            }
        }

        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Cart> cartOpt = cartRepository.findById(id);
        if (cartOpt.isPresent() && cartOpt.get().getUser().getId().equals(user.getId())) {
            cartRepository.deleteById(id);
            
            Integer currentCount = (Integer) session.getAttribute("cartCount");
            if (currentCount != null && currentCount > 0) {
                session.setAttribute("cartCount", currentCount - 1);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Item dihapus dari keranjang!");
        }

        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        cartRepository.deleteAll(cartItems);
        session.setAttribute("cartCount", 0);

        redirectAttributes.addFlashAttribute("successMessage", "Keranjang dikosongkan!");
        return "redirect:/cart";
    }

    private User getUserFromSession(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            return userOpt.orElse(null);
        }
        return null;
    }
}
