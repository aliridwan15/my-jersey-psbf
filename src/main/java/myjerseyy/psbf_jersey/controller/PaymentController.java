package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Order;
import myjerseyy.psbf_jersey.entity.OrderStatus;
import myjerseyy.psbf_jersey.entity.Payment;
import myjerseyy.psbf_jersey.entity.PaymentStatus;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.OrderRepository;
import myjerseyy.psbf_jersey.repository.PaymentRepository;
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

import java.util.Optional;

@Controller
@RequestMapping("/admin/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String paymentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Model model,
            HttpSession session) {

        model.addAttribute("activePage", "payment");
        model.addAttribute("pageTitle", "Kelola Pembayaran");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Payment> paymentPage;

        if (status != null && !status.isEmpty()) {
            try {
                PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
                paymentPage = paymentRepository.findByPaymentStatus(paymentStatus, pageable);
                model.addAttribute("filterStatus", status);
            } catch (IllegalArgumentException e) {
                paymentPage = paymentRepository.findAll(pageable);
            }
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("currentPage", paymentPage.getNumber());
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("totalItems", paymentPage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "kelola-pembayaran";
    }

    @PostMapping("/approve/{id}")
    public String approvePayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            if (order != null) {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Pembayaran berhasil divalidasi! Status order otomatis berubah menjadi Dikonfirmasi.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pembayaran tidak ditemukan!");
        }

        return "redirect:/admin/payments";
    }

    @PostMapping("/reject/{id}")
    public String rejectPayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            redirectAttributes.addFlashAttribute("successMessage", "Pembayaran berhasil ditolak!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pembayaran tidak ditemukan!");
        }

        return "redirect:/admin/payments";
    }

    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        paymentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Data pembayaran berhasil dihapus!");
        return "redirect:/admin/payments";
    }
}
