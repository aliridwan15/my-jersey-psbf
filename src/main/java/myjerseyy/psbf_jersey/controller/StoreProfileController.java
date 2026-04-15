package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.StoreProfile;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.StoreProfileRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
public class StoreProfileController {
    
    @Autowired
    private StoreProfileRepository storeProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/admin/store-profile")
    public String storeProfilePage(Model model, HttpSession session) {
        model.addAttribute("activePage", "storeProfile");
        model.addAttribute("pageTitle", "Pengaturan Toko");
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        StoreProfile storeProfile = storeProfileRepository.findById(1L).orElse(null);
        
        if (storeProfile == null) {
            storeProfile = new StoreProfile();
            storeProfile.setStoreName("myjersey");
            storeProfile.setLatitude(-7.2575);
            storeProfile.setLongitude(112.7521);
            storeProfile = storeProfileRepository.save(storeProfile);
        }
        
        model.addAttribute("storeProfile", storeProfile);
        
        return "admin/pengaturan-toko";
    }
    
    @PostMapping("/admin/store-profile/save")
    public String saveStoreProfile(
            @ModelAttribute StoreProfile storeProfile,
            @RequestParam(value = "logoBase64", required = false) String logoBase64,
            RedirectAttributes redirectAttributes) {
        
        StoreProfile existingProfile = storeProfileRepository.findById(1L).orElse(null);
        
        if (existingProfile != null) {
            existingProfile.setStoreName(storeProfile.getStoreName());
            existingProfile.setPhoneNumber(storeProfile.getPhoneNumber());
            existingProfile.setEmail(storeProfile.getEmail());
            existingProfile.setFullAddress(storeProfile.getFullAddress());
            existingProfile.setAboutUs(storeProfile.getAboutUs());
            existingProfile.setLatitude(storeProfile.getLatitude());
            existingProfile.setLongitude(storeProfile.getLongitude());
            
            if (logoBase64 != null && !logoBase64.isEmpty()) {
                existingProfile.setLogoImage(logoBase64);
            }
            
            storeProfileRepository.save(existingProfile);
        } else {
            storeProfile.setId(1L);
            if (logoBase64 != null && !logoBase64.isEmpty()) {
                storeProfile.setLogoImage(logoBase64);
            }
            storeProfileRepository.save(storeProfile);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Pengaturan toko berhasil disimpan!");
        return "redirect:/admin/store-profile";
    }
}
