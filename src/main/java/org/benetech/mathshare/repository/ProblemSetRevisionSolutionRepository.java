package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemSetRevisionSolutionRepository extends JpaRepository<ProblemSetRevisionSolution, Integer>,
        PagingAndSortingRepository<ProblemSetRevisionSolution, Integer> {

    ProblemSetRevisionSolution findOneByEditCode(long editCode);

    List<ProblemSetRevisionSolution> findAllByUserId(String userId, Pageable pageable);

    List<ProblemSetRevisionSolution> findAllByUserIdAndArchiveMode(String userId, String archiveMode, Pageable pageable);
}
