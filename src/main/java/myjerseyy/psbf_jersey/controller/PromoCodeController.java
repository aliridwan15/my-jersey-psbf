package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.PromoCode;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.PromoCodeRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/promos")
public class PromoCodeController {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String promosPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        model.addAttribute("activePage", "promo");
        model.addAttribute("pageTitle", "Kelola Kupon");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<PromoCode> promoPage = promoCodeRepository.findAll(pageable);

        model.addAttribute("promos", promoPage);
        model.addAttribute("currentPage", promoPage.getNumber());
        model.addAttribute("totalPages", promoPage.getTotalPages());
        model.addAttribute("totalItems", promoPage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "kelola-kupon";
    }

    @PostMapping("/save")
    public String savePromo(
            @ModelAttribute PromoCode promoCode,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "maxDiscount", required = false) Double maxDiscount,
            RedirectAttributes redirectAttributes) {

        if (id != null && id > 0) {
            Optional<PromoCode> existingPromo = promoCodeRepository.findById(id);
            if (existingPromo.isPresent()) {
                PromoCode promo = existingPromo.get();
                promo.setCampaignName(promoCode.getCampaignName());
                promo.setCode(promoCode.getCode());
                promo.setDiscountPercent(promoCode.getDiscountPercent());
                promo.setMaxDiscount(maxDiscount != null ? maxDiscount : null);
                promo.setStartDate(promoCode.getStartDate());
                promo.setEndDate(promoCode.getEndDate());
                promo.setIsActive(isActive != null);
                promoCodeRepository.save(promo);
                redirectAttributes.addFlashAttribute("successMessage", "Kupon berhasil diperbarui!");
            }
        } else {
            promoCode.setIsActive(isActive != null);
            promoCode.setMaxDiscount(maxDiscount != null ? maxDiscount : null);
            promoCodeRepository.save(promoCode);
            redirectAttributes.addFlashAttribute("successMessage", "Kupon berhasil ditambahkan!");
        }

        return "redirect:/admin/promos";
    }

    @GetMapping("/delete/{id}")
    public String deletePromo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promoCodeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Kupon berhasil dihapus!");
        return "redirect:/admin/promos";
    }
}
