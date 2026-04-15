package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.BrandRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@Controller
@RequestMapping("/admin/brands")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listBrands(
            @RequestParam(defaultValue = "0") int page,
            Model model, HttpSession session) {
        
        // Validate page number to prevent negative
        int safePage = Math.max(0, page);
        
        Pageable pageable = PageRequest.of(safePage, 10);
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        
        // Simpan Page object utuh
        model.addAttribute("brands", brandPage);
        
        // Simpan info pagination dengan aman
        model.addAttribute("currentPage", brandPage.getNumber());
        model.addAttribute("totalPages", brandPage.getTotalPages());
        model.addAttribute("totalItems", brandPage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "admin/kelola-brand";
    }

    @PostMapping("/save")
    public String saveBrand(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Brand brand;
        
        if (id != null && id > 0) {
            brand = brandRepository.findById(id).orElse(new Brand());
        } else {
            brand = new Brand();
        }
        
        if (name != null && !name.trim().isEmpty()) {
            brand.setName(name.trim());
        }
        
        if (description != null) {
            brand.setDescription(description.trim());
        } else {
            brand.setDescription("");
        }
        
        if (file != null && !file.isEmpty()) {
            try {
                brand.setLogo(file.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        brandRepository.save(brand);
        
        return "redirect:/admin/brands";
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable("id") Long id) {
        brandRepository.deleteById(id);
        return "redirect:/admin/brands";
    }

    @GetMapping("/logo/{id}")
    public void getLogo(@PathVariable("id") Long id, HttpServletResponse response) {
        brandRepository.findById(id).ifPresent(brand -> {
            if (brand.getLogo() != null && brand.getLogo().length > 0) {
                response.setContentType("image/jpeg");
                response.setContentLength(brand.getLogo().length);
                try {
                    response.getOutputStream().write(brand.getLogo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
