package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.Address;
import myjerseyy.psbf_jersey.entity.User;
import myjerseyy.psbf_jersey.repository.AddressRepository;
import myjerseyy.psbf_jersey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String profilePage(Model model, HttpSession session) {
        User currentUser = getUserFromSession(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        model.addAttribute("addresses", addressRepository.findByUser_Id(currentUser.getId()));
        
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("name") String name,
                               @RequestParam("username") String username,
                               @RequestParam(value = "gender", required = false) String gender,
                               @RequestParam(value = "address", required = false) String basicAddress,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        user.setName(name);
        user.setUsername(username);
        user.setGender(gender != null ? gender : "");
        user.setAddress(basicAddress != null ? basicAddress : "");
        session.setAttribute("username", username);
        
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Data profil berhasil diperbarui!");
        
        return "redirect:/profile";
    }

    @PostMapping("/update-photo")
    public String updatePhoto(@RequestParam("photo") MultipartFile file, 
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "File foto tidak boleh kosong!");
            return "redirect:/profile";
        }
        
        User user = getUserFromSession(session);
        if (user != null) {
            try {
                user.setProfilePicture(file.getBytes());
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("successMessage", "Foto profil berhasil diperbarui!");
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengupload foto!");
                e.printStackTrace();
            }
        }
        
        return "redirect:/profile";
    }

    @PostMapping("/delete-photo")
    public String deletePhoto(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        if (user != null) {
            user.setProfilePicture(null);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Foto profil berhasil dihapus!");
        }
        return "redirect:/profile";
    }

    @PostMapping("/address/save")
    public String saveAddress(@ModelAttribute Address address,
                             @RequestParam(value = "isDefault", required = false) Boolean isDefault,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (isDefault != null && isDefault) {
            List<Address> existingAddresses = addressRepository.findByUser_Id(user.getId());
            for (Address existing : existingAddresses) {
                existing.setIsDefault(false);
                addressRepository.save(existing);
            }
        }
        
        if (address.getId() != null) {
            Optional<Address> existingAddr = addressRepository.findById(address.getId());
            if (existingAddr.isPresent()) {
                Address addr = existingAddr.get();
                addr.setTitle(address.getTitle());
                addr.setFullAddress(address.getFullAddress());
                addr.setCity(address.getCity());
                addr.setPostalCode(address.getPostalCode());
                addr.setIsDefault(isDefault != null && isDefault);
                addressRepository.save(addr);
            }
        } else {
            address.setUser(user);
            address.setIsDefault(isDefault != null && isDefault);
            addressRepository.save(address);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Alamat berhasil disimpan!");
        return "redirect:/profile";
    }

    @GetMapping("/address/delete/{id}")
    public String deleteAddress(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        Optional<Address> addressOpt = addressRepository.findById(id);
        if (addressOpt.isPresent() && addressOpt.get().getUser().getId().equals(user.getId())) {
            addressRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Alamat berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Alamat tidak ditemukan!");
        }
        
        return "redirect:/profile";
    }

    @GetMapping("/address/set-default/{id}")
    public String setDefaultAddress(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        Optional<Address> addressOpt = addressRepository.findById(id);
        if (addressOpt.isPresent() && addressOpt.get().getUser().getId().equals(user.getId())) {
            List<Address> allAddresses = addressRepository.findByUser_Id(user.getId());
            for (Address addr : allAddresses) {
                addr.setIsDefault(addr.getId().equals(id));
                addressRepository.save(addr);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Alamat utama berhasil diubah!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Alamat tidak ditemukan!");
        }
        
        return "redirect:/profile";
    }

    @GetMapping("/address/edit/{id}")
    @ResponseBody
    public Address getAddress(@PathVariable Long id, HttpSession session) {
        User user = getUserFromSession(session);
        if (user != null) {
            Optional<Address> addressOpt = addressRepository.findById(id);
            if (addressOpt.isPresent() && addressOpt.get().getUser().getId().equals(user.getId())) {
                return addressOpt.get();
            }
        }
        return null;
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = getUserFromSession(session);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "Password lama tidak sesuai");
            return "redirect:/profile";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Password baru dan konfirmasi password tidak cocok");
            return "redirect:/profile";
        }
        
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("passwordError", "Password minimal 6 karakter");
            return "redirect:/profile";
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diperbarui!");
        
        return "redirect:/profile";
    }

    @GetMapping("/photo/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable Long userId,
                                                  @RequestParam(required = false) Long t) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            byte[] profilePicture = user.getProfilePicture();
            
            if (profilePicture != null && profilePicture.length > 0) {
                HttpHeaders headers = new HttpHeaders();
                String contentType = detectImageType(profilePicture);
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentLength(profilePicture.length);
                headers.setCacheControl("no-store, no-cache, must-revalidate, proxy-revalidate");
                headers.setPragma("no-cache");
                headers.setExpires(0);
                headers.setETag("\"" + userId + "-" + profilePicture.length + "\"");
                return ResponseEntity.ok().headers(headers).body(profilePicture);
            }
        }
        
        return getDefaultPhoto();
    }
    
    private String detectImageType(byte[] bytes) {
        if (bytes == null || bytes.length < 3) {
            return "image/jpeg";
        }
        
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
            return "image/jpeg";
        }
        if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E) {
            return "image/png";
        }
        if (bytes[0] == (byte) 0x47 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46) {
            return "image/gif";
        }
        if (bytes.length >= 12 && bytes[0] == (byte) 0x52 && bytes[1] == (byte) 0x49 && 
            bytes[2] == (byte) 0x46 && bytes[3] == (byte) 0x46 && 
            bytes[8] == (byte) 0x57 && bytes[9] == (byte) 0x45 && 
            bytes[10] == (byte) 0x42 && bytes[11] == (byte) 0x50) {
            return "image/webp";
        }
        
        return "image/jpeg";
    }

    private ResponseEntity<byte[]> getDefaultPhoto() {
        try {
            Resource resource = new ClassPathResource("static/images/default-profile.png");
            if (resource.exists()) {
                byte[] defaultPhoto = Files.readAllBytes(resource.getFile().toPath());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.setContentLength(defaultPhoto.length);
                headers.setCacheControl("no-store, no-cache, must-revalidate");
                return ResponseEntity.ok().headers(headers).body(defaultPhoto);
            }
        } catch (IOException e) {
            System.err.println("Failed to load default profile photo: " + e.getMessage());
        }
        
        byte[] placeholder = generatePlaceholderImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl("no-store, no-cache, must-revalidate");
        return ResponseEntity.ok().headers(headers).body(placeholder);
    }

    private byte[] generatePlaceholderImage() {
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='128' height='128' viewBox='0 0 128 128'>" +
                "<rect fill='%23547792' width='128' height='128' rx='64'/>" +
                "<text x='64' y='72' text-anchor='middle' fill='white' font-family='Arial' font-size='48' font-weight='bold'>AR</text>" +
                "</svg>";
        return svg.getBytes();
    }

    private User getUserFromSession(HttpSession session) {
        String username = (String) session.getAttribute("username");
        
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }
        
        return null;
    }
}
