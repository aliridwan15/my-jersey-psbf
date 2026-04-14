package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository = 
        new HttpSessionSecurityContextRepository();

    private final SecurityContextHolderStrategy securityContextHolderStrategy = 
        SecurityContextHolder.getContextHolderStrategy();

    @GetMapping("/auth")
    public String loginPage() {
        return "auth";
    }

    @GetMapping("/login")
    public String loginPageAlt() {
        return "auth";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth";
    }

    @PostMapping("/auth/login")
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            List<SimpleGrantedAuthority> authorities = 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
            
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
            
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authToken);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            
            session.setAttribute("username", username);
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin.html";
            }
            return "redirect:/";
        }
        
        redirectAttributes.addFlashAttribute("error", "Username atau Password salah!");
        return "redirect:/login";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "gender", required = false) String gender,
            RedirectAttributes redirectAttributes) {
        
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("registerError", "Username sudah digunakan!");
            redirectAttributes.addFlashAttribute("showRegister", true);
            return "redirect:/register";
        }
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setName(name);
        newUser.setRole("CUSTOMER");
        newUser.setAddress(address != null ? address : "");
        newUser.setGender(gender != null ? gender : "");
        
        userRepository.save(newUser);
        
        redirectAttributes.addFlashAttribute("registerSuccess", "Registrasi berhasil! Silakan login.");
        return "redirect:/login";
    }
}
