package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Address;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.AddressRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users/{userId}/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String addressPage(
            @PathVariable Long userId,
            Model model,
            HttpSession session) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }
        
        User user = userOpt.get();
        List<Address> addresses = addressRepository.findByUser_Id(userId);
        
        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);
        model.addAttribute("activePage", "user");
        
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId != null) {
            userRepository.findById(currentUserId).ifPresent(u -> model.addAttribute("currentUser", u));
        }
        
        return "kelola-alamat";
    }

    @PostMapping("/save")
    public String saveAddress(
            @PathVariable Long userId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("title") String title,
            @RequestParam("fullAddress") String fullAddress,
            @RequestParam("city") String city,
            @RequestParam(value = "postalCode", required = false) String postalCode,
            @RequestParam(value = "isDefault", required = false) Boolean isDefault,
            RedirectAttributes redirectAttributes) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }
        
        User user = userOpt.get();
        Address address;
        
        if (id != null && id > 0) {
            address = addressRepository.findById(id).orElse(new Address());
            address.setUser(user);
        } else {
            address = new Address();
            address.setUser(user);
        }
        
        if (isDefault != null && isDefault) {
            List<Address> existingAddresses = addressRepository.findByUser_Id(userId);
            for (Address existingAddr : existingAddresses) {
                if (!existingAddr.getId().equals(id)) {
                    existingAddr.setIsDefault(false);
                    addressRepository.save(existingAddr);
                }
            }
        }
        
        address.setTitle(title);
        address.setFullAddress(fullAddress);
        address.setCity(city);
        address.setPostalCode(postalCode != null ? postalCode : "");
        address.setIsDefault(isDefault != null && isDefault);
        
        addressRepository.save(address);
        
        redirectAttributes.addFlashAttribute("successMessage", "Alamat berhasil disimpan!");
        return "redirect:/admin/users/" + userId + "/addresses";
    }

    @GetMapping("/delete/{addressId}")
    public String deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            RedirectAttributes redirectAttributes) {
        
        addressRepository.deleteById(addressId);
        redirectAttributes.addFlashAttribute("successMessage", "Alamat berhasil dihapus!");
        return "redirect:/admin/users/" + userId + "/addresses";
    }
}
