package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
}
