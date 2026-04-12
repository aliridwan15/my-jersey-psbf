package myjerseyy.psbf_jersey.repository;

import myjerseyy.psbf_jersey.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
}
