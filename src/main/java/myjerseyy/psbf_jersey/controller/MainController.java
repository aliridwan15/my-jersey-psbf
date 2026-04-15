package myjerseyy.psbf_jersey.controller;

import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.entity.Jersey;
import myjerseyy.psbf_jersey.entity.League;
import myjerseyy.psbf_jersey.entity.PromoCode;
import myjerseyy.psbf_jersey.entity.StoreProfile;
import myjerseyy.psbf_jersey.repository.BrandRepository;
import myjerseyy.psbf_jersey.repository.JerseyRepository;
import myjerseyy.psbf_jersey.repository.LeagueRepository;
import myjerseyy.psbf_jersey.repository.PromoCodeRepository;
import myjerseyy.psbf_jersey.repository.StoreProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private JerseyRepository jerseyRepository;

    @Autowired
    private StoreProfileRepository storeProfileRepository;

    @GetMapping("/")
    public String indexPage(
            @RequestParam(required = false) Boolean logout,
            Model model, 
            HttpSession session) {
        
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("showLogoutMessage", true);
        }
        
        List<PromoCode> activePromos = promoCodeRepository.findByIsActiveTrue();
        PromoCode topPromo = promoCodeRepository.findTopActivePromo(java.time.LocalDate.now())
                .orElse(null);

        List<League> leagues = leagueRepository.findAll();
        List<Brand> brands = brandRepository.findAll();
        List<Jersey> latestJerseys = jerseyRepository.findTop8ByOrderByIdDesc();

        Optional<StoreProfile> storeProfile = storeProfileRepository.findById(1L);
        storeProfile.ifPresent(profile -> model.addAttribute("storeProfile", profile));

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        model.addAttribute("activePromo", topPromo);
        model.addAttribute("leagues", leagues);
        model.addAttribute("brands", brands);
        model.addAttribute("latestJerseys", latestJerseys);
        model.addAttribute("currentYear", java.time.Year.now().getValue());

        return "index";
    }

    @GetMapping("/products")
    public String productsPage(
            @RequestParam(required = false) Long leagueId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session) {
        
        List<Jersey> jerseys;
        String pageTitle = "Semua Produk";
        
        if (leagueId != null) {
            jerseys = jerseyRepository.findByLeagueId(leagueId);
            Optional<League> league = leagueRepository.findById(leagueId);
            if (league.isPresent()) {
                pageTitle = "Koleksi Jersey: " + league.get().getName();
                model.addAttribute("selectedLeague", league.get());
            }
        } else if (brandId != null) {
            jerseys = jerseyRepository.findByBrandId(brandId);
            Optional<Brand> brand = brandRepository.findById(brandId);
            if (brand.isPresent()) {
                pageTitle = "Koleksi Jersey: " + brand.get().getName();
                model.addAttribute("selectedBrand", brand.get());
            }
        } else if (search != null && !search.isEmpty()) {
            jerseys = jerseyRepository.findByNameContainingIgnoreCase(search);
            pageTitle = "Hasil Pencarian: " + search;
            model.addAttribute("searchQuery", search);
        } else {
            jerseys = jerseyRepository.findAll();
        }
        
        List<League> leagues = leagueRepository.findAll();
        List<Brand> brands = brandRepository.findAll();
        
        Optional<StoreProfile> storeProfile = storeProfileRepository.findById(1L);
        storeProfile.ifPresent(profile -> model.addAttribute("storeProfile", profile));
        
        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);
        
        model.addAttribute("jerseys", jerseys);
        model.addAttribute("leagues", leagues);
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("currentYear", java.time.Year.now().getValue());
        
        return "products";
    }
}
