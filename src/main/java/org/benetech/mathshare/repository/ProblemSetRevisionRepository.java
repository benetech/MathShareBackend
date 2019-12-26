package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemSetRevisionRepository extends JpaRepository<ProblemSetRevision, Integer> {

    ProblemSetRevision findOneByProblemSetAndReplacedBy(ProblemSet problemSet, ProblemSetRevision replacedBy);

    ProblemSetRevision findOneByShareCode(long code);

    ProblemSetRevision findFirstByOrderByDateCreatedAsc();

    List<ProblemSetRevision> findAllByIsExample(boolean isExample);

    @Modifying
    @Query("update ProblemSetRevision p set p.isExample = ?2 where p.shareCode = ?1")
    Integer setIsExampleForProblemSetRevision(long code, boolean isExample);
}
