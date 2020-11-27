package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemSetRevisionSolutionRepository extends JpaRepository<ProblemSetRevisionSolution, Integer>,
        PagingAndSortingRepository<ProblemSetRevisionSolution, Integer> {

    ProblemSetRevisionSolution findOneByEditCode(long editCode);

    List<ProblemSetRevisionSolution> findAllByUserId(String userId, Pageable pageable);

    List<ProblemSetRevisionSolution> findAllByUserIdAndArchiveMode(String userId, String archiveMode,
            Pageable pageable);

    @Query("SELECT p FROM ProblemSetRevisionSolution p WHERE p.userId = :userId AND (:archiveMode IS NULL"
            + " OR p.archiveMode = :archiveMode) AND (:archiveMode IS NOT NULL OR p.archiveMode IS NULL)"
            + " AND (:id < 1 OR p.id < :id)")
    List<ProblemSetRevisionSolution> findLatestSliceByUserIdAndArchiveMode(@Param("userId") String userId,
            @Param("archiveMode") String archiveMode, @Param("id") Integer id, Pageable pageable);
}
