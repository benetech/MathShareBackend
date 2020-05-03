package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemStepRepository extends JpaRepository<ProblemStep, Integer> {
    List<ProblemStep> findAllByProblemOrderByIdAsc(Problem problem);
}
