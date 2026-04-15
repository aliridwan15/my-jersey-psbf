package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.*;
import myjerseyy.psbf_jersey.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    private User getUserFromSession(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            return userOpt.orElse(null);
        }
        return null;
    }

    @GetMapping("/checkout")
    public String checkoutPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String success,
            Model model,
            HttpSession session) {
        
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double subtotal = 0;
        for (Cart item : cartItems) {
            subtotal += item.getJersey().getPrice() * item.getQuantity();
        }

        List<Address> addresses = addressRepository.findByUser_Id(user.getId());
        Address defaultAddress = addresses.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                .findFirst()
                .orElse(addresses.isEmpty() ? null : addresses.get(0));

        List<Courier> couriers = courierRepository.findByIsActiveTrue();
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByIsActiveTrue();
        List<PromoCode> activePromos = promoCodeRepository.findByIsActiveTrue();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("addresses", addresses);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("couriers", couriers);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("activePromos", activePromos);
        model.addAttribute("error", error);
        model.addAttribute("success", success);

        return "checkout";
    }
}
