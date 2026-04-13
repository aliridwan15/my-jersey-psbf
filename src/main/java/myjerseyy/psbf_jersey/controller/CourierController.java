package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Courier;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.CourierRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/couriers")
public class CourierController {
    
    @Autowired
    private CourierRepository courierRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public String couriersPage(Model model, HttpSession session) {
        model.addAttribute("activePage", "courier");
        model.addAttribute("pageTitle", "Kelola Kurir");
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        List<Courier> couriers = courierRepository.findAll();
        model.addAttribute("couriers", couriers);
        
        return "kelola-kurir";
    }
    
    @PostMapping("/save")
    public String saveCourier(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam String code,
            @RequestParam(required = false) String logoImage,
            @RequestParam(required = false) Boolean isActive,
            RedirectAttributes redirectAttributes) {
        
        Courier courier;
        
        if (id != null && id > 0) {
            courier = courierRepository.findById(id).orElse(new Courier());
        } else {
            courier = new Courier();
        }
        
        courier.setName(name);
        courier.setCode(code);
        courier.setLogoImage(logoImage);
        courier.setIsActive(isActive != null ? isActive : true);
        
        courierRepository.save(courier);
        
        redirectAttributes.addFlashAttribute("successMessage", "Data kurir berhasil disimpan!");
        return "redirect:/admin/couriers";
    }
    
    @GetMapping("/toggle/{id}")
    public String toggleCourier(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        Optional<Courier> courierOpt = courierRepository.findById(id);
        
        if (courierOpt.isPresent()) {
            Courier courier = courierOpt.get();
            courier.setIsActive(!courier.getIsActive());
            courierRepository.save(courier);
            
            String status = courier.getIsActive() ? "diaktifkan" : "dinonaktifkan";
            redirectAttributes.addFlashAttribute("successMessage", "Kurir berhasil " + status + "!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kurir tidak ditemukan!");
        }
        
        return "redirect:/admin/couriers";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCourier(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        if (courierRepository.existsById(id)) {
            courierRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kurir berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kurir tidak ditemukan!");
        }
        
        return "redirect:/admin/couriers";
    }
    
    @GetMapping("/edit/{id}")
    @ResponseBody
    public Courier getCourier(@PathVariable Long id) {
        return courierRepository.findById(id).orElse(null);
    }
}
