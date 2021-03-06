package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemSolutionRepository extends JpaRepository<ProblemSolution, Integer> {

    ProblemSolution findOneByEditCode(long editCode);
}
