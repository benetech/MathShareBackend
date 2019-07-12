package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemSetRevisionSolutionRepository extends JpaRepository<ProblemSetRevisionSolution, Integer> {

    ProblemSetRevisionSolution findOneByEditCode(long editCode);
}
