package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRevisionRepository extends JpaRepository<SolutionRevision, Integer> {

    SolutionRevision findOneByShareCode(long shareCode);

    SolutionRevision findAllByProblemSolutionAndReplacedBy(ProblemSolution problemSolution, SolutionRevision replacedBy);
}
