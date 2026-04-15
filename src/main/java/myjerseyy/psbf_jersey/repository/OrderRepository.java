package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o WHERE o.finalPrice IS NOT NULL")
    Double sumTotalRevenue();
    
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o")
    Double sumTotalRevenueOriginal();
    
    List<Order> findTop5ByOrderByOrderDateDesc();
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.promoCode")
    List<Order> findAllWithPromoCode();
    
    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.promoCode", 
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o")
    Page<Order> findAllWithPromoCode(Pageable pageable);
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.status = :status")
    List<Order> findByStatusWithItems(@Param("status") myjerseyy.psbf_jersey.entity.OrderStatus status);
    
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long userId);
}
