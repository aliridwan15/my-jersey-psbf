package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Brand;
import myjerseyy.psbf_jersey.entity.League;
import myjerseyy.psbf_jersey.entity.StoreProfile;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.BrandRepository;
import myjerseyy.psbf_jersey.repository.LeagueRepository;
import myjerseyy.psbf_jersey.repository.StoreProfileRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreProfileRepository storeProfileRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private BrandRepository brandRepository;

    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }
        
        return null;
    }

    @ModelAttribute("storeProfile")
    public StoreProfile getStoreProfile() {
        Optional<StoreProfile> storeProfile = storeProfileRepository.findById(1L);
        return storeProfile.orElse(new StoreProfile());
    }

    @ModelAttribute("leagues")
    public List<League> globalLeagues() {
        return leagueRepository.findAll();
    }

    @ModelAttribute("brands")
    public List<Brand> globalBrands() {
        return brandRepository.findAll();
    }
}
