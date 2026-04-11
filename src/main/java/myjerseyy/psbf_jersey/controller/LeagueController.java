package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.League;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.LeagueRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/admin/leagues")
public class LeagueController {

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listLeagues(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<League> leaguePage = leagueRepository.findAll(pageable);

        model.addAttribute("leagues", leaguePage);
        model.addAttribute("currentPage", leaguePage.getNumber());
        model.addAttribute("totalPages", leaguePage.getTotalPages());
        model.addAttribute("totalItems", leaguePage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "kelola-liga";
    }

    @PostMapping("/save")
    public String saveLeague(
            @ModelAttribute League league,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (league.getName() == null || league.getName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nama liga tidak boleh kosong!");
            return "redirect:/admin/leagues";
        }

        if (file != null && !file.isEmpty()) {
            try {
                String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                String mimeType = file.getContentType();
                league.setLogo("data:" + mimeType + ";base64," + base64);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (league.getId() != null) {
            leagueRepository.findById(league.getId()).ifPresent(existing -> {
                if (league.getLogo() == null || league.getLogo().isEmpty()) {
                    league.setLogo(existing.getLogo());
                }
            });
        }

        leagueRepository.save(league);
        redirectAttributes.addFlashAttribute("successMessage", "Data liga berhasil disimpan!");

        return "redirect:/admin/leagues";
    }

    @GetMapping("/delete/{id}")
    public String deleteLeague(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        leagueRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Liga berhasil dihapus!");
        return "redirect:/admin/leagues";
    }
}
