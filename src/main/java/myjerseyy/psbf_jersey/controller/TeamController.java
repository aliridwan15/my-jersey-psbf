package myjerseyy.psbf_jersey.controller;

import myjerseyy.psbf_jersey.entity.Team;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.LeagueRepository;
import myjerseyy.psbf_jersey.repository.TeamRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/admin/teams")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listTeams(
            @RequestParam(defaultValue = "0") int page,
            Model model, HttpSession session) {
        
        int safePage = Math.max(0, page);
        
        Pageable pageable = PageRequest.of(safePage, 10);
        Page<Team> teamPage = teamRepository.findAll(pageable);
        
        model.addAttribute("teams", teamPage);
        model.addAttribute("currentPage", teamPage.getNumber());
        model.addAttribute("totalPages", teamPage.getTotalPages());
        model.addAttribute("totalItems", teamPage.getTotalElements());
        
        model.addAttribute("leagues", leagueRepository.findAll());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "kelola-tim";
    }

    @PostMapping("/save")
    public String saveTeam(
            @ModelAttribute Team team,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "leagueId", required = false) Long leagueId) {
        
        if (leagueId != null) {
            leagueRepository.findById(leagueId).ifPresent(team::setLeague);
        }
        
        if (file != null && !file.isEmpty()) {
            try {
                String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                team.setLogo(base64);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (team.getId() != null) {
            teamRepository.findById(team.getId()).ifPresent(existingTeam -> {
                if (team.getLogo() == null || team.getLogo().isEmpty()) {
                    team.setLogo(existingTeam.getLogo());
                }
            });
        }
        
        teamRepository.save(team);
        
        return "redirect:/admin/teams";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable("id") Long id) {
        teamRepository.deleteById(id);
        return "redirect:/admin/teams";
    }
}