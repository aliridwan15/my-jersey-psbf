package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Faq;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.FaqRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/admin/faqs")
public class FAQController {

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String faqsPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        model.addAttribute("activePage", "faq");
        model.addAttribute("pageTitle", "Kelola FAQ");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "sortOrder"));
        Page<Faq> faqPage = faqRepository.findAll(pageable);

        model.addAttribute("faqs", faqPage.getContent());
        model.addAttribute("currentPage", faqPage.getNumber());
        model.addAttribute("totalPages", faqPage.getTotalPages());
        model.addAttribute("totalItems", faqPage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "kelola-faq";
    }

    @PostMapping("/save")
    public String saveFaq(
            @ModelAttribute Faq faq,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            RedirectAttributes redirectAttributes) {

        if (id != null && id > 0) {
            Optional<Faq> existingFaq = faqRepository.findById(id);
            if (existingFaq.isPresent()) {
                Faq f = existingFaq.get();
                f.setQuestion(faq.getQuestion());
                f.setAnswer(faq.getAnswer());
                f.setCategory(faq.getCategory());
                f.setSortOrder(faq.getSortOrder());
                f.setIsActive(isActive != null);
                faqRepository.save(f);
                redirectAttributes.addFlashAttribute("successMessage", "FAQ berhasil diperbarui!");
            }
        } else {
            faq.setIsActive(isActive != null);
            faqRepository.save(faq);
            redirectAttributes.addFlashAttribute("successMessage", "FAQ berhasil ditambahkan!");
        }

        return "redirect:/admin/faqs";
    }

    @GetMapping("/delete/{id}")
    public String deleteFaq(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        faqRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "FAQ berhasil dihapus!");
        return "redirect:/admin/faqs";
    }
}
