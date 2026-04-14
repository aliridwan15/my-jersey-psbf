package myjerseyy.psbf_jersey.controller;

import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.entity.Jersey;
import myjerseyy.psbf_jersey.entity.League;
import myjerseyy.psbf_jersey.entity.Team;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.BrandRepository;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.LeagueRepository;
import myjerseyy.psbf_jersey.repository.TeamRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/admin/jerseys")
public class JerseyController {

    private final JerseyRepository jerseyRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    public JerseyController(JerseyRepository jerseyRepository) {
        this.jerseyRepository = jerseyRepository;
    }

    @GetMapping
    public String getJerseys(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        
        Page<Jersey> jerseysPage = jerseyRepository.findAll(
            PageRequest.of(page, 10, Sort.by("id").ascending())
        );
        
        model.addAttribute("jerseys", jerseysPage);
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("teams", teamRepository.findAll());
        model.addAttribute("leagues", leagueRepository.findAll());
        
        return "kelola-jersey";
    }

    @PostMapping("/save")
    public String saveJersey(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("teamId") Long teamId,
            @RequestParam("leagueId") Long leagueId,
            @RequestParam("stockS") Integer stockS,
            @RequestParam("stockM") Integer stockM,
            @RequestParam("stockL") Integer stockL,
            @RequestParam("stockXL") Integer stockXL,
            @RequestParam("brand.id") Long brandId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        Jersey jersey;
        
        if (id != null && id > 0) {
            jersey = jerseyRepository.findById(id).orElse(new Jersey());
        } else {
            jersey = new Jersey();
        }
        
        jersey.setName(name);
        jersey.setPrice(price);
        jersey.setStockS(stockS != null ? stockS : 0);
        jersey.setStockM(stockM != null ? stockM : 0);
        jersey.setStockL(stockL != null ? stockL : 0);
        jersey.setStockXL(stockXL != null ? stockXL : 0);
        
        if (file != null && !file.isEmpty()) {
            try {
                jersey.setImageBase64(Base64.getEncoder().encodeToString(file.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (teamId != null) {
            Team team = teamRepository.findById(teamId).orElse(null);
            jersey.setTeam(team);
        }
        
        if (leagueId != null) {
            League league = leagueRepository.findById(leagueId).orElse(null);
            jersey.setLeague(league);
        }
        
        if (brandId != null) {
            Brand brand = brandRepository.findById(brandId).orElse(null);
            jersey.setBrand(brand);
        }
        
        jerseyRepository.save(jersey);
        
        return "redirect:/admin/jerseys";
    }

    @PostMapping("/delete")
    public String deleteJersey(@RequestParam("id") Long id) {
        jerseyRepository.deleteById(id);
        return "redirect:/admin/jerseys";
    }
}
