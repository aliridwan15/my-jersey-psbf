package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Jersey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JerseyRepository extends JpaRepository<Jersey, Long> {
    List<Jersey> findByNameContainingIgnoreCase(String name);
    List<Jersey> findByTeamNameContainingIgnoreCase(String teamName);
    
    List<Jersey> findTop8ByOrderByIdDesc();
}
