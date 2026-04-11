package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}