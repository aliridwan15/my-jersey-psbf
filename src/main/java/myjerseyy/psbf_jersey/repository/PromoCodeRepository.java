package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    
    @Query("SELECT p FROM PromoCode p WHERE p.code = :code AND p.isActive = true AND p.startDate <= :today AND p.endDate >= :today")
    Optional<PromoCode> findValidPromoCode(@Param("code") String code, @Param("today") LocalDate today);
    
    Optional<PromoCode> findByCode(String code);
}
