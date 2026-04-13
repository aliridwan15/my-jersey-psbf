package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.PaymentMethod;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.PaymentMethodRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/payment-methods")
public class PaymentMethodController {
    
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public String paymentMethodsPage(Model model, HttpSession session) {
        model.addAttribute("activePage", "paymentMethod");
        model.addAttribute("pageTitle", "Metode Pembayaran");
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        model.addAttribute("paymentMethods", paymentMethods);
        
        return "kelola-metode-pembayaran";
    }
    
    @PostMapping("/save")
    public String savePaymentMethod(
            @RequestParam(required = false) Long id,
            @RequestParam String providerName,
            @RequestParam String accountNumber,
            @RequestParam String accountHolder,
            @RequestParam(required = false) String instruction,
            @RequestParam(required = false) Boolean isActive,
            RedirectAttributes redirectAttributes) {
        
        PaymentMethod paymentMethod;
        
        if (id != null && id > 0) {
            paymentMethod = paymentMethodRepository.findById(id)
                    .orElse(new PaymentMethod());
        } else {
            paymentMethod = new PaymentMethod();
        }
        
        paymentMethod.setProviderName(providerName);
        paymentMethod.setAccountNumber(accountNumber);
        paymentMethod.setAccountHolder(accountHolder);
        paymentMethod.setInstruction(instruction);
        paymentMethod.setIsActive(isActive != null ? isActive : true);
        
        paymentMethodRepository.save(paymentMethod);
        
        redirectAttributes.addFlashAttribute("successMessage", "Metode pembayaran berhasil disimpan!");
        return "redirect:/admin/payment-methods";
    }
    
    @GetMapping("/toggle/{id}")
    public String togglePaymentMethod(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        Optional<PaymentMethod> paymentMethodOpt = paymentMethodRepository.findById(id);
        
        if (paymentMethodOpt.isPresent()) {
            PaymentMethod paymentMethod = paymentMethodOpt.get();
            paymentMethod.setIsActive(!paymentMethod.getIsActive());
            paymentMethodRepository.save(paymentMethod);
            
            String status = paymentMethod.getIsActive() ? "diaktifkan" : "dinonaktifkan";
            redirectAttributes.addFlashAttribute("successMessage", "Metode pembayaran berhasil " + status + "!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Metode pembayaran tidak ditemukan!");
        }
        
        return "redirect:/admin/payment-methods";
    }
    
    @GetMapping("/delete/{id}")
    public String deletePaymentMethod(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        if (paymentMethodRepository.existsById(id)) {
            paymentMethodRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Metode pembayaran berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Metode pembayaran tidak ditemukan!");
        }
        
        return "redirect:/admin/payment-methods";
    }
    
    @GetMapping("/edit/{id}")
    @ResponseBody
    public PaymentMethod getPaymentMethod(@PathVariable Long id) {
        return paymentMethodRepository.findById(id).orElse(null);
    }
}
