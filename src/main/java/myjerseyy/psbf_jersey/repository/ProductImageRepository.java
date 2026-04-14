package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByJerseyIdOrderByIsPrimaryDescCreatedAtAsc(Long jerseyId);
}
