package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Payment;
import myjerseyy.psbf_jersey.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPaymentStatus(PaymentStatus status);
    Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);
    Optional<Payment> findByOrderId(Long orderId);
}
