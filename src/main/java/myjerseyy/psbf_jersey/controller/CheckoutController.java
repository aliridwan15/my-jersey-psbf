package myjerseyy.psbf_jersey.controller;

import jakarta.servlet.http.HttpSession;
import myjerseyy.psbf_jersey.entity.*;
import myjerseyy.psbf_jersey.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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
        
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double subtotal = 0;
        for (Cart item : cartItems) {
            if (item.getJersey() != null && item.getJersey().getPrice() != null) {
                subtotal += item.getJersey().getPrice() * item.getQuantity();
            }
        }

        List<Address> addresses = addressRepository.findByUser_Id(user.getId());
        
        Address defaultAddress = null;
        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                defaultAddress = addr;
                break;
            }
        }
        if (defaultAddress == null && !addresses.isEmpty()) {
            defaultAddress = addresses.get(0);
        }

        List<Courier> couriers = courierRepository.findByIsActiveTrue();
        if (couriers == null) couriers = new ArrayList<>();
        
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByIsActiveTrue();
        if (paymentMethods == null) paymentMethods = new ArrayList<>();
        
        List<PromoCode> activePromos = promoCodeRepository.findByIsActiveTrue();
        if (activePromos == null) activePromos = new ArrayList<>();

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

    @GetMapping("/checkout/confirmation")
    public String checkoutConfirmation(
            @RequestParam Long addressId,
            @RequestParam Long courierId,
            @RequestParam Long paymentMethodId,
            @RequestParam(defaultValue = "0") double shippingCost,
            @RequestParam(defaultValue = "0") double discountAmount,
            @RequestParam(defaultValue = "0") double totalAmount,
            @RequestParam(required = false) String promoCode,
            Model model,
            HttpSession session) {
        
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double subtotal = 0;
        for (Cart item : cartItems) {
            if (item.getJersey() != null && item.getJersey().getPrice() != null) {
                subtotal += item.getJersey().getPrice() * item.getQuantity();
            }
        }

        Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findById(paymentMethodId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("addressId", addressId);
        model.addAttribute("courierId", courierId);
        model.addAttribute("paymentMethodId", paymentMethodId);
        model.addAttribute("paymentMethod", paymentMethod.orElse(null));
        model.addAttribute("shippingCost", shippingCost);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("promoCode", promoCode != null ? promoCode : "");

        return "checkout-confirmation";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(
            @RequestParam Long addressId,
            @RequestParam Long courierId,
            @RequestParam Long paymentMethodId,
            @RequestParam(defaultValue = "0") double shippingCost,
            @RequestParam(defaultValue = "0") double discountAmount,
            @RequestParam(defaultValue = "0") double totalAmount,
            @RequestParam(required = false) String promoCode,
            @RequestParam("paymentProof") MultipartFile paymentProof,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Keranjang kosong!");
            return "redirect:/cart";
        }

        if (paymentProof.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Wajib upload bukti transfer!");
            return "redirect:/checkout/confirmation?addressId=" + addressId + 
                   "&courierId=" + courierId + "&paymentMethodId=" + paymentMethodId +
                   "&shippingCost=" + shippingCost + "&discountAmount=" + discountAmount +
                   "&totalAmount=" + totalAmount + "&promoCode=" + (promoCode != null ? promoCode : "");
        }

        try {
            // Convert image to Base64
            String proofBase64 = Base64.getEncoder().encodeToString(paymentProof.getBytes());

            // Calculate subtotal
            double subtotal = 0;
            for (Cart item : cartItems) {
                if (item.getJersey() != null && item.getJersey().getPrice() != null) {
                    subtotal += item.getJersey().getPrice() * item.getQuantity();
                }
            }

            // Create Order
            Order order = new Order();
            order.setCustomer(user);
            order.setOrderDate(LocalDateTime.now());
            order.setTotalPrice(subtotal);
            order.setShippingCost(shippingCost);
            order.setDiscountAmount(discountAmount);
            order.setFinalPrice(totalAmount);
            order.setStatus(OrderStatus.PENDING);

            // Set Courier
            Optional<Courier> courierOpt = courierRepository.findById(courierId);
            if (courierOpt.isPresent()) {
                order.setCourier(courierOpt.get());
                order.setCourierName(courierOpt.get().getName());
            }

            // Set Address
            Optional<Address> addressOpt = addressRepository.findById(addressId);
            if (addressOpt.isPresent()) {
                order.setAddress(addressOpt.get());
            }

            // Apply promo code if exists
            if (promoCode != null && !promoCode.isEmpty()) {
                Optional<PromoCode> promo = promoCodeRepository.findValidPromoCode(promoCode, java.time.LocalDate.now());
                if (promo.isPresent()) {
                    order.setPromoCode(promo.get());
                }
            }

            // Create OrderItems from Cart
            for (Cart cart : cartItems) {
                OrderItem item = new OrderItem();
                item.setJersey(cart.getJersey());
                item.setSize(cart.getSize());
                item.setQuantity(cart.getQuantity());
                item.setPrice(cart.getJersey().getPrice());
                order.addItem(item);
            }

            // Save order
            order = orderRepository.save(order);

            // Get payment method
            Optional<PaymentMethod> pmOpt = paymentMethodRepository.findById(paymentMethodId);
            String paymentMethodName = pmOpt.map(PaymentMethod::getProviderName).orElse("Unknown");

            // Create Payment record
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentMethod(paymentMethodName);
            payment.setAmount(totalAmount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setProofImage(proofBase64);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            // Clear cart after successful order
            cartRepository.deleteAll(cartItems);
            session.setAttribute("cartCount", 0);

            // Store order ID for success page
            session.setAttribute("lastOrderId", order.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Pesanan berhasil dibuat! Silakan selesaikan pembayaran.");
            return "redirect:/order/success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memproses pesanan: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order/success")
    public String orderSuccess(Model model, HttpSession session) {
        Long orderId = (Long) session.getAttribute("lastOrderId");
        if (orderId == null) {
            return "redirect:/";
        }
        
        model.addAttribute("orderId", orderId);
        return "order-success";
    }
}
