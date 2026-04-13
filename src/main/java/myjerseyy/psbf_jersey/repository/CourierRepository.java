package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByIsActiveTrue();
}
