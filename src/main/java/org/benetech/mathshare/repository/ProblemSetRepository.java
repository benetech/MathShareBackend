package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemSetRepository extends JpaRepository<ProblemSet, Integer>,
        PagingAndSortingRepository<ProblemSet, Integer> {
    ProblemSet findOneByEditCode(long editCode);

    List<ProblemSet> findAllByUserIdAndArchiveMode(String userId, String archiveMode, Pageable pageable);
}
