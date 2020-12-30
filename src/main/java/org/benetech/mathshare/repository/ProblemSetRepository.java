package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemSetRepository extends JpaRepository<ProblemSet, Integer>,
        PagingAndSortingRepository<ProblemSet, Integer> {
    ProblemSet findOneByEditCode(long editCode);

    List<ProblemSet> findAllByUserIdAndArchiveMode(String userId, String archiveMode, Pageable pageable);

    @Query("SELECT p FROM ProblemSet p WHERE p.userId = :userId AND (:archiveMode IS NULL"
    + " OR p.archiveMode = :archiveMode) AND (:archiveMode IS NOT NULL OR p.archiveMode IS NULL)"
    + " AND (:id < 1 OR p.id < :id)")
    List<ProblemSet> findLatestSliceByUserIdAndArchiveMode(@Param("userId") String userId,
    @Param("archiveMode") String archiveMode, @Param("id") Integer id, Pageable pageable);
}
