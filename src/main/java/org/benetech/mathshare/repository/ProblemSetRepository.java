package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemSetRepository extends JpaRepository<ProblemSet, Long> {
    ProblemSet findOneByEditCode(Long editCode);
}
