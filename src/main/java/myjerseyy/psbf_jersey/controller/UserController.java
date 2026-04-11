package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<User> userPage = userRepository.findByRoleNot("ADMIN", pageable);

        model.addAttribute("users", userPage);
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> currentUser = userRepository.findById(userId);
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "kelola-user";
    }

    @PostMapping("/save")
    public String saveUser(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("username") String username,
            @RequestParam("name") String name,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "gender", required = false) String gender,
            RedirectAttributes redirectAttributes) {

        if (id != null && id > 0) {
            userRepository.findById(id).ifPresent(user -> {
                user.setName(name);
                user.setAddress(address != null ? address : "");
                user.setGender(gender != null ? gender : "");
                if (password != null && !password.isEmpty()) {
                    user.setPassword(passwordEncoder.encode(password));
                }
                userRepository.save(user);
            });
            redirectAttributes.addFlashAttribute("successMessage", "Data pengguna berhasil diperbarui!");
        } else {
            if (userRepository.findByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username sudah digunakan!");
                return "redirect:/admin/users";
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setName(name);
            newUser.setRole("CUSTOMER");
            newUser.setAddress(address != null ? address : "");
            newUser.setGender(gender != null ? gender : "");
            if (password != null && !password.isEmpty()) {
                newUser.setPassword(passwordEncoder.encode(password));
            } else {
                newUser.setPassword(passwordEncoder.encode("password123"));
            }
            userRepository.save(newUser);
            redirectAttributes.addFlashAttribute("successMessage", "Pengguna baru berhasil ditambahkan!");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Pengguna berhasil dihapus!");
        return "redirect:/admin/users";
    }
}
