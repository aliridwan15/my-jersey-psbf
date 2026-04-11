package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o")
    Double sumTotalRevenue();
    
    List<Order> findTop5ByOrderByOrderDateDesc();
}
