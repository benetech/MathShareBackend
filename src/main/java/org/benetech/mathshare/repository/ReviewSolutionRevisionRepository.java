package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.benetech.mathshare.model.entity.ReviewSolutionRevision;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSolutionRevisionRepository extends JpaRepository<ReviewSolutionRevision, Integer> {

    List<ReviewSolutionRevision> findAllByReviewCode(Long reviewCode);

    List<ReviewSolutionRevision> findAllByProblemSetRevisionSolutionAndInactive(
            ProblemSetRevisionSolution problemSetRevisionSolution, boolean inactive);

    ReviewSolutionRevision findOneBySolutionRevision(SolutionRevision solutionRevision);

    @Modifying
    @Query("update ReviewSolutionRevision r set r.inactive = true where r.problemSetRevisionSolution = ?1")
    int setAllReviewSolutionRevisionsInactiveFor(ProblemSetRevisionSolution problemSetRevisionSolution);
}
