package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.StoreProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreProfileRepository extends JpaRepository<StoreProfile, Long> {
}
