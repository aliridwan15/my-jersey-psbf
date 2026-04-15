package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Jersey;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.entity.Wishlist;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import myjerseyy.psbf_jersey.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wishlist")
public class UserWishlistController {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private JerseyRepository jerseyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String wishlistPage(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(user.getId());

        model.addAttribute("wishlistItems", wishlistItems);
        model.addAttribute("wishlistCount", wishlistItems.size());

        return "wishlist";
    }

    @PostMapping("/add")
    public String addToWishlist(@RequestParam("jerseyId") Long jerseyId,
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
        List<Wishlist> existingWishlists = wishlistRepository.findByUserId(user.getId());
        
        boolean alreadyExists = existingWishlists.stream()
                .anyMatch(w -> w.getJersey().getId().equals(jerseyId));
        
        if (alreadyExists) {
            redirectAttributes.addFlashAttribute("infoMessage", "Jersey sudah ada di wishlist!");
            return "redirect:/products";
        }

        Wishlist newItem = new Wishlist(user, jersey);
        wishlistRepository.save(newItem);

        redirectAttributes.addFlashAttribute("successMessage", "Jersey ditambahkan ke wishlist!");
        return "redirect:/wishlist";
    }

    @GetMapping("/remove/{id}")
    public String removeFromWishlist(@PathVariable Long id,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Wishlist> wishlistOpt = wishlistRepository.findById(id);
        if (wishlistOpt.isPresent() && wishlistOpt.get().getUser().getId().equals(user.getId())) {
            wishlistRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Item dihapus dari wishlist!");
        }

        return "redirect:/wishlist";
    }

    @PostMapping("/toggle/{jerseyId}")
    @ResponseBody
    public boolean toggleWishlistAjax(@PathVariable Long jerseyId, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return false;
        }

        List<Wishlist> existingWishlists = wishlistRepository.findByUserId(user.getId());
        
        Optional<Wishlist> existing = existingWishlists.stream()
                .filter(w -> w.getJersey().getId().equals(jerseyId))
                .findFirst();
        
        if (existing.isPresent()) {
            wishlistRepository.deleteById(existing.get().getId());
            return false;
        } else {
            Optional<Jersey> jerseyOpt = jerseyRepository.findById(jerseyId);
            if (jerseyOpt.isPresent()) {
                Wishlist newItem = new Wishlist(user, jerseyOpt.get());
                wishlistRepository.save(newItem);
                return true;
            }
            return false;
        }
    }

    @GetMapping("/check/{jerseyId}")
    @ResponseBody
    public boolean checkWishlist(@PathVariable Long jerseyId, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return false;
        }
        
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        return wishlists.stream().anyMatch(w -> w.getJersey().getId().equals(jerseyId));
    }

    @GetMapping("/check-all")
    @ResponseBody
    public List<Long> checkAllWishlists(HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return List.of();
        }
        
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        return wishlists.stream()
                .map(w -> w.getJersey().getId())
                .collect(Collectors.toList());
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
