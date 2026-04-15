package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Jersey;
import myjerseyy.psbf_jersey.entity.Order;
import myjerseyy.psbf_jersey.entity.Review;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.ReviewRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JerseyRepository jerseyRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/my-reviews")
    public String myReviewsPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/login";
        }
        
        model.addAttribute("currentUser", currentUser.get());
        
        List<Review> reviews = reviewRepository.findByUserIdOrderByReviewDateDesc(userId);
        model.addAttribute("reviews", reviews);
        
        return "my-reviews";
    }

    @GetMapping("/admin/reviews")
    public String adminReviewsPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        model.addAttribute("activePage", "review");
        model.addAttribute("pageTitle", "Kelola Ulasan");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "reviewDate"));
        Page<Review> reviewPage = reviewRepository.findAll(pageable);

        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", reviewPage.getNumber());
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("totalItems", reviewPage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "admin/kelola-ulasan";
    }

    @GetMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ulasan berhasil dihapus!");
        return "redirect:/admin/reviews";
    }
    
    @PostMapping("/review/submit")
    public String submitReview(
            @RequestParam Long jerseyId,
            @RequestParam Long orderId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Optional<Jersey> jerseyOpt = jerseyRepository.findById(jerseyId);
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (jerseyOpt.isEmpty() || orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Data tidak valid!");
            return "redirect:/orders/" + orderId;
        }
        
        if (!orderOpt.get().getCustomer().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak memiliki akses ke pesanan ini!");
            return "redirect:/orders";
        }
        
        Review review = new Review();
        review.setUser(userOpt.get());
        review.setJersey(jerseyOpt.get());
        review.setOrder(orderOpt.get());
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());
        
        reviewRepository.save(review);
        
        redirectAttributes.addFlashAttribute("successMessage", "Terima kasih! Ulasan Anda telah dikirim.");
        return "redirect:/orders/" + orderId + "?reviewSuccess=true";
    }
}
