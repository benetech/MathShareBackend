package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ReviewSolutionRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSolutionRevisionRepository extends JpaRepository<ReviewSolutionRevision, Integer> {

    List<ReviewSolutionRevision> findAllByReviewCode(Long reviewCode);
}
